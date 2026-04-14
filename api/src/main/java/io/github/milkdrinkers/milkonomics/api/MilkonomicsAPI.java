package io.github.milkdrinkers.milkonomics.api;

import io.github.milkdrinkers.milkonomics.api.account.Account;
import io.github.milkdrinkers.milkonomics.api.cache.Cache;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

/**
 * The MilkonomicsAPI class is the main entry point for accessing the Milkonomics API.
 */
public abstract class MilkonomicsAPI implements VaultProvider {
    private static MilkonomicsAPI INSTANCE;

    /**
     * Gets the instance of the MilkonomicsAPI.
     *
     * @return the instance of MilkonomicsAPI
     * @since 1.0.0
     */
    public static MilkonomicsAPI getInstance() {
        if (INSTANCE == null)
            throw new RuntimeException("Milkonomics API was accessed before being initialized!");
        return INSTANCE;
    }

    /**
     * Sets the instance of the MilkonomicsAPI.
     * This method is intended for internal use by the api provider only.
     *
     * @param api the instance of MilkonomicsAPI to set
     * @since 1.0.0
     */
    @ApiStatus.Internal
    protected static void setInstance(MilkonomicsAPI api) {
        INSTANCE = api;
    }

    /**
     * Gets the account manager.
     *
     * @return the account manager
     */
    public abstract @NotNull AccountManager<Account> getAccountManager();

    /**
     * Gets the account save handler.
     *
     * @return the account save handler
     */
    public abstract @NotNull AccountSaveHandler getAccountSaveHandler();

    /**
     * Gets the denomination manager.
     *
     * @return the denomination manager
     */
    public abstract @NotNull DenominationManager getDenominationManager();

    /**
     * Gets the cache.
     *
     * @return the cache
     */
    public abstract @NotNull Cache getCache();
}
