package io.github.milkdrinkers.milkonomicsplugin.utility;

import io.github.milkdrinkers.milkonomicsplugin.AbstractMilkonomicsPlugin;
import io.github.milkdrinkers.milkonomicsplugin.config.ConfigHandler;
import io.github.milkdrinkers.crate.Config;
import io.github.milkdrinkers.milkonomicsplugin.config.PluginConfig;
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
        return AbstractMilkonomicsPlugin.getInstance().getConfigHandler().getConfig();
    }
}
