package io.github.milkdrinkers.milkonomicsplugin.api.denomination;

public interface Denomination {
    String getId();
    String getDisplayName();
    String getSymbol();
    String getPrefix();
    String getSuffix();
    String getFormat();
    String getDecimalPlaces();
    String isDefault();
}
