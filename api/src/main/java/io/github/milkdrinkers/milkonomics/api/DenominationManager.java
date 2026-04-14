package io.github.milkdrinkers.milkonomics.api;

import io.github.milkdrinkers.milkonomics.api.denomination.Denomination;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnmodifiableView;

import java.math.BigDecimal;
import java.util.Map;

/**
 * The DenominationManager interface provides access to everything denominations related.
 */
public interface DenominationManager {
    /**
     * Gets the default denomination of the plugin/mod.
     * @return the default denomination
     */
    @NotNull Denomination getDefaultDenomination();

    /**
     * Gets a denomination by its ID.
     * @param id the ID of the denomination to get
     * @return the denomination with the given ID, or null if no such denomination exists
     */
    @Nullable Denomination getDenomination(@NotNull String id);

    /**
     * Gets a immutable map of all denominations, mapped by theird id to denomination.
     * @return an immutable map of all denominations, mapped by their id to denomination
     */
    @NotNull
    @UnmodifiableView
    Map<String, Denomination> getAllDenominations();


    /**
     * Gets a immutable map of all default balances for denominations, mapped by their object to default balance.
     * @return an immutable map of all default balances for denominations
     */
    @NotNull
    @UnmodifiableView
    Map<Denomination, BigDecimal> getDenominationsDefault();

    /**
     * Gets a immutable map of all default balances for denominations, mapped by their id to default balance.
     * @return an immutable map of all default balances for denominations
     */
    @NotNull
    @UnmodifiableView
    Map<String, BigDecimal> getDenominationsDefaults();
}
