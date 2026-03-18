package io.github.milkdrinkers.milkonomicsplugin.utility;


import io.github.milkdrinkers.milkonomicsplugin.AbstractMilkonomicsPlugin;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import org.jetbrains.annotations.NotNull;

/**
 * A class that provides shorthand access to {@link AbstractMilkonomicsPlugin#getComponentLogger}.
 */
public class Logger {
    /**
     * Get component logger. Shorthand for:
     *
     * @return the component logger {@link AbstractMilkonomicsPlugin#getComponentLogger}.
     */
    @NotNull
    public static ComponentLogger get() {
        return AbstractMilkonomicsPlugin.getInstance().getComponentLogger();
    }
}
