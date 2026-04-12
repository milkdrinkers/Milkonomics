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
import org.bukkit.command.CommandSender;

import java.math.BigDecimal;

final class AdminCommand extends Command {
    private final AbstractMilkonomics plugin;

    public AdminCommand(AbstractMilkonomics plugin) {
        this.plugin = plugin;
    }

    public CommandAPICommand command() {
        return new CommandAPICommand("admin")
            .withSubcommands(
                commandAdd(),
                commandRemove(),
                commandReset()
            )
            .withPermission("milkonomics.command.admin");
    }

    private CommandAPICommand commandAdd() {
        return new CommandAPICommand("add")
            .withArguments(new PlayerProfileArgument("player"), new DoubleArgument("amount"))
            .withPermission("milkonomics.command.admin.add")
            .executes(this::executorAdd);
    }

    private CommandAPICommand commandRemove() {
        return new CommandAPICommand("remove")
            .withArguments(new PlayerProfileArgument("player"), new DoubleArgument("amount"))
            .withPermission("milkonomics.command.admin.remove")
            .executes(this::executorRemove);
    }

    private CommandAPICommand commandReset() {
        return new CommandAPICommand("reset")
            .withArguments(new PlayerProfileArgument("player"))
            .withPermission("milkonomics.command.admin.reset")
            .executes(this::executorReset);
    }

    private void executorAdd(CommandSender sender, CommandArguments args) throws WrapperCommandSyntaxException {
        final PlayerProfile profile = args.getByClassOrDefault("player", PlayerProfile.class, null);
        if (profile == null)
            throw CommandAPIPaper.failWithAdventureComponent(ColorParser.of(Translation.of("commands.admin.add.player-not-found")).build());

        double amount = args.getByClassOrDefault("amount", Double.class, 0.0);

        final Account account = MilkonomicsAPI.getInstance().getAccountManager().getAccount(profile.getId()).orElseThrow(
            () -> CommandAPIPaper.failWithAdventureComponent(ColorParser.of(Translation.of("commands.admin.add.account-not-found")).build())
        );

        if (amount < 0.0)
            amount = 0.0;

        account.deposit(amount);
    }

    private void executorRemove(CommandSender sender, CommandArguments args) throws WrapperCommandSyntaxException {
        final PlayerProfile profile = args.getByClassOrDefault("player", PlayerProfile.class, null);
        if (profile == null)
            throw CommandAPIPaper.failWithAdventureComponent(ColorParser.of(Translation.of("commands.admin.remove.player-not-found")).build());

        double amount = args.getByClassOrDefault("amount", Double.class, 0.0);

        final Account account = MilkonomicsAPI.getInstance().getAccountManager().getAccount(profile.getId()).orElseThrow(
            () -> CommandAPIPaper.failWithAdventureComponent(ColorParser.of(Translation.of("commands.admin.remove.account-not-found")).build())
        );

        if (amount < 0.0)
            amount = 0.0;

        if (amount > account.getDouble())
            amount = account.getDouble();

        account.withdraw(amount);
    }

    private void executorReset(CommandSender sender, CommandArguments args) throws WrapperCommandSyntaxException {
        final PlayerProfile profile = args.getByClassOrDefault("player", PlayerProfile.class, null);
        if (profile == null)
            throw CommandAPIPaper.failWithAdventureComponent(ColorParser.of(Translation.of("commands.admin.reset.player-not-found")).build());

        final BigDecimal balance = Cfg.getDefaultDenominationCfg().defaultBalance;

        final Account account = MilkonomicsAPI.getInstance().getAccountManager().getAccount(profile.getId()).orElseThrow(
            () -> CommandAPIPaper.failWithAdventureComponent(ColorParser.of(Translation.of("commands.admin.reset.account-not-found")).build())
        );

        account.set(balance);
    }
}
