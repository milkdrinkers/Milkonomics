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
import io.github.milkdrinkers.milkonomics.utility.Cfg;
import io.github.milkdrinkers.milkonomics.player.PlayerDataHolder;
import io.github.milkdrinkers.wordweaver.Translation;
import org.bukkit.entity.Player;

public class PayCommand {

    AbstractMilkonomics plugin;

    public PayCommand(AbstractMilkonomics plugin) {
        this.plugin = plugin;

        command().register();
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
        PlayerProfile profile = args.getByClassOrDefault("player", PlayerProfile.class, null);
        if (profile == null) throw CommandAPIPaper.failWithAdventureComponent(ColorParser.of(Translation.as("commands.pay.player-not-found")).build());

        Player target = plugin.getServer().getPlayer(profile.getId());
        double amount = args.getByClassOrDefault("amount", Double.class, 0.0);

        if (target == null)
            throw CommandAPIPaper.failWithAdventureComponent(ColorParser.of(Translation.as("commands.pay.player-not-found")).build());

        if (!PlayerDataHolder.getInstance().getPlayerData(target.getUniqueId()).isAcceptingPayments())
            throw CommandAPIPaper.failWithAdventureComponent(ColorParser.of(Translation.as("commands.pay.target-not-accepting-payments")).with("player", target.displayName()).build());

        if (target.getUniqueId() == sender.getUniqueId()) throw CommandAPIPaper.failWithAdventureComponent(ColorParser.of(Translation.as("commands.pay.self")).build());

        if (!plugin.getEconomyProvider().has(sender, amount)) throw CommandAPIPaper.failWithAdventureComponent(ColorParser.of(Translation.as("commands.pay.insufficient-funds")).build());

        plugin.getEconomyProvider().withdrawPlayer(sender, amount);
        plugin.getEconomyProvider().depositPlayer(plugin.getServer().getPlayer(target.getUniqueId()), amount);

        sender.sendMessage(ColorParser.of(Translation.as("commands.pay.success"))
            .with("prefix", Cfg.getDefaultDenominationCfg().prefix)
            .with("amount", String.valueOf(amount))
            .with("suffix", Cfg.getDefaultDenominationCfg().suffix)
            .with("player", target.displayName())
            .build());

        target.sendMessage(ColorParser.of(Translation.as("commands.pay.success-target"))
            .with("prefix", Cfg.getDefaultDenominationCfg().prefix)
            .with("amount", String.valueOf(amount))
            .with("suffix", Cfg.getDefaultDenominationCfg().suffix) // TODO
            .with("player", sender.displayName())
            .build());
    }
}
