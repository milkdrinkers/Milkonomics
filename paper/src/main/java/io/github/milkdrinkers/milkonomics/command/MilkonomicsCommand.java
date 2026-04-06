package io.github.milkdrinkers.milkonomics.command;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.executors.CommandArguments;
import io.github.milkdrinkers.colorparser.paper.ColorParser;
import io.github.milkdrinkers.milkonomicsplugin.AbstractMilkonomicsPlugin;
import org.bukkit.command.CommandSender;

import static io.github.milkdrinkers.milkonomics.command.CommandHandler.BASE_PERM;

/**
 * Class containing the code for the example command.
 */
class MilkonomicsCommand {
    /**
     * Instantiates and registers a new command.
     */
    protected MilkonomicsCommand(AbstractMilkonomicsPlugin plugin) {
        new CommandAPICommand("milkonomics")
            .withHelp("Base command.", "Base command.")
            .withPermission(BASE_PERM)
            .withSubcommands(
                new TranslationCommand().command(),
                new DumpCommand().command(),
                new AdminCommand(plugin).command(),
                new BalanceCommand(plugin).command(),
                new PayCommand(plugin).command()
            )
            .executes(this::executorExample)
            .register();
    }

    private void executorExample(CommandSender sender, CommandArguments args) {
        sender.sendMessage(
            ColorParser.of("<white>Read more about CommandAPI &9<click:open_url:'https://commandapi.jorel.dev/9.0.3/'>here</click><white>.")
                .legacy() // Parse legacy color codes
                .build()
        );
    }
}
