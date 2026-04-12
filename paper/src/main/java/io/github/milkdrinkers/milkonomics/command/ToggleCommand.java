package io.github.milkdrinkers.milkonomics.command;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.CommandAPIPaper;
import dev.jorel.commandapi.exceptions.WrapperCommandSyntaxException;
import dev.jorel.commandapi.executors.CommandArguments;
import io.github.milkdrinkers.colorparser.paper.ColorParser;
import io.github.milkdrinkers.milkonomics.api.MilkonomicsAPI;
import io.github.milkdrinkers.milkonomics.api.account.Account;
import io.github.milkdrinkers.wordweaver.Translation;
import org.bukkit.entity.Player;

final class ToggleCommand extends Command {
    public ToggleCommand() {
    }

    public CommandAPICommand command() {
        return new CommandAPICommand("togglepayments")
            .withPermission("milkonomics.command.togglepayments")
            .executesPlayer(this::executor);
    }

    public void executor(Player sender, CommandArguments args) throws WrapperCommandSyntaxException {
        final Account account = MilkonomicsAPI.getInstance().getAccountManager().getAccount(sender.getUniqueId()).orElseThrow(
            () -> CommandAPIPaper.failWithAdventureComponent(ColorParser.of(Translation.as("commands.toggle-payments.no-data")).build())
        );

        if (account.isAcceptingTransactions()) {
            sender.sendMessage(ColorParser.of(Translation.as("commands.toggle-payments.accepting")).build());
        } else {
            sender.sendMessage(ColorParser.of(Translation.as("commands.toggle-payments.declining")).build());
        }
    }
}
