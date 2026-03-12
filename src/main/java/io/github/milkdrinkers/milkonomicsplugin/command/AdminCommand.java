package io.github.milkdrinkers.milkonomicsplugin.command;

import com.destroystokyo.paper.profile.PlayerProfile;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.CommandAPIPaper;
import dev.jorel.commandapi.arguments.DoubleArgument;
import dev.jorel.commandapi.arguments.PlayerProfileArgument;
import dev.jorel.commandapi.exceptions.WrapperCommandSyntaxException;
import dev.jorel.commandapi.executors.CommandArguments;
import io.github.milkdrinkers.colorparser.paper.ColorParser;
import io.github.milkdrinkers.milkonomicsplugin.MilkonomicsPlugin;
import io.github.milkdrinkers.wordweaver.Translation;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import java.math.BigDecimal;

public class AdminCommand {

    private MilkonomicsPlugin plugin;

    public AdminCommand(MilkonomicsPlugin plugin) {
        this.plugin = plugin;

        new CommandAPICommand("money")
            .withAliases("milkonomicsadmin", "ma")
            .withSubcommands(
                commandAdd(),
                commandRemove(),
                commandReset()
            )
            .withPermission("milkonomics.command.admin")
            .register();
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
        PlayerProfile profile = args.getByClassOrDefault("player", PlayerProfile.class, null);
        if (profile == null) {
            throw CommandAPIPaper.failWithAdventureComponent(ColorParser.of(Translation.of("commands.admin.add.player-not-found")).build());
        }

        double amount = args.getByClassOrDefault("amount", Double.class, 0.0);

        plugin.getAccountManager().getEconomy().depositPlayer(Bukkit.getOfflinePlayer(profile.getUniqueId()), amount);
    }

    private void executorRemove(CommandSender sender, CommandArguments args) throws WrapperCommandSyntaxException {
        PlayerProfile profile = args.getByClassOrDefault("player", PlayerProfile.class, null);
        if (profile == null) {
            throw CommandAPIPaper.failWithAdventureComponent(ColorParser.of(Translation.of("commands.admin.remove.player-not-found")).build());
        }

        double amount = args.getByClassOrDefault("amount", Double.class, 0.0);

        plugin.getAccountManager().getEconomy().withdrawPlayer(Bukkit.getOfflinePlayer(profile.getUniqueId()), amount);
    }

    private void executorReset(CommandSender sender, CommandArguments args) throws WrapperCommandSyntaxException {
        PlayerProfile profile = args.getByClassOrDefault("player", PlayerProfile.class, null);
        if (profile == null) {
            throw CommandAPIPaper.failWithAdventureComponent(ColorParser.of(Translation.of("commands.admin.reset.player-not-found")).build());
        }

        BigDecimal balance = BigDecimal.valueOf(0); // TODO get default balance from config

        // Accessing the account directly rather than through EconomyImpl to make use of the "setBalance" method.
        plugin.getAccountManager().getAccount(profile.getId()).setBalance(balance);
    }

}
