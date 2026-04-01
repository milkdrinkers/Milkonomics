package io.github.milkdrinkers.milkonomicsplugin.listener;

import io.github.milkdrinkers.milkonomicsplugin.cooldown.Cooldowns;
import io.github.milkdrinkers.milkonomicsplugin.database.Queries;
import io.github.milkdrinkers.milkonomicsplugin.event.PlayerDataLoadedEvent;
import io.github.milkdrinkers.milkonomicsplugin.player.PlayerData;
import io.github.milkdrinkers.milkonomicsplugin.player.PlayerDataBuilder;
import io.github.milkdrinkers.milkonomicsplugin.player.PlayerDataHolder;
import io.github.milkdrinkers.threadutil.Scheduler;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoinListener implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        final Player p = e.getPlayer();

        Scheduler
            .async(() -> {
                Queries.Cooldown.load(p).forEach((cooldownType, instant) -> {
                    Cooldowns.set(p, cooldownType, instant);
                });

                return loadPlayerData(p);
            })
            .sync(data -> {
                PlayerDataHolder.getInstance().setPlayerData(p.getUniqueId(), data);

                PlayerDataLoadedEvent event = new PlayerDataLoadedEvent(p.getUniqueId(), data);
                Bukkit.getPluginManager().callEvent(event);
            })
            .execute();
    }

    public PlayerData loadPlayerData(Player p) {
        final boolean acceptingPayments = Queries.Players.loadAcceptingPayments(p.getUniqueId());

        return new PlayerDataBuilder()
            .withUuid(p.getUniqueId())
            .withAcceptingPayments(acceptingPayments)
            .build();
    }

}
