package io.github.milkdrinkers.milkonomicsplugin.config;

import io.github.milkdrinkers.milkonomicsplugin.AbstractMilkonomicsPlugin;
import io.github.milkdrinkers.milkonomicsplugin.Reloadable;
import io.github.milkdrinkers.milkonomicsplugin.config.loading.ConfigLoader;
import io.github.milkdrinkers.milkonomicsplugin.utility.Logger;

import java.io.File;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

/**
 * A class that generates/loads {@literal &} provides access to a configuration file.
 */
public class ConfigHandler implements Reloadable {
    private final AbstractMilkonomicsPlugin plugin;
    private PluginConfig cfg;
    private DatabaseConfig databaseCfg;

    /**
     * Instantiates a new Config handler.
     *
     * @param plugin the plugin instance
     */
    public ConfigHandler(AbstractMilkonomicsPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void onLoad(AbstractMilkonomicsPlugin plugin) {
//        cfg = Config.builderConfig()
//            .path(plugin.getDataFolder().toPath().resolve("config.yml"))
//            .defaults(plugin.getResource("config.yml"))
//            .reload(ReloadSetting.MANUALLY)
//            .build(); // Create a config file from the template in our resources folder
//        databaseCfg = Config.builderConfig()
//            .path(plugin.getDataFolder().toPath().resolve("database.yml"))
//            .defaults(plugin.getResource("database.yml"))
//            .reload(ReloadSetting.MANUALLY)
//            .build();
        cfg = new ConfigLoader()
            .withDirectory()
            .withPath(plugin.getDataFolder().toPath().resolve("config.yml"))
            .withHeader("")
            .build(PluginConfig.class);

        databaseCfg = new ConfigLoader()
            .withDirectory()
            .withPath(plugin.getDataFolder().toPath().resolve("database.yml"))
            .withHeader("")
            .build(DatabaseConfig.class);



    }

    private List<DenominationConfig> loadDenominations(AbstractMilkonomicsPlugin plugin) {
        final File denominationsPath = plugin.getDataFolder().toPath().resolve("denominations").toFile();
        if (!denominationsPath.mkdirs() || !denominationsPath.isDirectory()) {
            Logger.get().error("Failed to create denominations directory at {}", denominationsPath.getAbsolutePath());
            return List.of();
        }

        return Arrays.stream(denominationsPath.listFiles())
            .filter(file -> file.isFile() && file.getName().endsWith(".yml"))
            .map(file -> {
                    return new ConfigLoader()
                        .withDirectory()
                        .withFile(file)
                        .withHeader("")
                        .build(DenominationConfig.class);
                })
            .toList();
    }

    /**
     * Gets main config object.
     *
     * @return the config object
     */
    public PluginConfig getConfig() {
        return cfg;
    }

    /**
     * Gets database config object.
     *
     * @return the config object
     */
    public DatabaseConfig getDatabaseConfig() {
        return databaseCfg;
    }
}
