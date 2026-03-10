package io.github.milkdrinkers.milkonomicsplugin.economy;

import io.github.milkdrinkers.milkonomicsplugin.MilkonomicsPlugin;
import io.github.milkdrinkers.milkonomicsplugin.Reloadable;
import io.github.milkdrinkers.milkonomicsplugin.database.Queries;
import io.github.milkdrinkers.milkonomicsplugin.economy.account.Account;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.plugin.ServicePriority;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class AccountManager implements Reloadable {
    private final Map<UUID, Account> accounts;
    private final Map<String, UUID> accountsReverseLookup;
    private final EconomyImpl economy;
    private final AccountSaveHandler saveHandler;

    public AccountManager(AccountSaveHandler saveHandler) {
        this.saveHandler = saveHandler;
        this.accounts = new ConcurrentHashMap<>();
        this.accountsReverseLookup = new ConcurrentHashMap<>();
        this.economy = new EconomyImpl(MilkonomicsPlugin.getInstance(), this);
    }

    @Override
    public void onEnable(MilkonomicsPlugin plugin) {
        Bukkit.getServicesManager().register(Economy.class, economy, MilkonomicsPlugin.getInstance(), ServicePriority.Highest);
        Queries.Economy.load().forEach(acc -> {
            accounts.put(acc.getUUID(), acc);
            accountsReverseLookup.put(acc.getName(), acc.getUUID());
        });
    }

    @Override
    public void onDisable(MilkonomicsPlugin plugin) {
        accounts.clear();
        accountsReverseLookup.clear();
    }

    public Map<UUID, Account> getAccounts() {
        return accounts;
    }

    public Map<String, UUID> getAccountsReverseLookup() {
        return accountsReverseLookup;
    }

    public boolean hasAccount(UUID uuid) {
        return getAccounts().containsKey(uuid);
    }

    public boolean hasAccount(String accountId) {
        return getAccountsReverseLookup().containsKey(accountId);
    }

    public Account getAccount(UUID uuid) {
        return getAccounts().get(uuid);
    }

    public Account getAccount(String accountId) {
        return getAccount(getAccountsReverseLookup().get(accountId));
    }

    public void addAccount(Account account) {
        getAccounts().put(account.getUUID(), account);
        getAccountsReverseLookup().put(account.getName(), account.getUUID());
        saveHandler.queue(account);
    }

    public Economy getEconomy() {
        return economy;
    }
}
