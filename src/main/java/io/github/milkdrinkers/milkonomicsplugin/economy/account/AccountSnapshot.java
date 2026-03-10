package io.github.milkdrinkers.milkonomicsplugin.economy.account;

import java.math.BigDecimal;
import java.util.UUID;

public record AccountSnapshot(UUID uuid, String name, BigDecimal balance) {
    public static AccountSnapshot of(Account account) {
        return new AccountSnapshot(account.getUUID(), account.getName(), account.getBalance());
    }
}
