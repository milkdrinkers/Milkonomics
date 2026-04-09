package io.github.milkdrinkers.milkonomics.command;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.executors.CommandArguments;
import io.github.milkdrinkers.colorparser.paper.ColorParser;
import io.github.milkdrinkers.milkonomics.AbstractMilkonomics;
import org.bukkit.command.CommandSender;

import static io.github.milkdrinkers.milkonomics.command.CommandHandler.BASE_PERM;

/**
 * Class containing the code for the example command.
 */
final class MilkonomicsCommand {
    /**
     * Instantiates and registers a new command.
     */
    MilkonomicsCommand(AbstractMilkonomics plugin) {
        new CommandAPICommand("milkonomics")
            .withHelp("Base command.", "Base command.")
            .withPermission(BASE_PERM)
            .withSubcommands(
                new TranslationCommand().command(),
                new DumpCommand().command(),
                new AdminCommand(plugin).command(),
                new BalanceCommand(plugin).command(),
                new PayCommand(plugin).command(),
                new BaltopCommand(plugin).command(),
                new ToggleCommand().command()
            )
            .executes(this::executorExample)
            .register();
    }

    private void executorExample(CommandSender sender, CommandArguments args) {
        sender.sendMessage(
            ColorParser.of("<white>Read more about CommandAPI &9<click:open_url:'https://commandapi.jorel.dev/9.0.3/'>here</click><white>.") // TODO Help message translatable
                .legacy() // Parse legacy color codes
                .build()
        );
    }
}
