package io.github.milkdrinkers.milkonomicsplugin.config;

import io.github.milkdrinkers.milkonomicsplugin.MilkonomicsPlugin;
import io.github.milkdrinkers.milkonomicsplugin.Reloadable;
import io.github.milkdrinkers.crate.Config;

/**
 * A class that generates/loads {@literal &} provides access to a configuration file.
 */
public class ConfigHandler implements Reloadable {
    private final MilkonomicsPlugin plugin;
    private Config cfg;
    private Config databaseCfg;

    /**
     * Instantiates a new Config handler.
     *
     * @param plugin the plugin instance
     */
    public ConfigHandler(MilkonomicsPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void onLoad(MilkonomicsPlugin plugin) {
        cfg = Config.builderConfig()
            .path(plugin.getDataFolder().toPath().resolve("config.yml"))
            .defaults(plugin.getResource("config.yml"))
            .build(); // Create a config file from the template in our resources folder
        databaseCfg = Config.builderConfig()
            .path(plugin.getDataFolder().toPath().resolve("database.yml"))
            .defaults(plugin.getResource("database.yml"))
            .build();
    }

    /**
     * Gets main config object.
     *
     * @return the config object
     */
    public Config getConfig() {
        return cfg;
    }

    /**
     * Gets database config object.
     *
     * @return the config object
     */
    public Config getDatabaseConfig() {
        return databaseCfg;
    }
}
