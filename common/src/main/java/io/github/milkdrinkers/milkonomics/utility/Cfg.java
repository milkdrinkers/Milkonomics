package io.github.milkdrinkers.milkonomics.utility;

import io.github.milkdrinkers.milkonomics.AbstractMilkonomics;
import io.github.milkdrinkers.milkonomics.config.ConfigHandler;
import io.github.milkdrinkers.crate.Config;
import io.github.milkdrinkers.milkonomics.config.PluginConfig;
import org.jetbrains.annotations.NotNull;

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
}
