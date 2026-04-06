package io.github.milkdrinkers.milkonomics.config.loading;

import io.github.milkdrinkers.milkonomics.config.common.VersionedConfig;
import io.github.milkdrinkers.milkonomics.config.typeserializer.BigDecimalSerializer;
import io.github.milkdrinkers.milkonomics.config.typeserializer.StringObjectMapSerializer;
import io.leangen.geantyref.TypeToken;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.VisibleForTesting;
import org.slf4j.Logger;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.ConfigurationVisitor;
import org.spongepowered.configurate.interfaces.InterfaceDefaultOptions;
import org.spongepowered.configurate.serialize.ScalarSerializer;
import org.spongepowered.configurate.serialize.Scalars;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.yaml.NodeStyle;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.nio.file.Path;
import java.util.Locale;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class ConfigLoader {
    private Consumer<CommentedConfigurationNode> transformer;
    private File file;
    private String header = "";
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

    public ConfigLoader withTransformer() throws NoSuchMethodException {
        // TODO implement
        throw new NoSuchMethodException("Not yet implemented");
    }

    public ConfigLoader withLogger(Logger logger) {
        this.logger = logger;
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
            if (createDirectory) {
                if (!file.getParentFile().exists()) {
                    if (!file.getParentFile().mkdirs()) {
                        if (logger != null)
                            logger.error("Failed to create directory for config file at {}", file.getAbsolutePath());
                    }
                }
            }

            final YamlConfigurationLoader loader = createLoader(file);

            final CommentedConfigurationNode node = loader.load();
            final boolean originallyEmpty = !file.exists() || node.isNull();

//            ConfigurationTransformation.Versioned migrations;
//            try {
//                // noinspection unchecked
//                final Function<Class<? extends VersionedConfig>, ConfigurationTransformation.Versioned> migrateFunc = (Function<Class<? extends VersionedConfig>, ConfigurationTransformation.Versioned>) configClass.getMethod("migrator").invoke(new Object());
//                migrations = migrateFunc.apply(configClass);
//            } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
//                throw new RuntimeException(e);
//            }
//
//            final int currentVersion = migrations.version(node);
//            migrations.apply(node);
//            final int newVersion = migrations.version(node);

            T config = node.get(configClass);

            // Serialize the instance to ensure strict field ordering. Additionally, if we serialized back
            // to the old node, existing nodes would only have their value changed, keeping their position
            // at the top of the ordered map, forcing all new nodes to the bottom (regardless of field order).
            // For that reason, we must also create a new node.
            final CommentedConfigurationNode newRoot = CommentedConfigurationNode.root(loader.defaultOptions());
            newRoot.set(config);

            if (originallyEmpty /*|| currentVersion != newVersion*/) {
                // Only copy comments over if the file already existed
//                if (!originallyEmpty) {
//                    ConfigurationCommentMover.moveComments(node, newRoot);
//                }

                loader.save(newRoot);
            }

            // We transform AFTER saving so that these specific transformations aren't applied to file.
            if (transformer != null) {
                transformer.accept(newRoot);
                config = newRoot.get(configClass);
            }

            // For testing
//            this.configurationNode = newRoot;

            return config;
        }

        @VisibleForTesting
        CommentedConfigurationNode loadConfigurationNode(Class<? extends VersionedConfig> configClass) throws ConfigurateException {
            loadInternal(configClass);
            return configurationNode.copy();
        }

        private YamlConfigurationLoader createLoader(File file) {
            final TypeToken<Map<String, Object>> stringObjectMapToken = new TypeToken<>() { // Used to serialize/deserialize Map<String, Object> objects
                @Override
                public Type getType() {
                    return new ParameterizedType() {
                        @Override
                        public Type @NotNull [] getActualTypeArguments() {
                            return new Type[]{String.class, Object.class};
                        }

                        @Override
                        public @NotNull Type getRawType() {
                            return Map.class;
                        }

                        @Override
                        public Type getOwnerType() {
                            return null;
                        }
                    };
                }
            };

            return YamlConfigurationLoader.builder()
                .file(file)
                .indent(2)
                .nodeStyle(NodeStyle.BLOCK)
                .defaultOptions(options -> InterfaceDefaultOptions.addTo(options, builder -> {})
                    .shouldCopyDefaults(false) // If we use ConfigurationNode#get(type, default), do not write the default back to the node.
                    .header(header)
                    .serializers(builder -> {
                        builder.register(new LowercaseEnumSerializer());
                        builder.registerExact(stringObjectMapToken, new StringObjectMapSerializer());
                        builder.registerExact(BigDecimal.class, BigDecimalSerializer.INSTANCE);
                    })
                )
                .build();
        }

        /**
         * Copyright (c) 2024 GeyserMC. <a href="http://geysermc.org">geysermc.org</a>
         * <p>
         * Permission is hereby granted, free of charge, to any person obtaining a copy
         * of this software and associated documentation files (the "Software"), to deal
         * in the Software without restriction, including without limitation the rights
         * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
         * copies of the Software, and to permit persons to whom the Software is
         * furnished to do so, subject to the following conditions:
         * <p>
         * The above copyright notice and this permission notice shall be included in
         * all copies or substantial portions of the Software.
         * <p>
         * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
         * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
         * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
         * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
         * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
         * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
         * THE SOFTWARE.
         * <p>
         * Ensures enum values are written to lowercase. {@link Scalars#ENUM} will read enum values in any case.
         * @author GeyserMC
         * @link <a href="https://github.com/GeyserMC/Geyser">Github</a>
         */
        private static final class LowercaseEnumSerializer extends ScalarSerializer<Enum<?>> {
            LowercaseEnumSerializer() {
                super(new TypeToken<>() {});
            }

            @Override
            public Enum<?> deserialize(Type type, Object obj) throws SerializationException {
                return Scalars.ENUM.deserialize(type, obj);
            }

            @Override
            protected Object serialize(Enum<?> item, Predicate<Class<?>> typeSupported) {
                return item.name().toLowerCase(Locale.ROOT);
            }
        }

        /**
         * Copyright (c) 2024 GeyserMC. <a href="http://geysermc.org">geysermc.org</a>
         * <p>
         * Permission is hereby granted, free of charge, to any person obtaining a copy
         * of this software and associated documentation files (the "Software"), to deal
         * in the Software without restriction, including without limitation the rights
         * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
         * copies of the Software, and to permit persons to whom the Software is
         * furnished to do so, subject to the following conditions:
         * <p>
         * The above copyright notice and this permission notice shall be included in
         * all copies or substantial portions of the Software.
         * <p>
         * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
         * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
         * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
         * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
         * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
         * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
         * THE SOFTWARE.
         * <p>
         * Moves comments from a different node and puts them on this node.
         * @author GeyserMC
         * @link <a href="https://github.com/GeyserMC/Geyser">Github</a>
         */
        private record ConfigurationCommentMover(CommentedConfigurationNode otherRoot) implements ConfigurationVisitor.Stateless<RuntimeException> {
            private ConfigurationCommentMover(@NotNull CommentedConfigurationNode otherRoot) {
                this.otherRoot = otherRoot;
            }

            @Override
            public void enterNode(final ConfigurationNode node) {
                if (!(node instanceof CommentedConfigurationNode destination)) {
                    throw new IllegalStateException(node.path() + " is not a CommentedConfigurationNode"); // Should not occur because all nodes in a tree are the same type, and our static method below ensures this visitor is only used on CommentedConfigurationNodes
                }

                final CommentedConfigurationNode source = otherRoot.node(node.path()); // Node with the same path

                moveSingle(source, destination);
            }

            private static void moveSingle(@NotNull CommentedConfigurationNode source, @NotNull CommentedConfigurationNode destination) {
                final String comment = source.comment();
                if (comment != null) {
                    destination.comment(comment);
                }
            }

            /**
             * Moves comments from a source node and its children to a destination node and its children (of a different tree), overriding if necessary.
             * Comments are only moved to the destination node and its children which exist.
             * Comments are only moved to and from nodes with the exact same path.
             *
             * @param source the source of the comments, which must be the topmost parent of a tree.
             * @param destination the destination of the comments, any node in a different tree.
             */
            public static void moveComments(@NotNull CommentedConfigurationNode source, @NotNull CommentedConfigurationNode destination) {
                if (source.parent() != null) {
                    throw new IllegalArgumentException("source is not the base of the tree it is within: " + source.path());
                }

                if (source.isNull()) { // It has no value(s), but may still have a comment on it. Don't bother traversing the whole destination tree.
                    moveSingle(source, destination);
                } else {
                    destination.visit(new ConfigurationCommentMover(source));
                }
            }
        }
    }
}
