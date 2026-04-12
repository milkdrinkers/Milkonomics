package io.github.milkdrinkers.milkonomics.economy;

import io.github.milkdrinkers.milkonomics.AbstractMilkonomics;
import io.github.milkdrinkers.milkonomics.Reloadable;
import io.github.milkdrinkers.milkonomics.api.AccountManager;
import io.github.milkdrinkers.milkonomics.api.AccountSaveHandler;
import io.github.milkdrinkers.milkonomics.api.account.Account;
import io.github.milkdrinkers.milkonomics.api.denomination.Denomination;
import io.github.milkdrinkers.milkonomics.database.Queries;
import io.github.milkdrinkers.milkonomics.economy.account.AccountImpl;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

public final class AccountManagerImpl extends AccountManager<Account> implements Reloadable {
    public AccountManagerImpl() {
    }

    @Override
    public void onEnable(AbstractMilkonomics plugin) {
        Queries.Economy.load().forEach(acc -> {
            this.createAccount(acc.getUUID(), acc.getName(), plugin.getDenominationHandler().getDefaultDenomination(), plugin.getDenominationHandler().getDenominationsDefaults());
        });
    }

    @Override
    public void onDisable(AbstractMilkonomics plugin) {
        clear();
    }

    @Override
    protected AccountImpl newAccount(UUID uuid, String name, Denomination defaultDenomination, Map<String, BigDecimal> initialBalances) {
        return new AccountImpl(uuid, name, defaultDenomination, initialBalances, true);
    }
}
