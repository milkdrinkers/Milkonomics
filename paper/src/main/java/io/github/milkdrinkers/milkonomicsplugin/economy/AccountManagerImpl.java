package io.github.milkdrinkers.milkonomicsplugin.economy;

import io.github.milkdrinkers.milkonomicsplugin.AbstractMilkonomicsPlugin;
import io.github.milkdrinkers.milkonomicsplugin.Reloadable;
import io.github.milkdrinkers.milkonomicsplugin.api.AccountManager;
import io.github.milkdrinkers.milkonomicsplugin.api.AccountSaveHandler;
import io.github.milkdrinkers.milkonomicsplugin.api.account.Account;
import io.github.milkdrinkers.milkonomicsplugin.api.denomination.Denomination;
import io.github.milkdrinkers.milkonomicsplugin.database.Queries;
import io.github.milkdrinkers.milkonomicsplugin.economy.account.AccountImpl;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

public class AccountManagerImpl extends AccountManager<Account> implements Reloadable {
    private final AccountSaveHandler saveHandler;

    public AccountManagerImpl(AccountSaveHandler saveHandler) {
        this.saveHandler = saveHandler;
    }

    @Override
    public void onEnable(AbstractMilkonomicsPlugin plugin) {
        Queries.Economy.load().forEach(acc -> {
            this.createAccount(acc.getUUID(), acc.getName(), plugin.getDenominationHandler().getDefaultDenomination(), plugin.getDenominationHandler().getDenominationsDefaults());
        });
    }

    @Override
    public void onDisable(AbstractMilkonomicsPlugin plugin) {
        clear();
    }

    @Override
    protected AccountImpl newAccount(UUID uuid, String name, Denomination defaultDenomination, Map<String, BigDecimal> initialBalances) {
        return new AccountImpl(uuid, name, defaultDenomination, initialBalances);
    }
}
