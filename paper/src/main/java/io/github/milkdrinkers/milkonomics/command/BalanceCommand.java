package io.github.milkdrinkers.milkonomics.command;

import com.destroystokyo.paper.profile.PlayerProfile;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.CommandAPIPaper;
import dev.jorel.commandapi.arguments.PlayerProfileArgument;
import dev.jorel.commandapi.exceptions.WrapperCommandSyntaxException;
import dev.jorel.commandapi.executors.CommandArguments;
import io.github.milkdrinkers.colorparser.paper.ColorParser;
import io.github.milkdrinkers.milkonomics.AbstractMilkonomics;
import io.github.milkdrinkers.milkonomics.api.MilkonomicsAPI;
import io.github.milkdrinkers.milkonomics.api.account.Account;
import io.github.milkdrinkers.milkonomics.utility.Cfg;
import io.github.milkdrinkers.wordweaver.Translation;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

final class BalanceCommand extends Command {
    private final AbstractMilkonomics plugin;

    public BalanceCommand(AbstractMilkonomics plugin) {
        this.plugin = plugin;
    }

    public CommandAPICommand command() {
        return new CommandAPICommand("balance")
            .withHelp("Check your own, or someone else's balance.", "Check your own, or someone else's balance.") // TODO translation
            .withPermission("milkonomics.command.balance")
            .withOptionalArguments(new PlayerProfileArgument("player"))
            .executes(this::executor)
            .executesPlayer(this::executorPlayer);
    }

    private void executor(CommandSender sender, CommandArguments args) throws WrapperCommandSyntaxException {
        final PlayerProfile profile = args.getOptionalByClass("player", PlayerProfile.class).orElseThrow(
            () -> CommandAPIPaper.failWithAdventureComponent(ColorParser.of(Translation.of("commands.balance.no-player-specified")).build())
        );

        final Account account = MilkonomicsAPI.getInstance().getAccountManager().getAccount(profile.getId()).orElseThrow(
            () -> CommandAPIPaper.failWithAdventureComponent(ColorParser.of(Translation.of("commands.balance.account-not-found")).build())
        );

        sender.sendMessage(ColorParser.of(Translation.of("commands.balance.balance"))
            .with("prefix", Cfg.getDefaultDenominationCfg().prefix)
            .with("amount", String.valueOf(account.get()))
            .with("suffix", Cfg.getDefaultDenominationCfg().suffix)
            .build());
    }

    private void executorPlayer(Player sender, CommandArguments args) throws WrapperCommandSyntaxException {
        final PlayerProfile profile = args.getOptionalByClass("player", PlayerProfile.class)
            .orElse(sender.getPlayerProfile());

        final Account account = MilkonomicsAPI.getInstance().getAccountManager().getAccount(profile.getId()).orElseThrow(
            () -> CommandAPIPaper.failWithAdventureComponent(ColorParser.of(Translation.of("commands.balance.account-not-found")).build())
        );

        sender.sendMessage(ColorParser.of(Translation.of("commands.balance.balance"))
            .with("prefix", Cfg.getDefaultDenominationCfg().prefix)
            .with("amount", String.valueOf(account.get()))
            .with("suffix", Cfg.getDefaultDenominationCfg().suffix)
            .build());
    }
}
