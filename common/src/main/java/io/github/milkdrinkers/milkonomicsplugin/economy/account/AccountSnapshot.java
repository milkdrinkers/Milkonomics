package io.github.milkdrinkers.milkonomicsplugin.economy.account;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

public record AccountSnapshot(UUID uuid, String name, Map<String, BigDecimal> balances) implements io.github.milkdrinkers.milkonomicsplugin.api.account.AccountSnapshot {
}
