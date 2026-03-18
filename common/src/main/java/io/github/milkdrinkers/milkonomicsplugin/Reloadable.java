package io.github.milkdrinkers.milkonomicsplugin;

/**
 * Implemented in classes that should support being reloaded IE executing the methods during runtime after startup.
 */
public interface Reloadable {
    /**
     * On plugin load.
     */
    default void onLoad(AbstractMilkonomicsPlugin plugin) {};

    /**
     * On plugin enable.
     */
    default void onEnable(AbstractMilkonomicsPlugin plugin) {}

    /**
     * On plugin disable.
     */
    default void onDisable(AbstractMilkonomicsPlugin plugin) {};
}
