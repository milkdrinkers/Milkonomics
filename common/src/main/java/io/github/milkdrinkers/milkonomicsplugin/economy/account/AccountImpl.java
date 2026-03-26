package io.github.milkdrinkers.milkonomicsplugin.economy.account;

import io.github.milkdrinkers.milkonomicsplugin.AbstractMilkonomicsPlugin;
import io.github.milkdrinkers.milkonomicsplugin.api.account.Account;

import java.math.BigDecimal;
import java.util.UUID;

public class AccountImpl extends Account {
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
    public io.github.milkdrinkers.milkonomicsplugin.api.account.AccountSnapshot getSnapshot() {
        return new AccountSnapshot(getUUID(), getName(), getBalance());
    }
}
