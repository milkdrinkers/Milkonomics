package io.github.milkdrinkers.milkonomicsplugin.config;

import io.github.milkdrinkers.milkonomicsplugin.AbstractMilkonomicsPlugin;
import io.github.milkdrinkers.milkonomicsplugin.Reloadable;
import io.github.milkdrinkers.milkonomicsplugin.config.loading.ConfigLoader;
import io.github.milkdrinkers.milkonomicsplugin.utility.Logger;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * A class that generates/loads {@literal &} provides access to a configuration file.
 */
public class ConfigHandler implements Reloadable {
    private final AbstractMilkonomicsPlugin plugin;
    private PluginConfig cfg;
    private DatabaseConfig databaseCfg;
    private List<DenominationConfig> denominationConfigs;

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

        denominationConfigs = loadDenominations(plugin);
    }

    private List<DenominationConfig> loadDenominations(AbstractMilkonomicsPlugin plugin) {
        final File denominationsPath = plugin.getDataFolder().toPath().resolve("denominations").toFile();
        if (!denominationsPath.mkdirs() || !denominationsPath.isDirectory()) {
            Logger.get().error("Failed to create denominations directory at {}", denominationsPath.getAbsolutePath());
            return List.of();
        }

        final File[] files = denominationsPath.listFiles();
        if (files == null) {
            Logger.get().error("Failed to get files in denominations directory at {}", denominationsPath.getAbsolutePath());
            return List.of();
        }

        if (files.length == 0) {
            Logger.get().warn("No denomination config files found in {}, creating default file.", denominationsPath.getAbsolutePath());

            return List.of(Objects.requireNonNull(new ConfigLoader()
                .withDirectory()
                .withFile(denominationsPath.toPath().resolve("dollar.yml").toFile())
                .withHeader("")
                .build(DenominationConfig.class)));
        }

        return Arrays.stream(files)
            .filter(file -> file.isFile() && file.getName().endsWith(".yml"))
            .map(file -> new ConfigLoader()
                .withDirectory()
                .withFile(file)
                .withHeader("")
                .build(DenominationConfig.class))
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

    /**
     * Gets the list of denomination configs.
     *
     * @return the list of denomination configs
     */
    public List<DenominationConfig> getDenominationConfigs() {
        return denominationConfigs;
    }
}
