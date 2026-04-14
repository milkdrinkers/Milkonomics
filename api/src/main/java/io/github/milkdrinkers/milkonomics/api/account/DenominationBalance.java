package io.github.milkdrinkers.milkonomics.api.account;

import io.github.milkdrinkers.milkonomics.api.denomination.Denomination;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;

/**
 * Extends account balance operations to support multiple denominations.
 */
public interface DenominationBalance {
    /**
     * Gets the balance of a specific denomination.
     * @param denomination The denomination to get.
     * @return The balance for the specified denomination, or BigDecimal.ZERO if not set.
     */
    @NotNull BigDecimal get(@NotNull Denomination denomination);

    /**
     * Gets the balance of a specific denomination as a double.
     * @param denomination The denomination to get.
     * @return The balance for the specified denomination as a double, or 0.0 if not set.
     */
    default double getDouble(@NotNull Denomination denomination) {
        return get(denomination).doubleValue();
    }

    /**
     * Sets the balance of a specific denomination. This will overwrite any existing balance of that denomination.
     * @param denomination The denomination to set.
     * @param amount The amount to set of the specified denomination.
     * @return true if the balance was successfully set, false otherwise (e.g. if the amount is negative).
     */
    boolean set(@NotNull Denomination denomination, @NotNull BigDecimal amount);

    /**
     * Sets the balance of a specific denomination. This will overwrite any existing balance of that denomination.
     * @param denomination The denomination to set.
     * @param amount The amount to set of the specified denomination.
     * @return true if the balance was successfully set, false otherwise (e.g. if the amount is negative).
     */
    default boolean set(@NotNull Denomination denomination, double amount) {
        return set(denomination, BigDecimal.valueOf(amount));
    }

    /**
     * Checks if the balance has at least the specified amount of the given denomination.
     * @param denomination The denomination to check.
     * @param amount The amount to check of the specified denomination.
     * @return true if the balance has at least the specified amount of the given denomination, false otherwise.
     */
    boolean has(@NotNull Denomination denomination, @NotNull BigDecimal amount);

    /**
     * Checks if the balance has at least the specified amount of the given denomination.
     * @param denomination The denomination to check.
     * @param amount The amount to check of the specified denomination.
     * @return true if the balance has at least the specified amount of the given denomination, false otherwise.
     */
    default boolean has(@NotNull Denomination denomination, double amount) {
        return has(denomination, BigDecimal.valueOf(amount));
    }

    /**
     * Withdraws the specified amount from the balance for the given denomination. This will only succeed if the balance has at least the specified amount for that denomination.
     * @param denomination The denomination to withdraw for.
     * @param amount The amount to withdraw.
     * @return true if the withdrawal was successful, false otherwise (e.g. if the account does not have enough funds of the specified denomination).
     */
    boolean withdraw(@NotNull Denomination denomination, @NotNull BigDecimal amount);

    /**
     * Withdraws the specified amount from the balance for the given denomination. This will only succeed if the balance has at least the specified amount for that denomination.
     * @param denomination The denomination to withdraw for.
     * @param amount The amount to withdraw.
     * @return true if the withdrawal was successful, false otherwise (e.g. if the account does not have enough funds of the specified denomination).
     */
    default boolean withdraw(@NotNull Denomination denomination, double amount) {
        return withdraw(denomination, BigDecimal.valueOf(amount));
    }

    /**
     * Deposits the specified amount into the balance for the given denomination.
     * @param denomination The denomination to deposit for.
     * @param amount The amount to deposit.
     * @return true if the deposit was successful, false otherwise (e.g. if the amount is negative).
     */
    boolean deposit(@NotNull Denomination denomination, @NotNull BigDecimal amount);

    /**
     * Deposits the specified amount into the balance for the given denomination.
     * @param denomination The denomination to deposit for.
     * @param amount The amount to deposit.
     * @return true if the deposit was successful, false otherwise (e.g. if the amount is negative).
     */
    default boolean deposit(@NotNull Denomination denomination, double amount) {
        return deposit(denomination, BigDecimal.valueOf(amount));
    }
}