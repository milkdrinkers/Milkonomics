package io.github.milkdrinkers.milkonomicsplugin.utility;


import io.github.milkdrinkers.milkonomicsplugin.AbstractMilkonomics;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import org.jetbrains.annotations.NotNull;

/**
 * A class that provides shorthand access to {@link AbstractMilkonomics#getComponentLogger}.
 */
public class Logger {
    /**
     * Get component logger. Shorthand for:
     *
     * @return the component logger {@link AbstractMilkonomics#getComponentLogger}.
     */
    @NotNull
    public static ComponentLogger get() {
        return AbstractMilkonomics.getInstance().getComponentLogger();
    }
}
