package io.github.milkdrinkers.milkonomicsplugin.config;

import io.github.milkdrinkers.milkonomicsplugin.AbstractMilkonomicsPlugin;
import io.github.milkdrinkers.milkonomicsplugin.Reloadable;
import io.github.milkdrinkers.milkonomicsplugin.config.loading.ConfigLoader;
import org.slf4j.Logger;

import java.io.File;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * A class that generates/loads {@literal &} provides access to a configuration file.
 */
public class ConfigHandler implements Reloadable {
    private final AbstractMilkonomicsPlugin plugin;
    private final Path configDir;
    private final Logger logger;

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
        this.configDir = plugin.getDataFolder().toPath();
        this.logger = plugin.getComponentLogger();
    }

    public ConfigHandler(Milkonomics plugin, Path configDir, Logger logger) {
        this.plugin = plugin;
        this.configDir = configDir;
        this.logger = logger;
    }

    @Override
    public void onLoad(AbstractMilkonomicsPlugin plugin) {
        cfg = new ConfigLoader()
            .withLogger(logger)
            .withDirectory()
            .withPath(configDir.resolve("config.yml"))
            .withHeader("")
            .build(PluginConfig.class);

        databaseCfg = new ConfigLoader()
            .withLogger(logger)
            .withDirectory()
            .withPath(configDir.resolve("database.yml"))
            .withHeader("")
            .build(DatabaseConfig.class);

        denominationConfigs = loadDenominations(plugin);
    }

    private List<DenominationConfig> loadDenominations(Milkonomics plugin) {
        final File denominationsPath = configDir.resolve("denominations").toFile();
        if (!denominationsPath.mkdirs() && !denominationsPath.isDirectory()) {
            logger.error("Failed to create denominations directory at {}", denominationsPath.getAbsolutePath());
            return List.of();
        }

        final File[] files = denominationsPath.listFiles();
        if (files == null) {
            logger.error("Failed to get files in denominations directory at {}", denominationsPath.getAbsolutePath());
            return List.of();
        }

        if (files.length == 0) {
            logger.warn("No denomination config files found in {}, creating default file.", denominationsPath.getAbsolutePath());

            final DenominationConfig cfg = new ConfigLoader()
                .withLogger(logger)
                .withDirectory()
                .withPath(denominationsPath.toPath().resolve("dollar.yml"))
                .withHeader("")
                .build(DenominationConfig.class);

            return List.of(cfg);
        }

        return Arrays.stream(files)
            .filter(file -> file.isFile() && file.getName().endsWith(".yml"))
            .map(file -> new ConfigLoader()
                .withDirectory()
                .withFile(file)
                .withHeader("")
                .build(DenominationConfig.class))
            .filter(Objects::nonNull)
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
