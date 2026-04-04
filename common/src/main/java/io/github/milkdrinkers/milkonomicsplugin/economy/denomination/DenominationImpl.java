package io.github.milkdrinkers.milkonomicsplugin.economy.denomination;

import io.github.milkdrinkers.milkonomicsplugin.api.denomination.Denomination;

public class DenominationImpl implements Denomination {
    private final String id;
    private final String displayName;
    private final String symbol;
    private final String prefix;
    private final String suffix;
    private final String format;
    private final String decimalPlaces;
    private final String isDefault;

    public DenominationImpl(String id, String displayName, String symbol, String prefix, String suffix, String format, String decimalPlaces, String isDefault) {
        this.id = id;
        this.displayName = displayName;
        this.symbol = symbol;
        this.prefix = prefix;
        this.suffix = suffix;
        this.format = format;
        this.decimalPlaces = decimalPlaces;
        this.isDefault = isDefault;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String getSymbol() {
        return symbol;
    }

    @Override
    public String getPrefix() {
        return prefix;
    }

    @Override
    public String getSuffix() {
        return suffix;
    }

    @Override
    public String getFormat() {
        return format;
    }

    @Override
    public String getDecimalPlaces() {
        return decimalPlaces;
    }

    @Override
    public String isDefault() {
        return isDefault;
    }
}
