package io.github.milkdrinkers.milkonomicsplugin.utility;


import io.github.milkdrinkers.milkonomicsplugin.MilkonomicsPlugin;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import org.jetbrains.annotations.NotNull;

/**
 * A class that provides shorthand access to {@link MilkonomicsPlugin#getComponentLogger}.
 */
public class Logger {
    /**
     * Get component logger. Shorthand for:
     *
     * @return the component logger {@link MilkonomicsPlugin#getComponentLogger}.
     */
    @NotNull
    public static ComponentLogger get() {
        return MilkonomicsPlugin.getInstance().getComponentLogger();
    }
}
