package io.github.milkdrinkers.milkonomicsplugin.config;

import io.github.milkdrinkers.milkonomicsplugin.config.common.VersionedConfig;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;
import org.spongepowered.configurate.transformation.ConfigurationTransformation;

import java.util.function.Function;

@ConfigSerializable
public class PluginConfig implements VersionedConfig {
//    @Comment("Should the plugin check for plugin updates on startup?")
//    public boolean updateChecker = true;

//    @Comment("Do not change this value!")
//    public int configVersion = 1;

    @Comment("Update Checker Settings")
    public UpdateChecker updateChecker = new UpdateChecker();

    @ConfigSerializable
    public static class UpdateChecker {
        @Comment("Should the plugin check for plugin updates on startup?")
        public boolean enabled = true;

        @Comment("Should the plugin check for plugin updates on startup?")
        public boolean console = true;
    }

    @Override
    public int configVersion() {
        return 2;
    }

    @Override
    public Function<Class<? extends VersionedConfig>, ConfigurationTransformation.Versioned> migrator() {
        return VersionedConfig.super.migrator();
    }
}