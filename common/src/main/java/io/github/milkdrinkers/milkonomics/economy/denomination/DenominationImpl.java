package io.github.milkdrinkers.milkonomics.economy.denomination;

import io.github.milkdrinkers.milkonomics.api.denomination.Denomination;

import java.math.BigDecimal;

public record DenominationImpl(
    String id,
    String displayName,
    String displayNamePlural,
    String symbol,
    String prefix,
    String suffix,
    String format,
    int decimalPlaces,
    boolean isDefault,
    BigDecimal defaultBalance
) implements Denomination {
}
