package io.github.milkdrinkers.milkonomics.api.account;

import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;

/**
 * This interface provides methods for all balance related methods accounts implement.
 */
public interface AccountBalance {
    /**
     * Gets the current balance of the account using the default denomination.
     *
     * @return The current balance of the account.
     */
    @NotNull BigDecimal get();

    /**
     * Gets the current balance of the account as a double using the default denomination. This is equivalent to {@link #get()}.
     *
     * @return The current balance of the account as a double.
     */
    default double getDouble() {
        return get().doubleValue();
    }

    /**
     * Sets the balance of the account to the specified value using the default denomination.
     *
     * @param amount The new balance of the account. Must be non-negative.
     * @return True if the balance was successfully set, false otherwise.
     */
    boolean set(@NotNull BigDecimal amount);

    /**
     * Sets the balance of the account to the specified value using the default denomination.
     *
     * @param amount The new balance of the account. Must be non-negative.
     * @return True if the balance was successfully set, false otherwise.
     */
    default boolean set(double amount) {
        return set(BigDecimal.valueOf(amount));
    }

    /**
     * Checks if the account has at least the specified amount of money using the default denomination.
     *
     * @param amount The amount to check for. Must be non-negative.
     * @return True if the account has at least the specified amount of money, false otherwise.
     */
    boolean has(@NotNull BigDecimal amount);

    /**
     * Checks if the account has at least the specified amount of money using the default denomination.
     *
     * @param amount The amount to check for. Must be non-negative.
     * @return True if the account has at least the specified amount of money, false otherwise.
     */
    default boolean has(double amount) {
        return has(BigDecimal.valueOf(amount));
    }

    /**
     * Withdraws the specified amount of money from the account using the default denomination. The account must have at least the specified amount of money for this to succeed.
     *
     * @param amount The amount to withdraw. Must be non-negative.
     * @return True if the withdrawal was successful, false otherwise.
     */
    boolean withdraw(@NotNull BigDecimal amount);

    /**
     * Withdraws the specified amount of money from the account using the default denomination. The account must have at least the specified amount of money for this to succeed.
     *
     * @param amount The amount to withdraw. Must be non-negative.
     * @return True if the withdrawal was successful, false otherwise.
     */
    @SuppressWarnings("UnusedReturnValue")
    default boolean withdraw(double amount) {
        return withdraw(BigDecimal.valueOf(amount));
    }

    /**
     * Deposits the specified amount of money into the account using the default denomination. The account must be able to accept the specified amount of money for this to succeed (e.g. it must not exceed the maximum balance).
     *
     * @param amount The amount to deposit. Must be non-negative.
     * @return True if the deposit was successful, false otherwise.
     */
    boolean deposit(@NotNull BigDecimal amount);

    /**
     * Deposits the specified amount of money into the account using the default denomination. The account must be able to accept the specified amount of money for this to succeed (e.g. it must not exceed the maximum balance).
     *
     * @param amount The amount to deposit. Must be non-negative.
     * @return True if the deposit was successful, false otherwise.
     */
    @SuppressWarnings("UnusedReturnValue")
    default boolean deposit(double amount) {
        return deposit(BigDecimal.valueOf(amount));
    }

    /**
     * Gets a snapshot of the account's current state, including its balance and any other relevant information. This snapshot can be used for saving the account to the database or for other purposes where a consistent view of the account's state is needed.
     *
     * @return A snapshot of the account's current state.
     */
    @NotNull AccountSnapshot getSnapshot();
}
