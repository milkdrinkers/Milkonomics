package io.github.milkdrinkers.milkonomics.api.account;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

/**
 * AccountSnapshot represents a snapshot of an account's state at a specific point in time.
 */
public interface AccountSnapshot {
    /**
     * Gets the unique identifier of the account.
     *
     * @return The UUID of the account.
     */
    @NotNull UUID uuid();

    /**
     * Gets the name of the account.
     *
     * @return The name of the account.
     */
    @NotNull String name();

    /**
     * Gets all the balances of the account for all denominations. The keys of the map are the denomination id's, and the values are the corresponding balances.
     *
     * @return A map containing the balances of the account for all denominations.
     */
    @NotNull @UnmodifiableView Map<String, BigDecimal> balances();

    /**
     * If the account is currently accepting transactions.
     * @return true if the account is accepting transactions, false otherwise.
     */
    boolean acceptingTransactions();
}
