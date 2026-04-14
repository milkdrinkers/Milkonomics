package io.github.milkdrinkers.milkonomics;

import io.github.milkdrinkers.milkonomics.api.AccountManager;
import io.github.milkdrinkers.milkonomics.api.AccountSaveHandler;
import io.github.milkdrinkers.milkonomics.api.DenominationManager;
import io.github.milkdrinkers.milkonomics.api.MilkonomicsAPI;
import io.github.milkdrinkers.milkonomics.api.account.Account;
import io.github.milkdrinkers.milkonomics.api.cache.Cache;
import net.milkbowl.vault.economy.Economy;
import org.jspecify.annotations.NonNull;

class MilkonomicsAPIProvider extends MilkonomicsAPI implements Reloadable {
    private final Milkonomics plugin;

    MilkonomicsAPIProvider(Milkonomics plugin) {
        super();
        this.plugin = plugin;
        setInstance(this);
    }

    @Override
    public @NonNull AccountManager<Account> getAccountManager() {
        return plugin.getAccountManager();
    }

    @Override
    public @NonNull AccountSaveHandler getAccountSaveHandler() {
        return plugin.getAccountSaveHandler();
    }

    @Override
    public @NonNull DenominationManager getDenominationManager() {
        return plugin.getDenominationHandler();
    }

    @Override
    public @NonNull Cache getCache() {
        return plugin.getCache();
    }

    @Override
    public @NonNull Economy getEconomy() {
        return plugin.getEconomyProvider();
    }
}
