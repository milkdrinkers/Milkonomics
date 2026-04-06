package io.github.milkdrinkers.milkonomicsplugin.database;

import io.github.milkdrinkers.milkonomicsplugin.AbstractMilkonomics;
import io.github.milkdrinkers.milkonomicsplugin.api.account.AccountSnapshot;
import io.github.milkdrinkers.milkonomicsplugin.cooldown.CooldownType;
import io.github.milkdrinkers.milkonomicsplugin.cooldown.Cooldowns;
import io.github.milkdrinkers.milkonomicsplugin.database.schema.tables.records.AccountsBalanceRecord;
import io.github.milkdrinkers.milkonomicsplugin.database.schema.tables.records.AccountsRecord;
import io.github.milkdrinkers.milkonomicsplugin.database.schema.tables.records.CooldownsRecord;
import io.github.milkdrinkers.milkonomicsplugin.economy.account.AccountImpl;
import io.github.milkdrinkers.milkonomicsplugin.economy.denomination.DenominationHandler;
import io.github.milkdrinkers.milkonomicsplugin.messaging.message.BidirectionalMessage;
import io.github.milkdrinkers.milkonomicsplugin.messaging.message.IncomingMessage;
import io.github.milkdrinkers.milkonomicsplugin.messaging.message.OutgoingMessage;
import io.github.milkdrinkers.milkonomicsplugin.utility.DB;
import io.github.milkdrinkers.milkonomicsplugin.utility.Logger;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.ApiStatus;
import org.jooq.*;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

import static io.github.milkdrinkers.milkonomicsplugin.database.QueryUtils.UUIDUtil;
import static io.github.milkdrinkers.milkonomicsplugin.database.schema.Tables.*;
import static org.jooq.impl.DSL.*;

/**
 * A class providing access to all SQL queries.
 */
@SuppressWarnings({"LoggingSimilarMessage", "StringConcatenationArgumentToLogCall"})
public final class Queries {
    /**
     * Holds all queries related to using the database as a messaging service.
     */
    @ApiStatus.Internal
    public static final class Sync {
        /**
         * Fetch the latest (greatest) message ID from the database.
         *
         * @return the message id or empty if no messages are queued
         */
        public static Optional<Integer> fetchLatestMessageId() {
            try (
                Connection con = DB.getConnection()
            ) {
                DSLContext context = DB.getContext(con);

                return context
                    .select(max(MESSAGING.ID))
                    .from(MESSAGING)
                    .fetchOptional(0, Integer.class);
            } catch (SQLException e) {
                Logger.get().error("SQL Query threw an error!" + e);
                return Optional.empty();
            }
        }

        /**
         * Adds a message to the database.
         *
         * @param message the outgoing message to send
         * @return the new message id or empty if insert failed
         */
        public static <T> Optional<Integer> send(OutgoingMessage<T> message) {
            try (
                Connection con = DB.getConnection()
            ) {
                DSLContext context = DB.getContext(con);

                return context
                    .insertInto(MESSAGING, MESSAGING.TIMESTAMP, MESSAGING.MESSAGE)
                    .values(
                        currentLocalDateTime(),
                        val(message.encode())
                    )
                    .returningResult(MESSAGING.ID)
                    .fetchOptional(0, Integer.class);
            } catch (SQLException e) {
                Logger.get().error("SQL Query threw an error!" + e);
                return Optional.empty();
            }
        }

        /**
         * Fetch all messages from the database.
         *
         * @param latestSyncId    the currently synced to message id
         * @param cleanupInterval the configured cleanup interval
         * @return the messages
         */
        public static Map<Integer, IncomingMessage<?, ?>> receive(int latestSyncId, long cleanupInterval) {
            try (
                Connection con = DB.getConnection()
            ) {
                DSLContext context = DB.getContext(con);

                return context
                    .selectFrom(MESSAGING)
                    .where(MESSAGING.ID.greaterThan(latestSyncId)
                        .and(MESSAGING.TIMESTAMP.greaterOrEqual(localDateTimeSub(currentLocalDateTime(), cleanupInterval / 1000, DatePart.SECOND))) // Checks TIMESTAMP >= now() - cleanupInterval
                    )
                    .orderBy(MESSAGING.ID.asc())
                    .fetch()
                    .intoMap(MESSAGING.ID, r -> BidirectionalMessage.from(r.getMessage()));
            } catch (SQLException e) {
                Logger.get().error("SQL Query threw an error!" + e);
                return Map.of();
            }
        }

        /**
         * Deletes all outdate messages from the database.
         *
         * @param cleanupInterval the configured cleanup interval
         */
        public static void cleanup(long cleanupInterval) {
            try (
                Connection con = DB.getConnection()
            ) {
                DSLContext context = DB.getContext(con);

                context
                    .deleteFrom(MESSAGING)
                    .where(MESSAGING.TIMESTAMP.lessThan(localDateTimeSub(currentLocalDateTime(), cleanupInterval / 1000, DatePart.SECOND))) // Checks TIMESTAMP < now() - cleanupInterval
                    .execute();
            } catch (SQLException e) {
                Logger.get().error("SQL Query threw an error!" + e);
            }
        }
    }

    /**
     * Wrapper class to organize cooldown-related queries.
     */
    public static final class Cooldown {
        public static Map<CooldownType, Instant> load(OfflinePlayer player) {
            return load(player.getUniqueId());
        }

        public static Map<CooldownType, Instant> load(UUID uuid) {
            try (
                Connection con = DB.getConnection()
            ) {
                DSLContext context = DB.getContext(con);

                final Result<CooldownsRecord> cooldownsRecords = context
                    .selectFrom(COOLDOWNS)
                    .where(COOLDOWNS.UUID.eq(UUIDUtil.toBytes(uuid)))
                    .fetch();

                return cooldownsRecords.stream()
                    .collect(Collectors.toMap(
                        r -> CooldownType.valueOf(r.getCooldownType()),
                        r -> QueryUtils.InstantUtil.fromDateTime(r.getCooldownTime())
                    ));
            } catch (SQLException e) {
                Logger.get().error("SQL Query threw an error!", e);
            }
            return Collections.emptyMap();
        }

        public static void save(OfflinePlayer player) {
            save(player.getUniqueId());
        }

        public static void save(UUID uuid) {
            try (
                Connection con = DB.getConnection()
            ) {
                DSLContext context = DB.getContext(con);

                context.transaction(config -> {
                    DSLContext ctx = config.dsl();

                    // Delete old cooldowns
                    ctx.deleteFrom(COOLDOWNS)
                        .where(COOLDOWNS.UUID.eq(UUIDUtil.toBytes(uuid)))
                        .execute();

                    // Insert new cooldowns
                    final List<CooldownsRecord> cooldownsRecords = new ArrayList<>();

                    for (CooldownType cooldownType : CooldownType.values()) {
                        if (!Cooldowns.has(uuid, cooldownType))
                            continue;

                        cooldownsRecords.add(new CooldownsRecord(
                            UUIDUtil.toBytes(uuid),
                            cooldownType.name(),
                            QueryUtils.InstantUtil.toDateTime(Cooldowns.get(uuid, cooldownType))
                        ));
                    }

                    if (!cooldownsRecords.isEmpty())
                        ctx.batchInsert(cooldownsRecords).execute();
                });
            } catch (SQLException e) {
                Logger.get().error("SQL Query threw an error!", e);
            }
        }
    }

    public static final class Economy {
        public static void save(Collection<AccountSnapshot> accounts) {
            try (
                final Connection con = DB.getConnection()
            ) {
                final DSLContext context = DB.getContext(con);

                context.transaction(config -> {
                    final DSLContext ctx = config.dsl();

                    final List<InsertOnDuplicateSetMoreStep<AccountsRecord>> accountQueries = accounts.stream()
                        .map(account -> ctx
                            .insertInto(
                                ACCOUNTS,
                                ACCOUNTS.UUID,
                                ACCOUNTS.NAME
                            )
                            .values(
                                UUIDUtil.toBytes(account.uuid()),
                                account.name()
                            )
                            .onDuplicateKeyUpdate()
                            .set(ACCOUNTS.NAME, account.name())
                        )
                        .toList();

                    final List<InsertOnDuplicateSetMoreStep<AccountsBalanceRecord>> balanceQueries = accounts.stream()
                        .flatMap(account -> account.balances().entrySet().stream()
                            .map(entry -> ctx
                                .insertInto(
                                    ACCOUNTS_BALANCE,
                                    ACCOUNTS_BALANCE.ACCOUNT_UUID,
                                    ACCOUNTS_BALANCE.NAME,
                                    ACCOUNTS_BALANCE.BALANCE
                                )
                                .values(
                                    UUIDUtil.toBytes(account.uuid()),
                                    entry.getKey(),
                                    entry.getValue()
                                )
                                .onDuplicateKeyUpdate()
                                .set(ACCOUNTS_BALANCE.BALANCE, entry.getValue())
                            )
                        )
                        .toList();

                    ctx.batch(accountQueries).execute();
                    ctx.batch(balanceQueries).execute();
                });
            } catch (SQLException e) {
                Logger.get().error("SQL Query threw an error!", e);
            }
        }

        public static List<AccountImpl> load() {
            try (
                final Connection con = DB.getConnection()
            ) {
                final DSLContext context = DB.getContext(con);

                return context
                    .select()
                    .from(ACCOUNTS)
                    .join(ACCOUNTS_BALANCE)
                    .on(ACCOUNTS_BALANCE.ACCOUNT_UUID.eq(ACCOUNTS.UUID))
                    .fetch()
                    .intoGroups(r -> r.into(ACCOUNTS))
                    .entrySet()
                    .stream()
                    .map(e -> {
                        final AccountsRecord account = e.getKey();

                        final Map<String, BigDecimal> balances = e.getValue().stream()
                            .collect(Collectors.toMap(
                                r -> r.get(ACCOUNTS_BALANCE.NAME),
                                r -> r.get(ACCOUNTS_BALANCE.BALANCE)
                            ));

                        final DenominationHandler denominationHandler = AbstractMilkonomics.getInstance().getDenominationHandler();

                        return new AccountImpl(
                            UUIDUtil.fromBytes(account.getUuid()),
                            account.getName(),
                            denominationHandler.getDefaultDenomination(),
                            balances
                        );
                    })
                    .toList();
            } catch (SQLException e) {
                Logger.get().error("SQL Query threw an error!", e);
            }
            return Collections.emptyList();
        }
    }
}
