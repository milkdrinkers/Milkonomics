package io.github.milkdrinkers.milkonomics.event;

import io.github.milkdrinkers.milkonomics.player.PlayerData;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.UUID;

public class PlayerDataLoadedEvent extends Event {
    private static final HandlerList HANDLER_LIST = new HandlerList();

    private final @NotNull UUID uuid;
    private final @NotNull PlayerData data;

    public PlayerDataLoadedEvent(@NotNull UUID uuid, @NotNull PlayerData data) {
        Objects.requireNonNull(uuid, "UUID cannot be null in PlayerDataLoadedEvent");
        Objects.requireNonNull(data, "PlayerData cannot be null in PlayerDataLoadedEvent");
        this.uuid = uuid;
        this.data = data;
    }

    public UUID getUuid() {
        return uuid;
    }

    public PlayerData getPlayerData() {
        return data;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLER_LIST;
    }

    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }
}
