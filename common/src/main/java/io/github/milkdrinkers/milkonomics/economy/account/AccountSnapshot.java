package io.github.milkdrinkers.milkonomics.economy.account;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

public record AccountSnapshot(UUID uuid, String name, Map<String, BigDecimal> balances,
                              boolean acceptingTransactions) implements io.github.milkdrinkers.milkonomics.api.account.AccountSnapshot {
}
