package io.github.milkdrinkers.milkonomics.economy.account;

import io.github.milkdrinkers.milkonomics.api.account.Account;
import io.github.milkdrinkers.milkonomics.api.account.AccountSnapshot;
import org.jspecify.annotations.NonNull;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

public class AccountImpl extends Account {
    public AccountImpl(UUID uuid, String name, Map<String, BigDecimal> initialBalances, boolean acceptingTransactions) {
        super(uuid, name, initialBalances, acceptingTransactions);
    }

    @Override
    public @NonNull AccountSnapshot getSnapshot() {
        return new io.github.milkdrinkers.milkonomics.economy.account.AccountSnapshot(
            getUUID(),
            getName(),
            getAllBalances(),
            isAcceptingTransactions()
        );
    }
}
