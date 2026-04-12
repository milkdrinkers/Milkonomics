package io.github.milkdrinkers.milkonomics.config;

import io.github.milkdrinkers.milkonomics.config.common.VersionedConfig;
import io.github.milkdrinkers.milkonomics.config.exception.ConfigValidationException;
import org.spongepowered.configurate.interfaces.meta.Exclude;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;
import org.spongepowered.configurate.transformation.ConfigurationTransformation;

import java.util.Map;

@ConfigSerializable
public class PluginConfig implements VersionedConfig {
    @Comment("Do not change this value!")
    public int configVersion = 1;

    @Override
    @Exclude
    public int configVersion() {
        return configVersion;
    }

    @Override
    @Exclude
    public Map<Integer, ConfigurationTransformation> migrations() {
        return Map.of();
    }

//    @Override
//    @Exclude
//    public void validate() throws ConfigValidationException {
//    }

    @Comment("Update Checker Settings")
    public UpdateChecker updateChecker = new UpdateChecker();

    @ConfigSerializable
    public static class UpdateChecker {
        @Comment("Should the plugin check for plugin updates on startup?")
        public boolean enabled = true;

        @Comment("Send update notifications to the console?")
        public boolean console = true;

        @Comment("Send update notifications to opped players on join?")
        public boolean op = true;
    }

    @Comment("Language, specify the language file to use, for example `en_US` which will load `/lang/en_US.json`")
    public String language = "en_US";

    @Comment("Balance Top Settings")
    public Baltop balanceTop = new Baltop();

    @ConfigSerializable
    public static class Baltop {
        @Comment("How many entries should each balance top page show?")
        public int entriesPerPage = 8;
    }
}