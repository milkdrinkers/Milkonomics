package io.github.milkdrinkers.milkonomics;

import io.github.milkdrinkers.milkonomics.api.AccountManager;
import io.github.milkdrinkers.milkonomics.api.AccountSaveHandler;
import io.github.milkdrinkers.milkonomics.api.account.Account;
import io.github.milkdrinkers.milkonomics.config.ConfigHandler;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public abstract class AbstractMilkonomics extends JavaPlugin {
    private static AbstractMilkonomics instance;

    /**
     * Gets plugin instance.
     *
     * @return the plugin instance
     */
    public static AbstractMilkonomics getInstance() {
        return AbstractMilkonomics.instance;
    }

    AbstractMilkonomics() {
        AbstractMilkonomics.instance = this;
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
    public abstract @NotNull io.github.milkdrinkers.milkonomics.api.DenominationManager getDenominationHandler();
}
