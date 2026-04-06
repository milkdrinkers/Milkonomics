package io.github.milkdrinkers.milkonomics.player;

import javax.annotation.Nullable;
import java.util.UUID;

public class PlayerDataBuilder {
    private @Nullable UUID uuid;
    private boolean acceptingPayments = true;

    public PlayerDataBuilder withUuid(UUID uuid){
        this.uuid = uuid;
        return this;
    }

    public PlayerDataBuilder withAcceptingPayments(boolean acceptingPayments){
        this.acceptingPayments = acceptingPayments;
        return this;
    }

    public PlayerData build(){
        if (uuid == null){
            throw new IllegalStateException("Mising state UUID when creating PlayerData object.");
        }
        return new PlayerData(uuid, acceptingPayments);
    }

}
