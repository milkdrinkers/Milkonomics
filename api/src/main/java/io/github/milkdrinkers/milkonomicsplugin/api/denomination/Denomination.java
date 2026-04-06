package io.github.milkdrinkers.milkonomicsplugin.api.denomination;

import java.math.BigDecimal;

public interface Denomination {
    String id();
    String displayName();
    String symbol();
    String prefix();
    String suffix();
    String format();
    int decimalPlaces();
    boolean isDefault();
    BigDecimal defaultBalance();
}
