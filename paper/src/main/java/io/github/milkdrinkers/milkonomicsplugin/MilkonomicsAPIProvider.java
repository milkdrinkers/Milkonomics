package io.github.milkdrinkers.milkonomicsplugin;

import io.github.milkdrinkers.milkonomicsplugin.api.AccountManager;
import io.github.milkdrinkers.milkonomicsplugin.api.AccountSaveHandler;
import io.github.milkdrinkers.milkonomicsplugin.api.MilkonomicsAPI;
import io.github.milkdrinkers.milkonomicsplugin.api.account.Account;
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
