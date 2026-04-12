package io.github.milkdrinkers.milkonomics.api.denomination;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public interface Denomination {
    String id();

    String displayName();

    String displayNamePlural();

    String symbol();

    String prefix();

    String suffix();

    String format();

    int decimalPlaces();

    boolean isDefault();

    BigDecimal defaultBalance();

    ThreadLocal<Map<Integer, DecimalFormat>> FORMATTERS = ThreadLocal.withInitial(HashMap::new);

    default String format(BigDecimal amount) {
        final DecimalFormat df = FORMATTERS.get().computeIfAbsent(decimalPlaces(), Denomination::buildFormat);
        return String.format(format(), prefix(), df.format(amount), suffix());
    }

    private static DecimalFormat buildFormat(int decimals) {
        final String pattern = decimals > 0 ? "#,##0." + "0".repeat(decimals) : "#,##0";
        final DecimalFormat df = new DecimalFormat(pattern, DecimalFormatSymbols.getInstance(Locale.US));
        df.setRoundingMode(RoundingMode.HALF_UP);
        df.setParseBigDecimal(true);
        return df;
    }
}
