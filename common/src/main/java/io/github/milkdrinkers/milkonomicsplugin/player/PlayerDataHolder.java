package io.github.milkdrinkers.milkonomicsplugin.player;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class PlayerDataHolder {

    private static PlayerDataHolder INSTANCE;
    private final Map<UUID, PlayerData> playerData = new ConcurrentHashMap<>();

    private PlayerDataHolder() {
    }

    public static PlayerDataHolder getInstance() {
        if (INSTANCE == null)
            INSTANCE = new PlayerDataHolder();
        return INSTANCE;
    }

    @Nullable
    public PlayerData getPlayerData(UUID uuid) {
        return playerData.get(uuid);
    }

    @NotNull
    public Optional<PlayerData> getPlayerDataOptional(UUID uuid) {
        return Optional.ofNullable(getPlayerData(uuid));
    }

    public void setPlayerData(UUID uuid, PlayerData data) {
        playerData.put(uuid, data);
    }

    public void removePlayerData(UUID uuid) {
        playerData.remove(uuid);
    }

    public void clear() {
        playerData.clear();
    }

    public boolean isPlayerDataLoaded(UUID uuid) {
        return playerData.containsKey(uuid);
    }

}
