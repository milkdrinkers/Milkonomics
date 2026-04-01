package io.github.milkdrinkers.milkonomicsplugin.command;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.executors.CommandArguments;
import io.github.milkdrinkers.milkonomicsplugin.player.PlayerDataHolder;
import org.bukkit.entity.Player;

public class ToggleCommand {

    public ToggleCommand() {
        command().register();
    }

    public CommandAPICommand command() {
        return new CommandAPICommand("togglepayments")
            .withPermission("milkonomics.command.togglepayments")
            .executesPlayer(this::executor);
    }

    public void executor(Player sender, CommandArguments args) {
        PlayerDataHolder.getInstance().getPlayerData(sender.getUniqueId()).togglePayments();
    }

}
