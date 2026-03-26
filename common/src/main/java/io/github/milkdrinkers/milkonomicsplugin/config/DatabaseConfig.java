package io.github.milkdrinkers.milkonomicsplugin.config;

import io.github.milkdrinkers.milkonomicsplugin.config.common.VersionedConfig;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;

@ConfigSerializable
public class DatabaseConfig implements VersionedConfig {
    @Comment("Should the plugin check for plugin updates on startup?")
    public boolean updateChecker = true;


//    @Comment("Do not change this value!")
//    public int configVersion = 1;
}