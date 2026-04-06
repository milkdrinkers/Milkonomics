package io.github.milkdrinkers.milkonomicsplugin.api.account;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

public interface AccountSnapshot {
    UUID uuid();

    String name();

    Map<String, BigDecimal> balances();
}
