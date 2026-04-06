package io.github.milkdrinkers.milkonomics.economy.account;

import io.github.milkdrinkers.milkonomics.api.account.Account;
import io.github.milkdrinkers.milkonomics.api.account.AccountSnapshot;
import io.github.milkdrinkers.milkonomics.api.denomination.Denomination;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

public class AccountImpl extends Account {
    public AccountImpl(UUID uuid, String name, Denomination defaultDenomination, Map<String, BigDecimal> initialBalances) {
        super(uuid, name, defaultDenomination, initialBalances);
    }

    @Override
    public AccountSnapshot getSnapshot() {
        return null;
    }
}
