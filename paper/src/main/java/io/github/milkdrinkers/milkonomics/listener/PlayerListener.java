package io.github.milkdrinkers.milkonomics.listener;

import io.github.milkdrinkers.milkonomics.AbstractMilkonomics;
import io.github.milkdrinkers.milkonomics.api.MilkonomicsAPI;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;

import java.util.UUID;

public class PlayerListener implements Listener {
    private final AbstractMilkonomics plugin;

    public PlayerListener(AbstractMilkonomics plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    @SuppressWarnings("unused")
    public void onPlayerJoin(AsyncPlayerPreLoginEvent e) {
        final String playerName = e.getName();
        final UUID uuid = e.getUniqueId();

        if (MilkonomicsAPI.getInstance().getAccountManager().hasAccount(uuid)) {
            return;
        }

        MilkonomicsAPI.getInstance()
            .getAccountManager()
            .createAccount(uuid, playerName);
    }
}
