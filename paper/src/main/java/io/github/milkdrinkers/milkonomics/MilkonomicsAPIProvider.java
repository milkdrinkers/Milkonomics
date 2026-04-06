package io.github.milkdrinkers.milkonomics;

import io.github.milkdrinkers.milkonomics.api.AccountManager;
import io.github.milkdrinkers.milkonomics.api.AccountSaveHandler;
import io.github.milkdrinkers.milkonomics.api.MilkonomicsAPI;
import io.github.milkdrinkers.milkonomics.api.account.Account;
import net.milkbowl.vault.economy.Economy;

class MilkonomicsAPIProvider extends MilkonomicsAPI implements Reloadable {
    private final MilkonomicsPlugin plugin;

    MilkonomicsAPIProvider(MilkonomicsPlugin plugin) {
        super();
        this.plugin = plugin;
        setInstance(this);
    }

    @Override
    public AccountManager<Account> getAccountManager() {
        return plugin.getAccountManager();
    }

    @Override
    public AccountSaveHandler getAccountSaveHandler() {
        return plugin.getAccountSaveHandler();
    }

    @Override
    public Economy getEconomy() {
        return plugin.getEconomyProvider();
    }
}
