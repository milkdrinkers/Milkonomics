package io.github.milkdrinkers.milkonomics.command;

import com.destroystokyo.paper.profile.PlayerProfile;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.AsyncPlayerProfileArgument;
import dev.jorel.commandapi.exceptions.WrapperCommandSyntaxException;
import dev.jorel.commandapi.executors.CommandArguments;
import io.github.milkdrinkers.colorparser.paper.ColorParser;
import io.github.milkdrinkers.milkonomics.AbstractMilkonomics;
import io.github.milkdrinkers.milkonomics.api.MilkonomicsAPI;
import io.github.milkdrinkers.milkonomics.api.account.Account;
import io.github.milkdrinkers.milkonomics.api.denomination.Denomination;
import io.github.milkdrinkers.threadutil.Scheduler;
import io.github.milkdrinkers.wordweaver.Translation;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.CompletableFuture;

final class BalanceCommand extends Command {
    private final AbstractMilkonomics plugin;

    public BalanceCommand(AbstractMilkonomics plugin) {
        this.plugin = plugin;
    }

    public CommandAPICommand command() {
        return new CommandAPICommand("balance")
            .withHelp("Check your own, or someone else's balance.", "Check your own, or someone else's balance.") // TODO translation
            .withPermission("milkonomics.command.balance")
            .withOptionalArguments(new AsyncPlayerProfileArgument("player"))
            .executesPlayer(this::executorPlayer)
            .executes(this::executor);
    }

    private void executor(CommandSender sender, CommandArguments args) throws WrapperCommandSyntaxException {
        // noinspection unchecked
        final CompletableFuture<List<PlayerProfile>> profiles = (CompletableFuture<List<PlayerProfile>>) args.getByClassOrDefault("player", CompletableFuture.class, null);
        if (profiles == null)
            throw Result.fail(ColorParser.of(Translation.of("commands.balance.no-player-specified")).build());

        Scheduler
            .async(profiles)
            .sync(profileList -> {
                try {
                    if (profileList.isEmpty() || profileList.getFirst() == null)
                        throw Result.fail(ColorParser.of(Translation.of("commands.balance.no-player-specified")).build());

                    final PlayerProfile targetProfile = profileList.getFirst();
                    if (targetProfile.getId() == null || targetProfile.getName() == null)
                        throw Result.fail(ColorParser.of(Translation.of("commands.balance.no-player-specified")).build());

                    final Account account = MilkonomicsAPI.getInstance().getAccountManager().getAccount(targetProfile.getId()).orElseThrow(
                        () -> Result.fail(ColorParser.of(Translation.of("commands.balance.account-not-found")).build())
                    );

                    final Denomination defaultDenomination = MilkonomicsAPI.getInstance().getDenominationManager().getDefaultDenomination();

                    sender.sendMessage(ColorParser.of(Translation.of("commands.balance.balance"))
                        .with("player", targetProfile.getName())
                        .with("amount", String.valueOf(account.get()))
                        .with("amount_formatted", String.valueOf(defaultDenomination.format(account.get())))
                        .with("prefix", defaultDenomination.prefix())
                        .with("suffix", defaultDenomination.suffix())
                        .with("symbol", defaultDenomination.symbol())
                        .with("currency_name", defaultDenomination.displayName())
                        .with("currency_name_plural", defaultDenomination.displayNamePlural())
                        .build());
                } catch (Result.CommandException e) {
                    sender.sendMessage(e.getClientMessage());
                }
            })
            .execute();
    }

    private void executorPlayer(Player sender, CommandArguments args) throws WrapperCommandSyntaxException {
        if (args.get("player") == null) {
            final PlayerProfile targetProfile = sender.getPlayerProfile();
            if (targetProfile.getId() == null)
                throw Result.fail(ColorParser.of(Translation.of("commands.balance.no-player-specified")).build());

            final Account account = MilkonomicsAPI.getInstance().getAccountManager().getAccount(targetProfile.getId()).orElseThrow(
                () -> Result.fail(ColorParser.of(Translation.of("commands.balance.account-not-found")).build())
            );

            final Denomination defaultDenomination = MilkonomicsAPI.getInstance().getDenominationManager().getDefaultDenomination();

            sender.sendMessage(ColorParser.of(Translation.of("commands.balance.balance-self"))
                .with("player", sender.displayName())
                .with("amount", String.valueOf(account.get()))
                .with("amount_formatted", String.valueOf(defaultDenomination.format(account.get())))
                .with("prefix", defaultDenomination.prefix())
                .with("suffix", defaultDenomination.suffix())
                .with("symbol", defaultDenomination.symbol())
                .with("currency_name", defaultDenomination.displayName())
                .with("currency_name_plural", defaultDenomination.displayNamePlural())
                .build());
        } else {
            executor(sender, args);
        }
    }
}
