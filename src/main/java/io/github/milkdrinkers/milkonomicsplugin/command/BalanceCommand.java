package io.github.milkdrinkers.milkonomicsplugin.command;

import com.destroystokyo.paper.profile.PlayerProfile;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.CommandAPIPaper;
import dev.jorel.commandapi.arguments.PlayerProfileArgument;
import dev.jorel.commandapi.exceptions.WrapperCommandSyntaxException;
import dev.jorel.commandapi.executors.CommandArguments;
import io.github.milkdrinkers.colorparser.paper.ColorParser;
import io.github.milkdrinkers.milkonomicsplugin.MilkonomicsPlugin;
import io.github.milkdrinkers.wordweaver.Translation;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class BalanceCommand {

    private MilkonomicsPlugin plugin;

    public BalanceCommand(MilkonomicsPlugin plugin) {
        this.plugin = plugin;

        new CommandAPICommand("balance")
            .withHelp("Check your own, or someone else's balance.", "Check your own, or someone else's balance.") // TODO translation
            .withPermission("milkonomics.command.balance")
            .withOptionalArguments(new PlayerProfileArgument("player"))
            .executes(this::executor)
            .register();
    }

    private void executor(CommandSender sender, CommandArguments args) throws WrapperCommandSyntaxException {
        double balance;
        if (args.count() < 1) {
            if (!(sender instanceof Player player)) {
                throw CommandAPIPaper.failWithAdventureComponent(ColorParser.of(Translation.of("commands.balance.not-player")).build());
            }

            balance = plugin.getAccountManager().getEconomy().getBalance(player);
        } else {
            PlayerProfile profile = args.getByClassOrDefault("player", PlayerProfile.class, null);
            if (profile == null) {
                throw CommandAPIPaper.failWithAdventureComponent(ColorParser.of(Translation.of("commands.balance.player-not-found")).build());
            }
            OfflinePlayer player = Bukkit.getOfflinePlayer(profile.getUniqueId());
            balance = plugin.getAccountManager().getEconomy().getBalance(player);
        }

        sender.sendMessage(ColorParser.of(Translation.of("commands.balance.balance"))
            .with("amount", String.valueOf(balance))
            .with("symbol", "$") // TODO make configurable
            .build());
    }

}
