package io.github.milkdrinkers.milkonomicsplugin.economy;

import io.github.milkdrinkers.milkonomicsplugin.MilkonomicsPlugin;
import io.github.milkdrinkers.milkonomicsplugin.api.account.Account;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.Nullable;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;

public final class EconomyUtil {
    public static Account getUUIDFromCache(String accountId) {
        try {
            // If the accountId is a UUID, get the account using the UUID
            final UUID uuid = UUID.fromString(accountId);
            
            return MilkonomicsPlugin
                .getInstance()
                .getAccountManager()
                .getAccount(uuid)
                .orElse(
                    null
                );
        } catch (IllegalArgumentException e) {
            // Try to get the account using the accountId as a name
            final @Nullable Account acc = MilkonomicsPlugin
                .getInstance()
                .getAccountManager()
                .getAccount(accountId)
                .orElse(null);
            
            if (acc == null) {
                // Tru to get a player UUID using the accountId as a player name
                final UUID uuid = Objects.requireNonNull(Bukkit.getOfflinePlayerIfCached(accountId), "UUID is null").getUniqueId();
                
                return getUUIDFromCache(uuid);
            }
            
            return acc;
        }
    }

    public static Account getUUIDFromCache(UUID uuid) {
        return Objects.requireNonNull(MilkonomicsPlugin
            .getInstance()
            .getAccountManager()
            .getAccount(uuid)
            .orElse(null), "Account is null");
    }

    public static boolean isNegative(BigDecimal amount) {
        return amount.compareTo(BigDecimal.ZERO) < 0;
    }

    public static boolean exceedsAccountLimit(BigDecimal amount) {
        return amount.compareTo(BigDecimal.valueOf(Double.MAX_VALUE)) > 0; // TODO Use a configurable account limit
    }
}
