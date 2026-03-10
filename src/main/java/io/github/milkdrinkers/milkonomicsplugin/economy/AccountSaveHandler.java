package io.github.milkdrinkers.milkonomicsplugin.economy;

import io.github.milkdrinkers.milkonomicsplugin.MilkonomicsPlugin;
import io.github.milkdrinkers.milkonomicsplugin.Reloadable;
import io.github.milkdrinkers.milkonomicsplugin.database.Queries;
import io.github.milkdrinkers.milkonomicsplugin.economy.account.Account;
import io.github.milkdrinkers.milkonomicsplugin.economy.account.AccountSnapshot;
import io.papermc.paper.threadedregions.scheduler.ScheduledTask;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * Handles saving of accounts to the database. Accounts are queued for saving and flushed periodically to avoid overwhelming the database with too many individual save operations.
 */
public class AccountSaveHandler implements Reloadable {
    private final Map<UUID, AccountSnapshot> queue;
    private ScheduledTask task;

    public AccountSaveHandler() {
        this.queue = new ConcurrentHashMap<>();
    }

    @Override
    public void onEnable(MilkonomicsPlugin plugin) {
        queue.clear();
        task = plugin.getServer().getAsyncScheduler().runAtFixedRate(plugin, task -> flush(), 0L, 1, TimeUnit.SECONDS); // TODO Make flush interval configurable
    }

    /**
     * Cancels the scheduled flush task and flushes any remaining accounts in the queue before shutdown.
     */
    @Override
    public void onDisable(MilkonomicsPlugin plugin) {
        if (task != null && !task.isCancelled()) {
            task.cancel();
            task = null;
        }
        flush(); // Flush any remaining accounts in the queue before shutdown
    }

    /**
     * Queues an account snapshot for saving. The account will be saved to the database during the next flush operation.
     * @param account the account snapshot to queue for saving
     */
    public void queue(AccountSnapshot account) {
        queue.put(account.uuid(), account);
    }

    /**
     * Queues an account for saving by taking a snapshot of its current state. The account will be saved to the database during the next flush operation.
     * @param account the account to queue for saving
     */
    public void queue(Account account) {
        queue(account.getSnapshot());
    }

    /**
     * Flushes the save queue, saving all queued accounts to the database and clearing the queue.
     * This should be called periodically (e.g., every few minutes) to ensure that account changes
     * are persisted without overwhelming the database with too many individual save operations.
     * @apiNote This method runs synchronously.
     */
    public void flush() {
        if (queue.isEmpty())
            return;

        final List<AccountSnapshot> snapshots = List.copyOf(queue.values());
        snapshots.forEach(s -> queue.remove(s.uuid(), s)); // Drain original queue of flushed accounts

        Queries.Economy.save(queue.values());
    }
}
