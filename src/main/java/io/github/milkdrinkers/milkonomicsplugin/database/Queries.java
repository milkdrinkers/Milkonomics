package io.github.milkdrinkers.milkonomicsplugin.database;

import io.github.milkdrinkers.milkonomicsplugin.cooldown.CooldownType;
import io.github.milkdrinkers.milkonomicsplugin.cooldown.Cooldowns;
import io.github.milkdrinkers.milkonomicsplugin.database.schema.tables.records.AccountsRecord;
import io.github.milkdrinkers.milkonomicsplugin.database.schema.tables.records.CooldownsRecord;
import io.github.milkdrinkers.milkonomicsplugin.economy.account.Account;
import io.github.milkdrinkers.milkonomicsplugin.economy.account.AccountImpl;
import io.github.milkdrinkers.milkonomicsplugin.economy.account.AccountSnapshot;
import io.github.milkdrinkers.milkonomicsplugin.messaging.message.BidirectionalMessage;
import io.github.milkdrinkers.milkonomicsplugin.messaging.message.IncomingMessage;
import io.github.milkdrinkers.milkonomicsplugin.messaging.message.OutgoingMessage;
import io.github.milkdrinkers.milkonomicsplugin.utility.DB;
import io.github.milkdrinkers.milkonomicsplugin.utility.Logger;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.ApiStatus;
import org.jooq.*;

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
                Connection con = DB.getConnection()
            ) {
                DSLContext context = DB.getContext(con);

                context.transaction(config -> {
                    DSLContext ctx = config.dsl();

                    final List<InsertOnDuplicateSetMoreStep<AccountsRecord>> queries = accounts.stream().map(account -> ctx
                        .insertInto(
                            ACCOUNTS,
                            ACCOUNTS.UUID,
                            ACCOUNTS.NAME,
                            ACCOUNTS.BALANCE
                        )
                        .values(
                            UUIDUtil.toBytes(account.uuid()),
                            account.name(),
                            account.balance()
                        )
                        .onDuplicateKeyUpdate()
                        .set(ACCOUNTS.NAME, account.name())
                        .set(ACCOUNTS.BALANCE, account.balance())
                    )
                        .toList();

                    ctx.batch(queries)
                        .execute();
                });
            } catch (SQLException e) {
                Logger.get().error("SQL Query threw an error!", e);
            }
        }

        public static List<Account> load() {
            try (
                Connection con = DB.getConnection()
            ) {
                DSLContext context = DB.getContext(con);

                return context
                    .selectFrom(ACCOUNTS)
                    .fetch()
                    .map(r -> new AccountImpl(
                        UUIDUtil.fromBytes(r.getUuid()),
                        r.getName(),
                        r.getBalance()
                    ));
            } catch (SQLException e) {
                Logger.get().error("SQL Query threw an error!", e);
            }
            return Collections.emptyList();
        }
    }
}
