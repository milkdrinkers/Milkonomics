package io.github.milkdrinkers.milkonomicsplugin.api;

import io.github.milkdrinkers.milkonomicsplugin.api.account.Account;
import io.github.milkdrinkers.milkonomicsplugin.api.denomination.Denomination;

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
     * @param uuid The unique identifier for the account.
     * @param name The name of the account.
     * @param defaultDenomination The denomination used by all default balance operations.
     * @param initialBalances The starting balances.
     * @return A new, unregistered account instance.
     * @apiNote Implementations should not register the account themselves.
     */
    protected abstract A newAccount(UUID uuid, String name, Denomination defaultDenomination, Map<String, BigDecimal> initialBalances);

    /**
     * Looks up an account by UUID.
     *
     * @param uuid The UUID to look up.
     * @return An {@link Optional} containing the account, or empty if not found.
     */
    public Optional<A> getAccount(UUID uuid) {
        return Optional.ofNullable(accounts.get(uuid));
    }

    /**
     * Looks up an account by name.
     *
     * @param name The name to look up.
     * @return An {@link Optional} containing the account, or empty if not found.
     */
    public Optional<A> getAccount(String name) {
        final UUID uuid = accountLookup.get(name);
        return Optional.ofNullable(uuid != null ? accounts.get(uuid) : null);
    }

    /**
     * Returns whether an account with the given UUID is registered.
     */
    public boolean hasAccount(UUID uuid) {
        return accounts.containsKey(uuid);
    }

    /**
     * Returns whether an account with the given name is registered.
     */
    public boolean hasAccount(String name) {
        return accountLookup.containsKey(name);
    }

    /**
     * Returns an unmodifiable view of all registered accounts.
     */
    public Collection<A> getAccounts() {
        return Collections.unmodifiableCollection(accounts.values());
    }

    /**
     * Creates and registers a new account with the given identity and starting balance.
     * If an account with the same UUID already exists, the existing account is returned.
     *
     * @param uuid The unique identifier for the account.
     * @param name The name for the account.
     * @param defaultDenomination The denomination used for all default balance operations.
     * @param initialBalances The starting balances.
     * @return The newly created account, or the existing account if one already existed.
     */
    public A createAccount(UUID uuid, String name, Denomination defaultDenomination, Map<String, BigDecimal> initialBalances) {
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

            final A account = newAccount(uuid, name, defaultDenomination, initialBalances);
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
     * @see #createAccount(UUID, String, Denomination, Map)
     */
    public A createAccount(UUID uuid, String name, Denomination defaultDenomination) {
        return createAccount(uuid, name, defaultDenomination, Map.of(defaultDenomination.id(), BigDecimal.ZERO));
    }

    /**
     * Removes an account from the manager by UUID.
     *
     * @param uuid The UUID of the account to remove.
     * @return The removed account, or {@link Optional#empty()} if no account was found.
     */
    public Optional<A> removeAccount(UUID uuid) {
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
    public Optional<A> removeAccount(String name) {
        final UUID uuid = accountLookup.get(name);
        return uuid != null ? removeAccount(uuid) : Optional.empty();
    }

    /**
     * Clears all accounts from the manager.
     */
    public void clear() {
        writeLock.lock();
        try {
            accounts.clear();
            accountLookup.clear();
        } finally {
            writeLock.unlock();
        }
    }
}