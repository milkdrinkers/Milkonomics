package io.github.milkdrinkers.milkonomicsplugin;

/**
 * Implemented in classes that should support being reloaded IE executing the methods during runtime after startup.
 */
public interface Reloadable {
    /**
     * On plugin load.
     */
    default void onLoad(Milkonomics plugin) {};

    /**
     * On plugin enable.
     */
    default void onEnable(Milkonomics plugin) {}

    /**
     * On plugin disable.
     */
    default void onDisable(Milkonomics plugin) {};
}
