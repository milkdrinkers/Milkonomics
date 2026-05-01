package io.github.milkdrinkers.milkonomics.config.loading;

import io.github.milkdrinkers.milkonomics.config.VersionedConfig;
import io.github.milkdrinkers.milkonomics.config.typeserializer.BigDecimalSerializer;
import io.github.milkdrinkers.milkonomics.config.typeserializer.DurationSerializer;
import io.github.milkdrinkers.milkonomics.config.typeserializer.LowercaseEnumSerializer;
import io.github.milkdrinkers.milkonomics.config.typeserializer.StringObjectMapSerializer;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.VisibleForTesting;
import org.slf4j.Logger;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.interfaces.InterfaceDefaultOptions;
import org.spongepowered.configurate.serialize.TypeSerializerCollection;
import org.spongepowered.configurate.transformation.ConfigurationTransformation;
import org.spongepowered.configurate.yaml.NodeStyle;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Path;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Fluent builder that loads, migrates, and validates a {@link VersionedConfig} from a YAML file.
 *
 * <p>Typical usage:
 * <pre>{@code
 * MyConfig cfg = new ConfigLoader()
 *     .withLogger(logger)
 *     .withDirectory()
 *     .withPath(dataFolder.resolve("config.yml"))
 *     .withHeader("My Plugin Configuration")
 *     .build(MyConfig.class);
 * }</pre>
 */
public class ConfigLoader {
    private Consumer<CommentedConfigurationNode> transformer;
    private final List<Consumer<TypeSerializerCollection.Builder>> extraSerializers = new ArrayList<>();
    private File file;
    private String header = "";
    private NodeStyle nodeStyle = NodeStyle.BLOCK;
    private int indent = 2;
    private boolean createDirectory = false;
    private Logger logger;

    @VisibleForTesting
    CommentedConfigurationNode configurationNode;

    @VisibleForTesting
    ConfigLoader(Path path) {
        this.file = path.toFile();
    }

    public ConfigLoader() {
    }

    public ConfigLoader withDirectory() {
        createDirectory = true;
        return this;
    }

    public ConfigLoader withPath(Path path) {
        this.file = path.toFile();
        return this;
    }

    public ConfigLoader withFile(File file) {
        return withPath(file.toPath());
    }

    public ConfigLoader withHeader(String header) {
        this.header = header;
        return this;
    }

    /**
     * @return null if the config failed to load.
     * Register additional type serializers.
     *
     * <p>Example:
     * <pre>{@code
     * .withSerializer(b -> b.registerExact(MyType.class, MyTypeSerializer.INSTANCE))
     * }</pre>
     */
    public ConfigLoader withSerializer(Consumer<TypeSerializerCollection.Builder> serializer) {
        extraSerializers.add(serializer);
        return this;
    }

    /**
     * Apply a post-load transformation to the root node.
     * The transformer runs <em>after</em> migrations and after the file has been saved,
     * so its changes are intentionally not persisted back to disk.
     */
    public ConfigLoader withTransformer(Consumer<CommentedConfigurationNode> transformer) {
        this.transformer = transformer;
        return this;
    }
    public ConfigLoader withNodeStyle(NodeStyle nodeStyle) {
        this.nodeStyle = nodeStyle;
        return this;
    }
    public ConfigLoader withIndent(int indent) {
        this.indent = indent;
        return this;
    }
    public ConfigLoader withLogger(Logger logger) {
        this.logger = logger;
        return this;
    }

    /**
     * Load, migrate, and validate the config.
     *
     * @param configClass the config class to deserialize into
     * @param <T>         a type implementing {@link VersionedConfig}
     * @return the loaded config instance, or {@code null} if loading failed
     */
    @Nullable
    public <T extends VersionedConfig> T build(Class<T> configClass) {
        try {
            return new Loader().loadInternal(configClass);
        } catch (IOException e) {
            if (logger != null)
                logger.error("Failed to load config file of type: {}", configClass.getSimpleName(), e);
            return null;
        }
    }

    private class Loader {
        private <T extends VersionedConfig> T loadInternal(Class<T> configClass) throws ConfigurateException {
            if (createDirectory && !file.getParentFile().exists()) {
                if (!file.getParentFile().mkdirs()) {
                    if (logger != null)
                        logger.error("Failed to create directory for config file at {}", file.getAbsolutePath());
                }
            }

            final YamlConfigurationLoader loader = createLoader(file);
            final CommentedConfigurationNode node = loader.load();
            final boolean originallyEmpty = !file.exists() || node.isNull();

            int currentVersion = -1;
            int newVersion = -1;
            try {
                final T blank = configClass.getDeclaredConstructor().newInstance();
                if (!blank.migrations().isEmpty()) {
                    final ConfigurationTransformation.Versioned migrator = blank.migrator();
                    currentVersion = migrator.version(node);
                    migrator.apply(node);
                    newVersion = migrator.version(node);
                }
            } catch (ReflectiveOperationException e) {
                if (logger != null)
                    logger.warn(
                        "Could not instantiate {} to apply migrations — skipping",
                        configClass.getSimpleName()
                    );
            }

            T config = node.get(configClass);

            // Serialize the instance to ensure strict field ordering. Additionally, if we serialized back
            // to the old node, existing nodes would only have their value changed, keeping their position
            // at the top of the ordered map, forcing all new nodes to the bottom (regardless of field order).
            // For that reason, we must also create a new node.
            final CommentedConfigurationNode newRoot = CommentedConfigurationNode.root(loader.defaultOptions());
            newRoot.set(config);

            if (originallyEmpty || currentVersion != newVersion) {
                loader.save(newRoot);
            }

            // We transform AFTER saving so that these specific transformations aren't applied to file.
            if (transformer != null) {
                transformer.accept(newRoot);
                config = newRoot.get(configClass);
            }

            ConfigLoader.this.configurationNode = newRoot;

            if (config != null) {
                config.validate();
            }

            return config;
        }

        @VisibleForTesting
        CommentedConfigurationNode loadConfigurationNode(Class<? extends VersionedConfig> configClass) throws ConfigurateException {
            loadInternal(configClass);
            return configurationNode.copy();
        }

        private YamlConfigurationLoader createLoader(File file) {
            final TypeSerializerCollection.Builder serializerBuilder = TypeSerializerCollection.defaults().childBuilder()
                .register(LowercaseEnumSerializer.INSTANCE)
                .registerExact(StringObjectMapSerializer.TYPE_TOKEN, StringObjectMapSerializer.INSTANCE)
                .registerExact(BigDecimal.class, BigDecimalSerializer.INSTANCE)
                .registerExact(Duration.class, DurationSerializer.INSTANCE);

            for (Consumer<TypeSerializerCollection.Builder> extra : extraSerializers) {
                extra.accept(serializerBuilder);
            }

            return YamlConfigurationLoader.builder()
                .file(file)
                .indent(indent)
                .nodeStyle(nodeStyle)
                .defaultOptions(options -> InterfaceDefaultOptions.addTo(options, builder -> {})
                    .shouldCopyDefaults(false)
                    .header(header)
                    .serializers(builder -> builder.registerAll(serializerBuilder.build()))
                )
                .build();
        }
    }
}
