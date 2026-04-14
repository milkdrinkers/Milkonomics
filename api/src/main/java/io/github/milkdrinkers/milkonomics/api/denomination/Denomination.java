package io.github.milkdrinkers.milkonomics.api.denomination;

import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Represents a denomination/currency, such as "dollar" or "euro".
 */
public interface Denomination {
    /**
     * Gets the unique ID of this denomination. This should be unique for all denominations.
     * @return the unique ID of this denomination
     */
    @NotNull String id();

    /**
     * Gets the display name of this denomination, used for singular amounts (e.g. "dollar").
     * @return the display name of this denomination
     */
    @NotNull String displayName();

    /**
     * Gets the display name of this denomination, used for plural amounts (e.g. "dollars").
     * @return the plural display name of this denomination
     */
    @NotNull String displayNamePlural();

    /**
     * Gets the symbol of this denomination (e.g. "$").
     * @return the symbol of this denomination
     */
    @NotNull String symbol();

    /**
     * Gets the prefix to use when formatting amounts of this denomination (e.g. "$" for dollars).
     * @return the prefix to use when formatting amounts of this denomination
     */
    @NotNull String prefix();

    /**
     * Gets the suffix to use when formatting amounts of this denomination (e.g. "€" for euros).
     * @return the suffix to use when formatting amounts of this denomination
     */
    @NotNull String suffix();

    /**
     * Gets the format to use when formatting amounts of this denomination (e.g. "%s%s" for dollars).
     * The format string should contain two "%s" placeholders, one for the prefix and one for the formatted amount with suffix.
     * @return the format string to use when formatting amounts of this denomination
     */
    @NotNull String format();

    /**
     * Gets the number of decimal places to use when formatting amounts of this denomination (e.g. 2 results in "0.00").
     * @return the number of decimal places to use when formatting amounts of this denomination
     */
    int decimalPlaces();

    /**
     * Gets whether this denomination is the default denomination of the plugin/mod.
     * @return whether this denomination is the default denomination
     * @apiNote If multiple default denominations are registered, the behavior is undefined as the first one loaded is set as the default one.
     */
    boolean isDefault();

    /**
     * Gets the default balance for this denomination, used when creating new accounts.
     * @return the default balance for this denomination
     */
    @NotNull BigDecimal defaultBalance();

    ThreadLocal<Map<Integer, DecimalFormat>> FORMATTERS = ThreadLocal.withInitial(HashMap::new);

    /**
     * Formats the given amount according to this denomination's formatting rules.
     * @param amount the amount to format
     * @return the formatted amount as a string
     */
    default @NotNull String format(@NotNull BigDecimal amount) {
        final DecimalFormat df = FORMATTERS.get().computeIfAbsent(decimalPlaces(), Denomination::buildFormat);
        return String.format(format(), prefix(), df.format(amount), suffix());
    }

    private static @NotNull DecimalFormat buildFormat(int decimals) {
        final String pattern = decimals > 0 ? "#,##0." + "0".repeat(decimals) : "#,##0";
        final DecimalFormat df = new DecimalFormat(pattern, DecimalFormatSymbols.getInstance(Locale.US));
        df.setRoundingMode(RoundingMode.HALF_UP);
        df.setParseBigDecimal(true);
        return df;
    }
}
