package io.github.milkdrinkers.milkonomicsplugin.hook.placeholderapi;

import io.github.milkdrinkers.milkonomicsplugin.MilkonomicsPlugin;
import io.github.milkdrinkers.milkonomicsplugin.hook.AbstractHook;
import io.github.milkdrinkers.milkonomicsplugin.hook.Hook;

/**
 * A hook to interface with <a href="https://wiki.placeholderapi.com/">PlaceholderAPI</a>.
 */
public class PAPIHook extends AbstractHook {
    private PAPIExpansion PAPIExpansion;

    /**
     * Instantiates a new PlaceholderAPI hook.
     *
     * @param plugin the plugin instance
     */
    public PAPIHook(MilkonomicsPlugin plugin) {
        super(plugin);
    }

    @Override
    public void onEnable(MilkonomicsPlugin plugin) {
        if (!isHookLoaded())
            return;

        PAPIExpansion = new PAPIExpansion(super.getPlugin());
        PAPIExpansion.register();
    }

    @Override
    public void onDisable(MilkonomicsPlugin plugin) {
        if (!isHookLoaded())
            return;

        PAPIExpansion.unregister();
        PAPIExpansion = null;
    }

    @Override
    public boolean isHookLoaded() {
        return isPluginPresent(Hook.PAPI.getPluginName()) && isPluginEnabled(Hook.PAPI.getPluginName());
    }
}
