package io.github.milkdrinkers.milkonomics;

/**
 * Implemented in classes that should support being reloaded IE executing the methods during runtime after startup.
 */
public interface Reloadable {
    /**
     * On plugin load.
     */
    default void onLoad(AbstractMilkonomics plugin) {
    }

    /**
     * On plugin enable.
     */
    default void onEnable(AbstractMilkonomics plugin) {
    }

    /**
     * On plugin disable.
     */
    default void onDisable(AbstractMilkonomics plugin) {
    }

}
