package io.github.milkdrinkers.milkonomics.command;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.CommandAPIPaper;
import dev.jorel.commandapi.arguments.IntegerArgument;
import dev.jorel.commandapi.exceptions.WrapperCommandSyntaxException;
import dev.jorel.commandapi.executors.CommandArguments;
import io.github.milkdrinkers.colorparser.paper.ColorParser;
import io.github.milkdrinkers.milkonomics.AbstractMilkonomics;
import io.github.milkdrinkers.milkonomics.api.account.Account;
import io.github.milkdrinkers.milkonomics.utility.Cfg;
import io.github.milkdrinkers.threadutil.Scheduler;
import io.github.milkdrinkers.wordweaver.Translation;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;

import java.util.List;

final class BaltopCommand extends Command {
    private final AbstractMilkonomics plugin;

    public BaltopCommand(AbstractMilkonomics plugin) {
        this.plugin = plugin;
    }

    public CommandAPICommand command() {
        return new CommandAPICommand("balancetop")
            .withHelp("Check the top balances.", "Check the top balances.") // TODO translation
            .withPermission("milkonomics.command.baltop")
            .withOptionalArguments(new IntegerArgument("page"))
            .executes(this::executor);
    }

    private void executor(CommandSender sender, CommandArguments args) throws WrapperCommandSyntaxException {
        final List<Account> accounts = plugin.getAccountManager().getAccounts().stream().toList();
        if (accounts.isEmpty()) {
            throw CommandAPIPaper.failWithAdventureComponent(ColorParser.of(Translation.as("commands.baltop.no-balances")).build());
        }

        Scheduler.async(() -> {
            final int pageLength = 10; // TODO config
            int page = args.getByClassOrDefault("page", Integer.class, 1);
            final int totalBalances = accounts.size();
            final int totalPages = calculatePages(totalBalances, pageLength);

            if (page <= 0)
                page = 1;
            else if (page > totalPages)
                page = totalPages;

            final List<Account> sortedAccounts = accounts.stream()
                .sorted((a, b) -> b.get().compareTo(a.get()))
                .toList();

            Component message = ColorParser.of(Translation.as("commands.baltop.header"))
                .with("page", String.valueOf(page)).build();

            for (int i = 0; i < pageLength; i++) {
                int index = (page - 1) * pageLength + i;
                if (index >= sortedAccounts.size())
                    break;
                message = message.appendNewline().append(
                    ColorParser.of(Translation.as("commands.baltop.format"))
                        .with("rank", String.valueOf(index + 1))
                        .with("account", accounts.get(index).getName())
                        .with("balance", String.valueOf(sortedAccounts.get(index).get()))
                        .with("prefix", Cfg.getDefaultDenominationCfg().prefix)
                        .with("suffix", Cfg.getDefaultDenominationCfg().suffix)
                        .build()
                );
            }

            sender.sendMessage(message);
        }).execute();
    }

    private int calculatePages(int totalBalances, int pageLength) {
        return (int) Math.ceil((double) totalBalances / pageLength);
    }
}
