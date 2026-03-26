package io.github.milkdrinkers.milkonomicsplugin.api;

import io.github.milkdrinkers.milkonomicsplugin.api.account.Account;
import org.jetbrains.annotations.ApiStatus;

public abstract class MilkonomicsAPI implements VaultProvider {
    private static MilkonomicsAPI INSTANCE;

    /**
     * Gets the instance of the MilkonomicsAPI.
     *
     * @return the instance of MilkonomicsAPI
     * @since 1.0.0
     */
    private static MilkonomicsAPI getInstance() {
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
    protected abstract AccountManager<Account> getAccountManager();

    /**
     * Gets the account save handler.
     *
     * @return the account save handler
     */
    protected abstract AccountSaveHandler getAccountSaveHandler();
}
