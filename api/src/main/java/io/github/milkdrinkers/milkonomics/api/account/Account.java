package io.github.milkdrinkers.milkonomics.api.account;

import io.github.milkdrinkers.milkonomics.api.MilkonomicsAPI;
import io.github.milkdrinkers.milkonomics.api.denomination.Denomination;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.StampedLock;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public abstract class Account implements AccountBalance, DenominationBalance {
    private final UUID uuid;
    private final String name;
    private final Map<String, BalanceEntry> balances = new ConcurrentHashMap<>();

    private final StampedLock stateLock = new StampedLock();
    private boolean acceptingTransactions = true;

    public Account(
        UUID uuid,
        String name,
        Map<String, BigDecimal> initialBalances,
        boolean acceptingTransactions
    ) {
        this.uuid = uuid;
        this.name = name;
        for (Map.Entry<String, BigDecimal> entry : initialBalances.entrySet()) {
            this.balances.put(entry.getKey(), new BalanceEntry(entry.getValue()));
        }
        this.acceptingTransactions = acceptingTransactions;
    }

    public Account(UUID uuid, String name, BigDecimal initialBalance) {
        this.uuid = uuid;
        this.name = name;
        this.balances.put(MilkonomicsAPI.getInstance().getDenominationManager().getDefaultDenomination().id(), new BalanceEntry(initialBalance));
    }

    public Account(UUID uuid, String name) {
        this(uuid, name, BigDecimal.ZERO);
    }

    public UUID getUUID() {
        return uuid;
    }

    public String getName() {
        return name;
    }

    @Override
    public BigDecimal get() {
        return get(MilkonomicsAPI.getInstance().getDenominationManager().getDefaultDenomination());
    }

    public Map<String, BigDecimal> getAllBalances() {
        final Map<String, BigDecimal> result = new ConcurrentHashMap<>();
        balances.forEach((denomId, entry) -> result.put(denomId, entry.read(() -> entry.balance)));
        return result;
    }

    @Override
    public boolean set(BigDecimal amount) {
        return set(MilkonomicsAPI.getInstance().getDenominationManager().getDefaultDenomination(), amount);
    }

    @Override
    public boolean has(BigDecimal amount) {
        return has(MilkonomicsAPI.getInstance().getDenominationManager().getDefaultDenomination(), amount);
    }

    @Override
    public boolean withdraw(BigDecimal amount) {
        return withdraw(MilkonomicsAPI.getInstance().getDenominationManager().getDefaultDenomination(), amount);
    }

    @Override
    public boolean deposit(BigDecimal amount) {
        return deposit(MilkonomicsAPI.getInstance().getDenominationManager().getDefaultDenomination(), amount);
    }

    @Override
    public BigDecimal get(Denomination denomination) {
        return getEntry(denomination).read(() -> balances.get(denomination.id()).balance);
    }

    @Override
    public boolean set(Denomination denomination, BigDecimal amount) {
        getEntry(denomination).writeWith(() -> balances.get(denomination.id()).balance = amount);
        MilkonomicsAPI.getInstance().getAccountSaveHandler().queue(this);
        return true;
    }

    @Override
    public boolean has(Denomination denomination, BigDecimal amount) {
        return getEntry(denomination).read(() -> balances.get(denomination.id()).balance.compareTo(amount) >= 0);
    }

    @Override
    public boolean withdraw(Denomination denomination, BigDecimal amount) {
        return getEntry(denomination).writeAndGet(() -> {
            final BalanceEntry entry = balances.get(denomination.id());
            if (entry.balance.compareTo(amount) < 0) {
                return false;
            }
            entry.balance = entry.balance.subtract(amount);
            MilkonomicsAPI.getInstance().getAccountSaveHandler().queue(this);
            return true;
        });
    }

    @Override
    public boolean deposit(Denomination denomination, BigDecimal amount) {
        getEntry(denomination).writeWith(() -> {
            final BalanceEntry entry = balances.get(denomination.id());
            entry.balance = entry.balance.add(amount);
        });
        MilkonomicsAPI.getInstance().getAccountSaveHandler().queue(this);
        return true;
    }

    private BalanceEntry getEntry(Denomination denomination) {
        return balances.computeIfAbsent(denomination.id(), id -> new BalanceEntry(BigDecimal.ZERO));
    }

    private static class BalanceEntry {
        private BigDecimal balance;
        private final StampedLock lock = new StampedLock();

        BalanceEntry(BigDecimal initialBalance) {
            this.balance = initialBalance;
        }

        /**
         * Performs an optimistic read using the provided reader function. If the optimistic read fails, it falls back to a pessimistic read.
         *
         * @param reader The function to read the value. This should not modify any state.
         * @param <T>    The type of the value being read.
         * @return The value read by the reader function.
         */
        <T> T read(Supplier<T> reader) {
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
        void writeWith(Runnable writer) {
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
        <T> T writeAndGet(Supplier<T> writer) {
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
            MilkonomicsAPI.getInstance().getAccountSaveHandler().queue(this);
        }
    }
}
