package io.github.milkdrinkers.milkonomicsplugin.economy.account;

import io.github.milkdrinkers.milkonomicsplugin.MilkonomicsPlugin;

import java.math.BigDecimal;
import java.util.UUID;

public class AccountImpl implements Account {
    private final UUID uuid;
    private final String name;
    private BigDecimal balance;

    public AccountImpl(UUID uuid, String name) {
        this.uuid = uuid;
        this.name = name;
        this.balance = new BigDecimal("0");
    }

    public AccountImpl(UUID uuid, String name, BigDecimal balance) {
        this.uuid = uuid;
        this.name = name;
        this.balance = balance;
    }

    @Override
    public UUID getUUID() {
        return uuid;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public BigDecimal getBalance() {
        return balance;
    }

    @Override
    public void setBalance(BigDecimal state) {
        balance = state;
        MilkonomicsPlugin.getInstance().getAccountSaveHandler().queue(this);
    }

    @Override
    public AccountSnapshot getSnapshot() {
        return AccountSnapshot.of(this);
    }
}
