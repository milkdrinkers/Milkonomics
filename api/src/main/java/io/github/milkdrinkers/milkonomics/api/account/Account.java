package io.github.milkdrinkers.milkonomics.api.account;

import io.github.milkdrinkers.milkonomics.api.MilkonomicsAPI;
import io.github.milkdrinkers.milkonomics.api.denomination.Denomination;
import org.jetbrains.annotations.*;
import org.jspecify.annotations.NonNull;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.StampedLock;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * Account represents a financial account that can hold balances in multiple denominations.
 */
public abstract class Account implements AccountBalance, DenominationBalance {
    private final UUID uuid;
    private final String name;
    private final Map<String, BalanceEntry> balances = new ConcurrentHashMap<>();

    private final StampedLock stateLock = new StampedLock(); // Lock for misc state
    private boolean acceptingTransactions;

    public Account(
        @NotNull UUID uuid,
        @NotNull String name,
        @NotNull Map<String, BigDecimal> initialBalances,
        boolean acceptingTransactions
    ) {
        this.uuid = uuid;
        this.name = name;
        for (Map.Entry<String, BigDecimal> entry : initialBalances.entrySet()) {
            this.balances.put(entry.getKey(), new BalanceEntry(entry.getValue()));
        }
        this.acceptingTransactions = acceptingTransactions;
    }

    /**
     * Gets the unique identifier of the account.
     * @return The UUID of the account.
     */
    public @NotNull UUID getUUID() {
        return uuid;
    }

    /**
     * Gets the name of the account.
     * @return The name of the account.
     */
    public @NotNull String getName() {
        return name;
    }

    /**
     * Gets all the balances of the account for all denominations. The keys of the map are the denomination id's, and the values are the corresponding balances.
     * @return A map containing the balances of the account for all denominations.
     */
    @NotNull
    @UnmodifiableView
    public Map<String, BigDecimal> getAllBalances() {
        return Collections.unmodifiableMap(balances.entrySet().stream()
            .map(entry -> Map.entry(entry.getKey(), entry.getValue().balance))
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue))
        );
    }

    @Override
    public @NonNull BigDecimal get() {
        return get(MilkonomicsAPI.getInstance().getDenominationManager().getDefaultDenomination());
    }

    @Override
    public boolean set(@NonNull BigDecimal amount) {
        return set(MilkonomicsAPI.getInstance().getDenominationManager().getDefaultDenomination(), amount);
    }

    @Override
    public boolean has(@NonNull BigDecimal amount) {
        return has(MilkonomicsAPI.getInstance().getDenominationManager().getDefaultDenomination(), amount);
    }

    @Override
    public boolean withdraw(@NonNull BigDecimal amount) {
        return withdraw(MilkonomicsAPI.getInstance().getDenominationManager().getDefaultDenomination(), amount);
    }

    @Override
    public boolean deposit(@NonNull BigDecimal amount) {
        return deposit(MilkonomicsAPI.getInstance().getDenominationManager().getDefaultDenomination(), amount);
    }

    @Override
    public @NonNull BigDecimal get(@NonNull Denomination denomination) {
        return getEntry(denomination).read(() -> balances.get(denomination.id()).balance);
    }

    @Override
    public boolean set(@NonNull Denomination denomination, @NonNull BigDecimal amount) {
        getEntry(denomination).writeWith(() -> balances.get(denomination.id()).balance = amount);
        MilkonomicsAPI.getInstance().getAccountSaveHandler().queue(this);
        return true;
    }

    @Override
    public boolean has(@NonNull Denomination denomination, @NonNull BigDecimal amount) {
        return getEntry(denomination).read(() -> balances.get(denomination.id()).balance.compareTo(amount) >= 0);
    }

    @Override
    public boolean withdraw(@NonNull Denomination denomination, @NonNull BigDecimal amount) {
        final boolean ok = getEntry(denomination).writeAndGet(() -> {
            final BalanceEntry entry = balances.get(denomination.id());
            if (entry.balance.compareTo(amount) < 0) return false;
            entry.balance = entry.balance.subtract(amount);
            return true;
        });
        if (ok)
            MilkonomicsAPI.getInstance().getAccountSaveHandler().queue(this);
        return ok;
    }

    @Override
    public boolean deposit(@NonNull Denomination denomination, @NonNull BigDecimal amount) {
        getEntry(denomination).writeWith(() -> {
            final BalanceEntry entry = balances.get(denomination.id());
            entry.balance = entry.balance.add(amount);
        });
        MilkonomicsAPI.getInstance().getAccountSaveHandler().queue(this);
        return true;
    }

    /**
     * Gets the BalanceEntry for the specified denomination, creating a new one with the default balance if it does not already exist.
     * @param denomination The denomination to get the BalanceEntry for.
     * @return The BalanceEntry for the specified denomination.
     */
    @ApiStatus.Internal
    private BalanceEntry getEntry(Denomination denomination) {
        return balances.computeIfAbsent(denomination.id(), id -> new BalanceEntry(denomination.defaultBalance()));
    }

    /**
     * BalanceEntry is a helper class that encapsulates the balance for a specific denomination and provides thread-safe read and write operations using a StampedLock.
     */
    @ApiStatus.Internal
    private static class BalanceEntry {
        private @NotNull BigDecimal balance;
        private final @NotNull StampedLock lock = new StampedLock();

        BalanceEntry(@NonNull BigDecimal initialBalance) {
            this.balance = initialBalance;
        }

        /**
         * Performs an optimistic read using the provided reader function. If the optimistic read fails, it falls back to a pessimistic read.
         *
         * @param reader The function to read the value. This should not modify any state.
         * @param <T>    The type of the value being read.
         * @return The value read by the reader function.
         */
        @NotNull
        <T> T read(@NotNull Supplier<T> reader) {
            long stamp = lock.tryOptimisticRead();
            T value = reader.get();

            if (!lock.validate(stamp)) {
                stamp = lock.readLock();
                try {
                    value = reader.get();
                } finally {
                    lock.unlockRead(stamp);
                }
            }

            return value;
        }

        /**
         * Performs a write operation using the provided writer function. This will acquire a write lock for the duration of the operation.
         *
         * @param writer The function to perform the write operation. This should modify the state of the account.
         */
        void writeWith(@NotNull Runnable writer) {
            final long stamp = lock.writeLock();
            try {
                writer.run();
            } finally {
                lock.unlockWrite(stamp);
            }
        }

        /**
         * Performs a write operation using the provided writer function and returns a value. This will acquire a write lock for the duration of the operation.
         *
         * @param writer The function to perform the write operation. This should modify the state of the account and return a value.
         * @param <T>    The type of the value being returned by the writer function.
         * @return The value returned by the writer function.
         */
        @NotNull
        <T> T writeAndGet(@NotNull Supplier<T> writer) {
            final long stamp = lock.writeLock();
            try {
                return writer.get();
            } finally {
                lock.unlockWrite(stamp);
            }
        }
    }

    /**
     * Checks if the account is currently accepting transactions.
     */
    public boolean isAcceptingTransactions() {
        long stamp = stateLock.tryOptimisticRead();
        boolean value = acceptingTransactions;

        if (!stateLock.validate(stamp)) {
            stamp = stateLock.readLock();
            try {
                value = acceptingTransactions;
            } finally {
                stateLock.unlockRead(stamp);
            }
        }

        return value;
    }

    /**
     * Sets whether the account is currently accepting transactions. When set to false, all transaction operations (withdraw, deposit, set) should be rejected until accepting transactions is set back to true.
     *
     * @param value Whether the account should accept transactions.
     */
    public void setAcceptingTransactions(boolean value) {
        final long stamp = stateLock.writeLock();
        try {
            acceptingTransactions = value;
        } finally {
            stateLock.unlockWrite(stamp);
        }
        MilkonomicsAPI.getInstance().getAccountSaveHandler().queue(this);
    }
}