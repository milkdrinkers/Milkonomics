package io.github.milkdrinkers.milkonomicsplugin.economy.account;

import java.math.BigDecimal;
import java.util.UUID;

public interface Account {
    UUID getUUID();
    String getName();

    BigDecimal getBalance();
    void setBalance(BigDecimal state);

    AccountSnapshot getSnapshot();
}
