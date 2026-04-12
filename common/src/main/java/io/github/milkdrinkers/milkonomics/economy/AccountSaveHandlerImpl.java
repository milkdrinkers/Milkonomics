package io.github.milkdrinkers.milkonomics.economy;

import io.github.milkdrinkers.milkonomics.AbstractMilkonomics;
import io.github.milkdrinkers.milkonomics.Reloadable;
import io.github.milkdrinkers.milkonomics.api.AccountSaveHandler;
import io.github.milkdrinkers.milkonomics.api.account.AccountSnapshot;
import io.github.milkdrinkers.milkonomics.database.Queries;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Handles saving of accounts to the database. Accounts are queued for saving and flushed periodically to avoid overwhelming the database with too many individual save operations.
 */
public abstract class AccountSaveHandlerImpl implements Reloadable, AccountSaveHandler {
    private final Map<UUID, AccountSnapshot> queue;

    public AccountSaveHandlerImpl() {
        this.queue = new ConcurrentHashMap<>();
    }

    @Override
    public void onEnable(AbstractMilkonomics plugin) {
        queue.clear();
    }

    /**
     * Cancels the scheduled flush task and flushes any remaining accounts in the queue before shutdown.
     */
    @Override
    public void onDisable(AbstractMilkonomics plugin) {
        flush(); // Flush any remaining accounts in the queue before shutdown
    }

    @Override
    public void queue(AccountSnapshot account) {
        queue.put(account.uuid(), account);
    }

    @Override
    public void flush() {
        if (queue.isEmpty())
            return;

        final List<AccountSnapshot> snapshots = List.copyOf(queue.values());
        snapshots.forEach(s -> queue.remove(s.uuid(), s)); // Drain original queue of flushed accounts

        Queries.Economy.save(snapshots);
    }
}
