package io.github.milkdrinkers.milkonomics.command;

import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.CommandAPIPaperConfig;
import io.github.milkdrinkers.milkonomics.AbstractMilkonomics;
import io.github.milkdrinkers.milkonomics.Milkonomics;
import io.github.milkdrinkers.milkonomics.Reloadable;

/**
 * A class to handle registration of commands.
 */
public class CommandHandler implements Reloadable {
    public static final String BASE_PERM = "milkonomics.command";
    private final Milkonomics plugin;

    /**
     * Instantiates the Command handler.
     *
     * @param plugin the plugin
     */
    public CommandHandler(Milkonomics plugin) {
        this.plugin = plugin;
    }

    @Override
    public void onLoad(AbstractMilkonomics plugin) {
        CommandAPI.onLoad(
            new CommandAPIPaperConfig(plugin)
                .silentLogs(true)
        );
    }

    @Override
    public void onEnable(AbstractMilkonomics plugin) {
        if (!CommandAPI.isLoaded())
            return;

        CommandAPI.onEnable();

        // Register commands here
        new MilkonomicsCommand(plugin);
    }

    @Override
    public void onDisable(AbstractMilkonomics plugin) {
        if (!CommandAPI.isLoaded())
            return;

        CommandAPI.onDisable();
    }
}