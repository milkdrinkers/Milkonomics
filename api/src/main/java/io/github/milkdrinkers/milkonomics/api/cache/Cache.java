package io.github.milkdrinkers.milkonomics.api.cache;

import io.github.milkdrinkers.milkonomics.api.account.AccountSnapshot;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.List;

/**
 * Cache provides cached data for frequently accessed information to improve performance. The cache is updated periodically based on the user configuration settings.
 */
public interface Cache {
    /**
     * Get the cached balance top list. The list is ordered by balance in descending order.
     * @return the cached balance top list
     */
    @UnmodifiableView
    List<AccountSnapshot> getBalanceTop();
}
