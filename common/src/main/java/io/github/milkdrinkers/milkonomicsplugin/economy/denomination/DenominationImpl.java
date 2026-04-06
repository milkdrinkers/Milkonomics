package io.github.milkdrinkers.milkonomicsplugin.economy.denomination;

import io.github.milkdrinkers.milkonomicsplugin.api.denomination.Denomination;

public record DenominationImpl(String id, String displayName, String symbol, String prefix, String suffix,
                               String format, int decimalPlaces, boolean isDefault, java.math.BigDecimal defaultBalance) implements Denomination {
}
