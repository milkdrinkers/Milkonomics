package io.github.milkdrinkers.milkonomicsplugin.utility;


import io.github.milkdrinkers.milkonomicsplugin.Milkonomics;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import org.jetbrains.annotations.NotNull;

/**
 * A class that provides shorthand access to {@link Milkonomics#getComponentLogger}.
 */
public class Logger {
    /**
     * Get component logger. Shorthand for:
     *
     * @return the component logger {@link Milkonomics#getComponentLogger}.
     */
    @NotNull
    public static ComponentLogger get() {
        return Milkonomics.getInstance().getComponentLogger();
    }
}
