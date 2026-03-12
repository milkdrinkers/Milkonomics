package io.github.milkdrinkers.milkonomicsplugin.command;

import com.destroystokyo.paper.profile.PlayerProfile;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.CommandAPIPaper;
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
    }

    private CommandAPICommand commandAdd() {

    }

    private CommandAPICommand commandRemove() {

    }

    private CommandAPICommand commandReset() {

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
