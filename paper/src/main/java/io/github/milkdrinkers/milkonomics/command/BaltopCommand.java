package io.github.milkdrinkers.milkonomics.command;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.IntegerArgument;
import dev.jorel.commandapi.exceptions.WrapperCommandSyntaxException;
import dev.jorel.commandapi.executors.CommandArguments;
import io.github.milkdrinkers.colorparser.paper.ColorParser;
import io.github.milkdrinkers.milkonomics.AbstractMilkonomics;
import io.github.milkdrinkers.milkonomics.api.MilkonomicsAPI;
import io.github.milkdrinkers.milkonomics.api.account.Account;
import io.github.milkdrinkers.milkonomics.api.denomination.Denomination;
import io.github.milkdrinkers.milkonomics.database.Queries;
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
        final int requestedPage = Math.max(1, args.getByClassOrDefault("page", Integer.class, 1));

        Scheduler
            .async(() -> Queries.Baltop.get(
                plugin.getConfigHandler().getConfig().balanceTop.entriesPerPage,
                MilkonomicsAPI.getInstance().getDenominationManager().getDefaultDenomination().id(),
                requestedPage
            ))
            .sync(accounts -> {
                try {
                    if (accounts.isEmpty() && requestedPage == 1)
                        throw Result.fail(ColorParser.of(Translation.of("commands.baltop.no-balances")).build());

                    if (accounts.isEmpty())
                        throw Result.fail(ColorParser.of(Translation.of("commands.baltop.page-not-exist")).build());

                    sender.sendMessage(render(requestedPage, accounts));
                } catch (Result.CommandException e) {
                    sender.sendMessage(e.getClientMessage());
                }
            })
            .execute();
    }

    private Component render(int page, List<Account> accounts) {
        final Denomination d = MilkonomicsAPI.getInstance().getDenominationManager().getDefaultDenomination();
        final int prevPage = Math.max(1, page - 1);
        final int nextPage = page + 1;

        Component message = ColorParser.of(Translation.of("commands.baltop.header"))
            .with("page", String.valueOf(page))
            .build();

        for (int i = 0; i < accounts.size(); i++) {
            final Account account = accounts.get(i);
            final int rank = (page - 1) * plugin.getConfigHandler().getConfig().balanceTop.entriesPerPage + i + 1;

            message = message.appendNewline().append(
                ColorParser.of(Translation.of("commands.baltop.format"))
                    .with("rank", String.valueOf(rank))
                    .with("account", account.getName())
                    .with("balance", String.valueOf(account.get()))
                    .with("balance_formatted", String.valueOf(d.format(account.get())))
                    .with("prefix", d.prefix())
                    .with("suffix", d.suffix())
                    .with("symbol", d.symbol())
                    .with("currency_name", d.displayName())
                    .with("currency_name_plural", d.displayNamePlural())
                    .build()
            );
        }

        final Component footer = ColorParser.of(Translation.of("commands.baltop.footer"))
            .with("next_page", String.valueOf(nextPage))
            .with("page", String.valueOf(page))
            .with("previous_page", String.valueOf(prevPage))
            .with("previous_page_button", ColorParser.of(Translation.of("commands.baltop.previous-page"))
                .with("next_page", String.valueOf(nextPage))
                .with("page", String.valueOf(page))
                .with("previous_page", String.valueOf(prevPage))
                .build()
            )
            .with("next_page_button", ColorParser.of(Translation.of("commands.baltop.next-page"))
                .with("next_page", String.valueOf(nextPage))
                .with("page", String.valueOf(page))
                .with("previous_page", String.valueOf(prevPage))
                .build()
            )
            .build();

        return message.appendNewline().append(footer);
    }
}
