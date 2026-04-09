package io.github.milkdrinkers.milkonomics.economy;

import io.github.milkdrinkers.milkonomics.AbstractMilkonomics;
import io.github.milkdrinkers.milkonomics.Reloadable;
import io.github.milkdrinkers.milkonomics.api.AccountSaveHandler;
import io.github.milkdrinkers.milkonomics.api.account.AccountSnapshot;
import io.github.milkdrinkers.milkonomics.database.Queries;
import io.papermc.paper.threadedregions.scheduler.ScheduledTask;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * Handles saving of accounts to the database. Accounts are queued for saving and flushed periodically to avoid overwhelming the database with too many individual save operations.
 */
public final class AccountSaveHandlerImpl implements Reloadable, AccountSaveHandler {
    private final Map<UUID, io.github.milkdrinkers.milkonomics.api.account.AccountSnapshot> queue;
    private ScheduledTask task;

    public AccountSaveHandlerImpl() {
        this.queue = new ConcurrentHashMap<>();
    }

    @Override
    public void onEnable(AbstractMilkonomics plugin) {
        queue.clear();
        task = plugin.getServer().getAsyncScheduler().runAtFixedRate(plugin, task -> flush(), 0L, 1, TimeUnit.SECONDS); // TODO Make flush interval configurable
    }

    /**
     * Cancels the scheduled flush task and flushes any remaining accounts in the queue before shutdown.
     */
    @Override
    public void onDisable(AbstractMilkonomics plugin) {
        if (task != null && !task.isCancelled()) {
            task.cancel();
            task = null;
        }
        flush(); // Flush any remaining accounts in the queue before shutdown
    }

    @Override
    public void queue(io.github.milkdrinkers.milkonomics.api.account.AccountSnapshot account) {
        queue.put(account.uuid(), account);
    }

    @Override
    public void flush() {
        if (queue.isEmpty())
            return;

        final List<AccountSnapshot> snapshots = List.copyOf(queue.values());
        snapshots.forEach(s -> queue.remove(s.uuid(), s)); // Drain original queue of flushed accounts

        Queries.Economy.save(queue.values());
    }
}
