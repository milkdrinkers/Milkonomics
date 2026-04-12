package io.github.milkdrinkers.milkonomics.command;

import com.destroystokyo.paper.profile.PlayerProfile;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.CommandAPIPaper;
import dev.jorel.commandapi.arguments.DoubleArgument;
import dev.jorel.commandapi.arguments.PlayerProfileArgument;
import dev.jorel.commandapi.exceptions.WrapperCommandSyntaxException;
import dev.jorel.commandapi.executors.CommandArguments;
import io.github.milkdrinkers.colorparser.paper.ColorParser;
import io.github.milkdrinkers.milkonomics.AbstractMilkonomics;
import io.github.milkdrinkers.milkonomics.api.MilkonomicsAPI;
import io.github.milkdrinkers.milkonomics.api.account.Account;
import io.github.milkdrinkers.milkonomics.utility.Cfg;
import io.github.milkdrinkers.wordweaver.Translation;
import org.bukkit.entity.Player;

final class PayCommand extends Command {
    private final AbstractMilkonomics plugin;

    public PayCommand(AbstractMilkonomics plugin) {
        this.plugin = plugin;
    }

    public CommandAPICommand command() {
        return new CommandAPICommand("pay")
            .withArguments(
                new PlayerProfileArgument("player"),
                new DoubleArgument("amount"))
            .withPermission("milkonomics.command.pay")
            .executesPlayer(this::executor);
    }

    private void executor(Player sender, CommandArguments args) throws WrapperCommandSyntaxException {
        final double amount = args.getByClassOrDefault("amount", Double.class, 0.0);
        if (amount <= 0.0)
            throw CommandAPIPaper.failWithAdventureComponent(ColorParser.of(Translation.as("commands.pay.invalid-amount")).build());

        final PlayerProfile targetProfile = args.getByClassOrDefault("player", PlayerProfile.class, null);
        if (targetProfile == null)
            throw CommandAPIPaper.failWithAdventureComponent(ColorParser.of(Translation.as("commands.pay.player-not-found")).build());

        final Player target = plugin.getServer().getPlayer(targetProfile.getId());
        if (target == null)
            throw CommandAPIPaper.failWithAdventureComponent(ColorParser.of(Translation.as("commands.pay.player-not-found")).build());

        if (target == sender)
            throw CommandAPIPaper.failWithAdventureComponent(ColorParser.of(Translation.as("commands.pay.self-payment")).build());

        final Account originAccount = MilkonomicsAPI.getInstance().getAccountManager().getAccount(sender.getUniqueId()).orElseThrow(
            () -> CommandAPIPaper.failWithAdventureComponent(ColorParser.of(Translation.as("commands.pay.origin-account-not-found")).build())
        );

        final Account targetAccount = MilkonomicsAPI.getInstance().getAccountManager().getAccount(sender.getUniqueId()).orElseThrow(
            () -> CommandAPIPaper.failWithAdventureComponent(ColorParser.of(Translation.as("commands.pay.target-account-not-found")).build())
        );

        if (!targetAccount.isAcceptingTransactions())
            throw CommandAPIPaper.failWithAdventureComponent(ColorParser.of(Translation.as("commands.pay.target-not-accepting-payments")).with("player", target.displayName()).build());

        if (!originAccount.has(amount))
            throw CommandAPIPaper.failWithAdventureComponent(ColorParser.of(Translation.as("commands.pay.insufficient-funds")).build());

        originAccount.withdraw(amount);
        targetAccount.deposit(amount);

        sender.sendMessage(ColorParser.of(Translation.as("commands.pay.origin-success"))
            .with("prefix", Cfg.getDefaultDenominationCfg().prefix)
            .with("amount", String.valueOf(amount))
            .with("suffix", Cfg.getDefaultDenominationCfg().suffix)
            .with("player", target.displayName())
            .build());

        target.sendMessage(ColorParser.of(Translation.as("commands.pay.target-success"))
            .with("prefix", Cfg.getDefaultDenominationCfg().prefix)
            .with("amount", String.valueOf(amount))
            .with("suffix", Cfg.getDefaultDenominationCfg().suffix)
            .with("player", sender.displayName())
            .build());
    }
}
