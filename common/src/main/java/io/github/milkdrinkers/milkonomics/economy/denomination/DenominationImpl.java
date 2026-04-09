package io.github.milkdrinkers.milkonomics.economy.denomination;

import io.github.milkdrinkers.milkonomics.api.denomination.Denomination;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public record DenominationImpl(String id, String displayName, String displayNamePlural, String symbol, String prefix, String suffix,
                               String format, int decimalPlaces, boolean isDefault, java.math.BigDecimal defaultBalance) implements Denomination {
    private static final ThreadLocal<Map<Integer, DecimalFormat>> FORMATTERS = ThreadLocal.withInitial(HashMap::new);

    public String format(BigDecimal amount) {
        final DecimalFormat df = FORMATTERS.get().computeIfAbsent(decimalPlaces, DenominationImpl::buildFormat);
        return String.format(format, prefix, df.format(amount), suffix);
    }

    private static DecimalFormat buildFormat(int decimals) {
        final String pattern = decimals > 0 ? "#,##0." + "0".repeat(decimals) : "#,##0";
        final DecimalFormat df = new DecimalFormat(pattern, DecimalFormatSymbols.getInstance(Locale.US));
        df.setRoundingMode(RoundingMode.HALF_UP);
        df.setParseBigDecimal(true);
        return df;
    }
}
