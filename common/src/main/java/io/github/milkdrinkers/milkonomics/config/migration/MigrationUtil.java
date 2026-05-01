package io.github.milkdrinkers.milkonomics.config.migration;

import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.transformation.TransformAction;

import java.util.Arrays;

/**
 * Utility helpers for building {@link TransformAction}s used inside
 * {@link io.github.milkdrinkers.milkonomics.config.VersionedConfig#migrations()}.
 *
 * <p>Portions derived from
 * <a href="https://github.com/GeyserMC/Geyser">GeyserMC/Geyser</a>
 * © 2024 GeyserMC, licensed under the MIT License.
 */
public final class MigrationUtil {
    private MigrationUtil() {}

    /**
     * Rename a key to an entirely new path (discards the original key name).
     *
     * <p>Example — move {@code old-key} to {@code section.new-key}:
     * <pre>{@code
     * MigrationUtil.renameAndMove("section", "new-key")
     * }</pre>
     *
     * @param newPath full destination path segments
     * @return a {@link TransformAction} that replaces the path
     */
    public static TransformAction renameAndMove(String... newPath) {
        return (path, value) -> Arrays.stream(newPath).toArray();
    }

    /**
     * Move a key into a new parent section while keeping its original key name as the last segment.
     *
     * <p>Example — move {@code old-key} into {@code section}, producing {@code section.old-key}:
     * <pre>{@code
     * MigrationUtil.moveTo("section")
     * }</pre>
     *
     * @param newPath parent path segments to prepend
     * @return a {@link TransformAction} that moves the key under the new parent
     */
    public static TransformAction moveTo(String... newPath) {
        return (path, value) -> {
            final Object[] arr = path.array();
            if (arr.length == 0) {
                throw new ConfigurateException(value, "The root node cannot be moved!");
            }

            final Object[] result = new Object[newPath.length + 1];
            System.arraycopy(newPath, 0, result, 0, newPath.length);
            result[newPath.length] = arr[arr.length - 1];
            return result;
        };
    }
}
