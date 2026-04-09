package io.github.milkdrinkers.milkonomics.config;

import io.github.milkdrinkers.milkonomics.config.common.VersionedConfig;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;
import org.spongepowered.configurate.transformation.ConfigurationTransformation;

import java.math.BigDecimal;
import java.util.Map;

@ConfigSerializable
public class DenominationConfig implements VersionedConfig {
    @Comment("The unique identifier for this denomination, for example 'dollar' or 'euro'. This should be lowercase, without spaces, and must be unique.")
    public String id = "dollar";

    @Comment("The display name for this denomination, for example 'Dollar' or 'Euro'.")
    public String displayName = "Dollar";

    @Comment("The display name (plural) for this denomination, for example 'Dollars' or 'Euros'.")
    public String displayNamePlural = "Dollars";

    @Comment("The symbol to use for this denomination, for example $ or €.")
    public String symbol = "$";

    @Comment("Optional prefix to the amount, e.g. \"$\" for $100.00")
    public String prefix = "";

    @Comment("Optional suffix to the amount, e.g. \"$\" for 100.00$")
    public String suffix = "";

    @Comment("Default format is symbol followed by amount, e.g. $100.00")
    public String format = "%s%s";

    @Comment("The number of decimal places to use for this denomination.")
    public int decimalPlaces = 2;

    @Comment("Whether this denomination is the default denomination used in transactions.")
    public boolean isDefault = true;

    @Comment("The default balance each account starts with.")
    public BigDecimal defaultBalance = BigDecimal.valueOf(0.0);

    @Override
    public int configVersion() {
        return 1;
    }

    @Override
    public Map<Integer, ConfigurationTransformation> migrations() {
        return Map.of();
    }
}
