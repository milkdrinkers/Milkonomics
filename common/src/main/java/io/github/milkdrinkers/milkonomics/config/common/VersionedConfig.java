package io.github.milkdrinkers.milkonomics.config.common;

import org.spongepowered.configurate.interfaces.meta.Exclude;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;
import org.spongepowered.configurate.transformation.ConfigurationTransformation;

import java.util.Map;
import java.util.function.Function;

@ConfigSerializable
public interface VersionedConfig {
    @Comment("Do not change this value!")
    @SuppressWarnings("unused")
    int configVersion();

    @Exclude
    default Map<Integer, ConfigurationTransformation> migrations() {
        return Map.of();
    };

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
     * The base interface for config migrations.
     * @author GeyserMC
     * @link <a href="https://github.com/GeyserMC/Geyser">Github</a>
     */
    @Exclude
    default Function<Class<? extends VersionedConfig>, ConfigurationTransformation.Versioned> migrator() {
        return (configClass) -> {
            final ConfigurationTransformation.VersionedBuilder builder = ConfigurationTransformation.versionedBuilder()
                .versionKey("config-version");

            for (Map.Entry<Integer, ConfigurationTransformation> entry : migrations().entrySet()) {
                builder.addVersion(entry.getKey(), entry.getValue());
            }

            return builder.build();
        };
    }
}
