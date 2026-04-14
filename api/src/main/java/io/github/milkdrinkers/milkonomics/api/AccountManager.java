package io.github.milkdrinkers.milkonomics.api;

import io.github.milkdrinkers.milkonomics.api.account.Account;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Manages accounts.
 *
 * @param <A> The concrete account type this manager produces.
 */
public abstract class AccountManager<A extends Account> {
    private final Map<UUID, A> accounts = new ConcurrentHashMap<>();
    private final Map<String, UUID> accountLookup = new ConcurrentHashMap<>();

    private final ReentrantLock writeLock = new ReentrantLock();

    /**
     * Constructs a new account instance. Called internally by {@link #createAccount}.
     *
     * @param uuid                The unique identifier for the account.
     * @param name                The name of the account.
     * @param initialBalances     The starting balances.
     * @return A new, unregistered account instance.
     * @apiNote Implementations should not register the account themselves.
     */
    @ApiStatus.Internal
    protected abstract @NotNull A newAccount(@NotNull UUID uuid, @NotNull String name, @NotNull Map<String, BigDecimal> initialBalances);

    /**
     * Looks up an account by UUID.
     *
     * @param uuid The UUID to look up.
     * @return An {@link Optional} containing the account, or empty if not found.
     */
    public @NotNull Optional<A> getAccount(@NotNull UUID uuid) {
        return Optional.ofNullable(accounts.get(uuid));
    }

    /**
     * Looks up an account by name.
     *
     * @param name The name to look up.
     * @return An {@link Optional} containing the account, or empty if not found.
     */
    public @NotNull Optional<A> getAccount(@NotNull String name) {
        final UUID uuid = accountLookup.get(name);
        return Optional.ofNullable(uuid != null ? accounts.get(uuid) : null);
    }

    /**
     * Returns whether an account with the given UUID is registered.
     */
    public boolean hasAccount(@NotNull UUID uuid) {
        return accounts.containsKey(uuid);
    }

    /**
     * Returns whether an account with the given name is registered.
     */
    public boolean hasAccount(@NotNull String name) {
        return accountLookup.containsKey(name);
    }

    /**
     * Returns an unmodifiable view of all registered accounts.
     */
    @NotNull
    @UnmodifiableView
    public Collection<A> getAccounts() {
        return Collections.unmodifiableCollection(accounts.values());
    }

    /**
     * Creates and registers a new account with the given identity and starting balance.
     * If an account with the same UUID already exists, the existing account is returned.
     *
     * @param uuid                The unique identifier for the account.
     * @param name                The name for the account.
     * @param initialBalances     The starting balances.
     * @return The newly created account, or the existing account if one already existed.
     */
    public @NotNull A createAccount(@NotNull UUID uuid, @NotNull String name, @NotNull Map<String, BigDecimal> initialBalances) {
        A existing = accounts.get(uuid);
        if (existing != null) {
            return existing;
        }

        writeLock.lock();
        try {
            existing = accounts.get(uuid);
            if (existing != null) {
                return existing;
            }

            final A account = newAccount(uuid, name, initialBalances);
            accounts.put(uuid, account);
            accountLookup.put(name, uuid);
            return account;
        } finally {
            writeLock.unlock();
        }
    }

    /**
     * Creates and registers a new account with a zero starting balance.
     *
     * @see #createAccount(UUID, String, Map)
     */
    public @NotNull A createAccount(@NotNull UUID uuid, @NotNull String name) {
        return createAccount(uuid, name, Map.of(MilkonomicsAPI.getInstance().getDenominationManager().getDefaultDenomination().id(), BigDecimal.ZERO));
    }

    /**
     * Loads a collection of accounts into the manager, replacing any existing accounts with the same UUIDs.
     * @param accounts The accounts to load.
     */
    @ApiStatus.Internal
    protected void loadAccounts(@NotNull Collection<A> accounts) {
        writeLock.lock();
        try {
            for (A account : accounts) {
                this.accounts.put(account.getUUID(), account);
                accountLookup.put(account.getName(), account.getUUID());
            }
        } finally {
            writeLock.unlock();
        }
    }

    /**
     * Removes an account from the manager by UUID.
     *
     * @param uuid The UUID of the account to remove.
     * @return The removed account, or {@link Optional#empty()} if no account was found.
     */
    public @NotNull Optional<A> removeAccount(@NotNull UUID uuid) {
        if (!accounts.containsKey(uuid)) {
            return Optional.empty();
        }

        writeLock.lock();
        try {
            final A removed = accounts.remove(uuid);
            if (removed != null) {
                accountLookup.remove(removed.getName());
            }
            return Optional.ofNullable(removed);
        } finally {
            writeLock.unlock();
        }
    }

    /**
     * Removes an account from the manager by name.
     *
     * @param name The name of the account to remove.
     * @return The removed account, or {@link Optional#empty()} if no account was found.
     */
    public @NotNull Optional<A> removeAccount(@NotNull String name) {
        final UUID uuid = accountLookup.get(name);
        return uuid != null ? removeAccount(uuid) : Optional.empty();
    }

    /**
     * Clears all accounts from the manager.
     */
    @ApiStatus.Internal
    protected void clear() {
        writeLock.lock();
        try {
            accounts.clear();
            accountLookup.clear();
        } finally {
            writeLock.unlock();
        }
    }
}