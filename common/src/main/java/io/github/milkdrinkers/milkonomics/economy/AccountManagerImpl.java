package io.github.milkdrinkers.milkonomics.economy;

import io.github.milkdrinkers.milkonomics.AbstractMilkonomics;
import io.github.milkdrinkers.milkonomics.Reloadable;
import io.github.milkdrinkers.milkonomics.api.AccountManager;
import io.github.milkdrinkers.milkonomics.api.account.Account;
import io.github.milkdrinkers.milkonomics.database.Queries;
import io.github.milkdrinkers.milkonomics.economy.account.AccountImpl;
import org.jspecify.annotations.NonNull;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

public final class AccountManagerImpl extends AccountManager<Account> implements Reloadable {
    public AccountManagerImpl() {
    }

    @Override
    public void onEnable(AbstractMilkonomics plugin) {
        loadAccounts(Queries.Economy.load());
    }

    @Override
    public void onDisable(AbstractMilkonomics plugin) {
        clear();
    }

    @Override
    protected @NonNull Account newAccount(@NonNull UUID uuid, @NonNull String name, @NonNull Map<String, BigDecimal> initialBalances) {
        return new AccountImpl(
            uuid,
            name,
            initialBalances,
            true
        );
    }
}
