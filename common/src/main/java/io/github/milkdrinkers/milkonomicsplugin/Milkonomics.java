package io.github.milkdrinkers.milkonomicsplugin;

import io.github.milkdrinkers.milkonomicsplugin.api.AccountManager;
import io.github.milkdrinkers.milkonomicsplugin.api.AccountSaveHandler;
import io.github.milkdrinkers.milkonomicsplugin.api.account.Account;
import io.github.milkdrinkers.milkonomicsplugin.config.ConfigHandler;
import io.github.milkdrinkers.milkonomicsplugin.economy.denomination.DenominationHandler;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public abstract class Milkonomics extends JavaPlugin {
    private static Milkonomics instance;

    /**
     * Gets plugin instance.
     *
     * @return the plugin instance
     */
    public static Milkonomics getInstance() {
        return Milkonomics.instance;
    }

    Milkonomics() {
        Milkonomics.instance = this;
    }

    /**
     * Gets config handler.
     *
     * @return the config handler
     */
    public abstract @NotNull ConfigHandler getConfigHandler();

    /**
     * Gets account manager.
     *
     * @return the account manager
     */
    public abstract @NotNull AccountManager<Account> getAccountManager();

    /**
     * Gets account save handler.
     *
     * @return the account save handler
     */
    public abstract @NotNull AccountSaveHandler getAccountSaveHandler();

    /**
    * Gets economy provider.
    *
    * @return the economy provider
    */
    public abstract @NotNull Economy getEconomyProvider();

    /**
     * Gets denomination handler.
     *
     * @return the denomination handler
     */
    public abstract @NotNull DenominationHandler getDenominationHandler();
}
