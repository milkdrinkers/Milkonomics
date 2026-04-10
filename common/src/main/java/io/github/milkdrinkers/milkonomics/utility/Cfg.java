package io.github.milkdrinkers.milkonomics.utility;

import io.github.milkdrinkers.crate.Config;
import io.github.milkdrinkers.milkonomics.AbstractMilkonomics;
import io.github.milkdrinkers.milkonomics.config.ConfigHandler;
import io.github.milkdrinkers.milkonomics.config.DenominationConfig;
import io.github.milkdrinkers.milkonomics.config.PluginConfig;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Convenience class for accessing {@link ConfigHandler#getConfig}
 */
public final class Cfg {
    /**
     * Convenience method for {@link ConfigHandler#getConfig} to getConnection {@link Config}
     *
     * @return the config
     */
    @NotNull
    public static PluginConfig get() {
        return AbstractMilkonomics.getInstance().getConfigHandler().getConfig();
    }

    /**
     * Convenience method for {@link ConfigHandler#getDenominationConfigs}
     *
     * @return the list of denomination configs
     */
    @NotNull
    public static List<DenominationConfig> getDenominationCfg() {
        return AbstractMilkonomics.getInstance().getConfigHandler().getDenominationConfigs();
    }

    /**
     * Convenience method for {@link ConfigHandler#getDenominationConfigs} to get the default denomination config
     *
     * @return the default denomination config, or null if one can't be found. This should never happen.
     */
    @Nullable
    public static DenominationConfig getDefaultDenominationCfg() {
        for (DenominationConfig denominationConfig : getDenominationCfg()) {
            if (denominationConfig.isDefault)
                return denominationConfig;
        }
        return null;
    }
}
