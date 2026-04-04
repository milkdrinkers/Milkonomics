package io.github.milkdrinkers.milkonomicsplugin.economy.account;

import io.github.milkdrinkers.milkonomicsplugin.api.account.Account;
import io.github.milkdrinkers.milkonomicsplugin.api.account.AccountSnapshot;
import io.github.milkdrinkers.milkonomicsplugin.api.denomination.Denomination;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

public class AccountImpl extends Account {
    public AccountImpl(UUID uuid, String name, Denomination defaultDenomination, Map<Denomination, BigDecimal> initialBalances) {
        super(uuid, name, defaultDenomination, initialBalances);
    }

    public AccountImpl(UUID uuid, String name, Denomination defaultDenomination, BigDecimal initialBalance) {
        super(uuid, name, defaultDenomination, initialBalance);
    }

    public AccountImpl(UUID uuid, String name, Denomination defaultDenomination) {
        super(uuid, name, defaultDenomination);
    }

    @Override
    public AccountSnapshot getSnapshot() {
        return null;
    }
}
