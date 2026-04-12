package io.github.milkdrinkers.milkonomics.economy.denomination;

import io.github.milkdrinkers.milkonomics.api.denomination.Denomination;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public record DenominationImpl(String id, String displayName, String displayNamePlural, String symbol, String prefix,
                               String suffix,
                               String format, int decimalPlaces, boolean isDefault,
                               java.math.BigDecimal defaultBalance) implements Denomination {


}
