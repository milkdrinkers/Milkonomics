package io.github.milkdrinkers.milkonomicsplugin.economy.account;

import java.math.BigDecimal;
import java.util.UUID;

public record AccountSnapshot(UUID uuid, String name, BigDecimal balance) implements io.github.milkdrinkers.milkonomicsplugin.api.account.AccountSnapshot {
}
