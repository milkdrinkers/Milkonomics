package io.github.milkdrinkers.milkonomics.api.account;

import io.github.milkdrinkers.milkonomics.api.denomination.Denomination;

import java.math.BigDecimal;

/**
 * Extends account balance operations to support multiple denominations.
 */
public interface DenominationBalance {
    BigDecimal get(Denomination denomination);

    default double getDouble(Denomination denomination) {
        return get(denomination).doubleValue();
    }

    boolean set(Denomination denomination, BigDecimal amount);

    default boolean set(Denomination denomination, double amount) {
        return set(denomination, BigDecimal.valueOf(amount));
    }

    boolean has(Denomination denomination, BigDecimal amount);

    default boolean has(Denomination denomination, double amount) {
        return has(denomination, BigDecimal.valueOf(amount));
    }

    boolean withdraw(Denomination denomination, BigDecimal amount);

    default boolean withdraw(Denomination denomination, double amount) {
        return withdraw(denomination, BigDecimal.valueOf(amount));
    }

    boolean deposit(Denomination denomination, BigDecimal amount);

    default boolean deposit(Denomination denomination, double amount) {
        return deposit(denomination, BigDecimal.valueOf(amount));
    }
}