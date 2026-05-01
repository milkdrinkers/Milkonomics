package io.github.milkdrinkers.milkonomics.config;

import io.github.milkdrinkers.milkonomics.config.exception.ConfigValidationException;
import org.spongepowered.configurate.interfaces.meta.Exclude;
import org.spongepowered.configurate.objectmapping.meta.Comment;
import org.spongepowered.configurate.transformation.ConfigurationTransformation;

import java.util.Map;

/**
 * Base interface for all versioned configuration files.
 * <p>
 * Implementors should override {@link #migrations()} to provide version-to-version migrations,
 * and may override {@link #validate()} to enforce constraints on loaded values.
 */
public interface VersionedConfig {
    @Comment("Do not change this value!")
    @SuppressWarnings("unused")
    int configVersion();

    /**
     * Provides a map of version-to-transformation migrations.
     * The key is the <em>target</em> version; the value is the {@link ConfigurationTransformation}
     * that upgrades a node from the previous version to that version.
     *
     * <p>Example — rename a key when migrating from v1 to v2:
     * <pre>{@code
     * @Override
     * public Map<Integer, ConfigurationTransformation> migrations() {
     *     return Map.of(
     *         2, ConfigurationTransformation.builder()
     *             .addAction(NodePath.path("old-key"), MigrationUtil.renameAndMove("new-key"))
     *             .build()
     *     );
     * }
     * }</pre>
     *
     * @return map of target version → transformation
     */
    @Exclude
    default Map<Integer, ConfigurationTransformation> migrations() {
        return Map.of();
    }

    /**
     * Builds the versioned migrator used by {@link io.github.milkdrinkers.milkonomics.config.loading.ConfigLoader}
     * to upgrade on-disk config nodes before deserialization.
     * <p>
     * Override {@link #migrations()} rather than this method.
     *
     * @return a {@link ConfigurationTransformation.Versioned} built from {@link #migrations()}
     */
    @Exclude
    default ConfigurationTransformation.Versioned migrator() {
        final ConfigurationTransformation.VersionedBuilder builder = ConfigurationTransformation.versionedBuilder()
            .versionKey("configVersion");

        for (Map.Entry<Integer, ConfigurationTransformation> entry : migrations().entrySet()) {
            builder.addVersion(entry.getKey(), entry.getValue());
        }

        return builder.build();
    }

    /**
     * Validates the config after loading.
     * <p>
     * Override this method and throw {@link ConfigValidationException} when a value is invalid.
     * The loader will propagate the exception and treat the config as failed.
     *
     * @throws ConfigValidationException if any field value is unacceptable
     */
    @Exclude
    default void validate() throws ConfigValidationException {
    }
}
