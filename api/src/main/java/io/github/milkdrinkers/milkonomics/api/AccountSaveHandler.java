package io.github.milkdrinkers.milkonomics.api;

import io.github.milkdrinkers.milkonomics.api.account.Account;
import io.github.milkdrinkers.milkonomics.api.account.AccountSnapshot;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Blocking;
import org.jetbrains.annotations.NotNull;

/**
 * Handles saving of accounts to the database. Accounts are queued for saving and flushed periodically to avoid overwhelming the database with too many individual save operations.
 */
public interface AccountSaveHandler {
    /**
     * Queues an account snapshot for saving. The account will be saved to the database during the next flush operation.
     *
     * @param account the account snapshot to queue for saving
     */
    void queue(@NotNull AccountSnapshot account);

    /**
     * Queues an account for saving by taking a snapshot of its current state. The account will be saved to the database during the next flush operation.
     *
     * @param account the account to queue for saving
     */
    default void queue(@NotNull Account account) {
        queue(account.getSnapshot());
    }

    /**
     * Flushes the save queue, saving all queued accounts to the database and clearing the queue.
     * This should be called periodically (e.g., every few minutes) to ensure that account changes
     * are persisted without overwhelming the database with too many individual save operations.
     *
     * @apiNote This method runs synchronously.
     */
    @Blocking
    @ApiStatus.Internal
    void flush();
}
