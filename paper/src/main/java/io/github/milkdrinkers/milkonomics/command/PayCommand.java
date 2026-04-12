package io.github.milkdrinkers.milkonomics.command;

import com.destroystokyo.paper.profile.PlayerProfile;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.CommandAPIPaper;
import dev.jorel.commandapi.arguments.AsyncPlayerProfileArgument;
import dev.jorel.commandapi.arguments.DoubleArgument;
import dev.jorel.commandapi.exceptions.WrapperCommandSyntaxException;
import dev.jorel.commandapi.executors.CommandArguments;
import io.github.milkdrinkers.colorparser.paper.ColorParser;
import io.github.milkdrinkers.milkonomics.AbstractMilkonomics;
import io.github.milkdrinkers.milkonomics.api.MilkonomicsAPI;
import io.github.milkdrinkers.milkonomics.api.account.Account;
import io.github.milkdrinkers.milkonomics.api.denomination.Denomination;
import io.github.milkdrinkers.threadutil.Scheduler;
import io.github.milkdrinkers.wordweaver.Translation;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.concurrent.CompletableFuture;

final class PayCommand extends Command {
    private final AbstractMilkonomics plugin;

    public PayCommand(AbstractMilkonomics plugin) {
        this.plugin = plugin;
    }

    public CommandAPICommand command() {
        return new CommandAPICommand("pay")
            .withArguments(
                new AsyncPlayerProfileArgument("player"),
                new DoubleArgument("amount"))
            .withPermission("milkonomics.command.pay")
            .executesPlayer(this::executor);
    }

    private void executor(Player sender, CommandArguments args) throws WrapperCommandSyntaxException {
        final double amount = args.getByClassOrDefault("amount", Double.class, 0.0);
        if (amount <= 0.0)
            throw CommandAPIPaper.failWithAdventureComponent(ColorParser.of(Translation.of("commands.pay.invalid-amount")).build());

        // noinspection unchecked
        final CompletableFuture<List<PlayerProfile>> profiles = (CompletableFuture<List<PlayerProfile>>) args.getByClassOrDefault("player", CompletableFuture.class, null);
        if (profiles == null)
            throw CommandAPIPaper.failWithAdventureComponent(ColorParser.of(Translation.of("commands.pay.player-not-found")).build());

        Scheduler
            .async(profiles)
            .sync(profileList -> {
                try {
                    if (profileList.isEmpty() || profileList.getFirst() == null)
                        throw Result.fail(ColorParser.of(Translation.of("commands.pay.player-not-found")).build()); // TODO Resolve these messages being stripped as exceptions

                    final PlayerProfile targetProfile = profileList.getFirst();
                    if (targetProfile.getId() == null)
                        throw Result.fail(ColorParser.of(Translation.of("commands.pay.player-not-found")).build());

                    final Player target = plugin.getServer().getPlayer(targetProfile.getId());
                    if (target == null)
                        throw Result.fail(ColorParser.of(Translation.of("commands.pay.player-not-found")).build());

                    if (target == sender)
                        throw Result.fail(ColorParser.of(Translation.of("commands.pay.self-payment")).build());

                    final Account originAccount = MilkonomicsAPI.getInstance().getAccountManager().getAccount(sender.getUniqueId()).orElseThrow(
                        () -> Result.fail(ColorParser.of(Translation.of("commands.pay.origin-account-not-found")).build())
                    );

                    final Account targetAccount = MilkonomicsAPI.getInstance().getAccountManager().getAccount(targetProfile.getId()).orElseThrow(
                        () -> Result.fail(ColorParser.of(Translation.of("commands.pay.target-account-not-found")).build())
                    );

                    if (!targetAccount.isAcceptingTransactions())
                        throw Result.fail(ColorParser.of(Translation.of("commands.pay.target-not-accepting-payments")).with("player", target.displayName()).build());

                    if (!originAccount.has(amount))
                        throw Result.fail(ColorParser.of(Translation.of("commands.pay.insufficient-funds")).build());

                    originAccount.withdraw(amount);
                    targetAccount.deposit(amount);

                    final Denomination defaultDenomination = MilkonomicsAPI.getInstance().getDenominationManager().getDefaultDenomination();

                    sender.sendMessage(ColorParser.of(Translation.of("commands.pay.origin-success"))
                        .with("player", target.displayName())
                        .with("amount", String.valueOf(amount))
                        .with("prefix", defaultDenomination.prefix())
                        .with("suffix", defaultDenomination.suffix())
                        .with("symbol", defaultDenomination.symbol())
                        .with("currency_name", defaultDenomination.displayName())
                        .with("currency_name_plural", defaultDenomination.displayNamePlural())
                        .build());

                    target.sendMessage(ColorParser.of(Translation.of("commands.pay.target-success"))
                        .with("player", sender.displayName())
                        .with("amount", String.valueOf(amount))
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
}
