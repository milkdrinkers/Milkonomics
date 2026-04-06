package io.github.milkdrinkers.milkonomics.command;

import com.destroystokyo.paper.profile.PlayerProfile;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.CommandAPIPaper;
import dev.jorel.commandapi.arguments.PlayerProfileArgument;
import dev.jorel.commandapi.exceptions.WrapperCommandSyntaxException;
import dev.jorel.commandapi.executors.CommandArguments;
import io.github.milkdrinkers.colorparser.paper.ColorParser;
import io.github.milkdrinkers.milkonomics.AbstractMilkonomics;
import io.github.milkdrinkers.milkonomics.utility.Cfg;
import io.github.milkdrinkers.wordweaver.Translation;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Optional;

public class BalanceCommand {

    private AbstractMilkonomics plugin;

    public BalanceCommand(AbstractMilkonomics plugin) {
        this.plugin = plugin;

        command().register();
    }

    public CommandAPICommand command() {
        return new CommandAPICommand("balance")
            .withHelp("Check your own, or someone else's balance.", "Check your own, or someone else's balance.") // TODO translation
            .withPermission("milkonomics.command.balance")
            .withOptionalArguments(new PlayerProfileArgument("player"))
            .executes(this::executor);
    }

    private void executor(CommandSender sender, CommandArguments args) throws WrapperCommandSyntaxException {
        double balance;
        Optional<PlayerProfile> profileOptional = args.getOptionalByClass("player", PlayerProfile.class);
        if (profileOptional.isEmpty()) {
            if (!(sender instanceof Player player)) {
                throw CommandAPIPaper.failWithAdventureComponent(ColorParser.of(Translation.of("commands.balance.not-player")).build());
            }

            balance = plugin.getEconomyProvider().getBalance(player);
        } else {
            PlayerProfile profile = profileOptional.get();
            OfflinePlayer player = Bukkit.getOfflinePlayer(profile.getId());
            balance = plugin.getEconomyProvider().getBalance(player);
        }

        sender.sendMessage(ColorParser.of(Translation.of("commands.balance.balance"))
            .with("prefix", Cfg.getDefaultDenominationCfg().prefix)
            .with("amount", String.valueOf(balance))
            .with("suffix", Cfg.getDefaultDenominationCfg().suffix)
            .build());
    }

}
