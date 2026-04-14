package io.github.milkdrinkers.milkonomics.cache;

import io.github.milkdrinkers.milkonomics.AbstractMilkonomics;
import io.github.milkdrinkers.milkonomics.Reloadable;
import io.github.milkdrinkers.milkonomics.api.account.AccountSnapshot;
import io.github.milkdrinkers.milkonomics.api.cache.Cache;
import io.github.milkdrinkers.milkonomics.database.Queries;
import org.jspecify.annotations.NonNull;

import java.util.Collections;
import java.util.List;

public abstract class CacheImpl implements Reloadable, Cache {
    private final AbstractMilkonomics plugin;
    private volatile List<AccountSnapshot> balanceTopCache = List.of();

    CacheImpl(AbstractMilkonomics plugin) {
        this.plugin = plugin;
    }

    @Override
    public void onLoad(AbstractMilkonomics plugin) {
        updateBalanceTop();
    }

    @Override
    public @NonNull List<AccountSnapshot> getBalanceTop() {
        return List.copyOf(balanceTopCache);
    }

    protected void updateBalanceTop() {
        balanceTopCache = Collections.unmodifiableList(
            Queries.Baltop.get(
                plugin.getConfigHandler().getConfig().balanceTop.cachedEntries,
                plugin.getDenominationHandler().getDefaultDenomination().id(),
                1
            )
        );
    }
}
