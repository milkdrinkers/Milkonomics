package io.github.milkdrinkers.milkonomicsplugin.player;

import java.util.UUID;

public class PlayerData {

    private final UUID uuid;

    // Milkonomics data
    private boolean acceptingPayments = true;

    PlayerData(UUID uuid) {
        this.uuid = uuid;
    }

    PlayerData(UUID uuid, boolean acceptingPayments) {
        this.uuid = uuid;
        this.acceptingPayments = acceptingPayments;
    }

    public boolean isAcceptingPayments() {
        return acceptingPayments;
    }

    public void setAcceptingPayments(boolean acceptingPayments) {
        this.acceptingPayments = acceptingPayments;
    }

    public void disablePayments() {
        this.acceptingPayments = false;
    }

    public void enablePayments() {
        this.acceptingPayments = true;
    }

    public void togglePayments() {
        this.acceptingPayments = !this.acceptingPayments;
    }

    public UUID getUUID() {
        return uuid;
    }

    public boolean equals(PlayerData playerData) {
        return getUUID() == playerData.getUUID();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PlayerData playerData)) return false;
        return equals(playerData);
    }

    @Override
    public String toString() {
        return "PlayerData{" +
            "uuid=" + uuid +
            ", acceptingPayments=" + acceptingPayments +
            '}';
    }
}
