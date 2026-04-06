package io.github.milkdrinkers.milkonomics.economy.denomination;

import io.github.milkdrinkers.milkonomics.api.denomination.Denomination;

public record DenominationImpl(String id, String displayName, String symbol, String prefix, String suffix,
                               String format, int decimalPlaces, boolean isDefault, java.math.BigDecimal defaultBalance) implements Denomination {
}
