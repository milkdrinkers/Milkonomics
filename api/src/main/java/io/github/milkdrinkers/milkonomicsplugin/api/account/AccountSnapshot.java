package io.github.milkdrinkers.milkonomicsplugin.api.account;

import java.math.BigDecimal;
import java.util.UUID;

public interface AccountSnapshot {
    UUID uuid();

    String name();

    BigDecimal balance();
}
