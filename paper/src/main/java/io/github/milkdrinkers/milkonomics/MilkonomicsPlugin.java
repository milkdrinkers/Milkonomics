package io.github.milkdrinkers.milkonomics;

import io.github.milkdrinkers.milkonomics.api.MilkonomicsAPI;
import io.github.milkdrinkers.milkonomics.command.CommandHandler;
import io.github.milkdrinkers.milkonomics.config.ConfigHandler;
import io.github.milkdrinkers.milkonomics.cooldown.CooldownHandler;
import io.github.milkdrinkers.milkonomics.database.handler.DatabaseHandler;
import io.github.milkdrinkers.milkonomics.economy.AccountManagerImpl;
import io.github.milkdrinkers.milkonomics.api.AccountSaveHandler;
import io.github.milkdrinkers.milkonomics.economy.AccountSaveHandlerImpl;
import io.github.milkdrinkers.milkonomics.economy.EconomyImpl;
import io.github.milkdrinkers.milkonomics.economy.denomination.DenominationHandler;
import io.github.milkdrinkers.milkonomics.hook.HookManager;
import io.github.milkdrinkers.milkonomics.listener.ListenerHandler;
import io.github.milkdrinkers.milkonomics.messaging.MessagingHandler;
import io.github.milkdrinkers.milkonomics.threadutil.SchedulerHandler;
import io.github.milkdrinkers.milkonomics.translation.TranslationHandler;
import io.github.milkdrinkers.milkonomics.updatechecker.UpdateHandler;
import io.github.milkdrinkers.milkonomics.utility.DB;
import io.github.milkdrinkers.milkonomics.utility.Logger;
import io.github.milkdrinkers.milkonomics.utility.Messaging;
import io.github.milkdrinkers.colorparser.paper.ColorParser;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

import java.util.List;

/**
 * Main class.
 */
@SuppressWarnings({"FieldCanBeLocal", "unused"})
public class MilkonomicsPlugin extends AbstractMilkonomics {
    private static MilkonomicsPlugin instance;

    // Handlers/Managers
    private ConfigHandler configHandler;
    private TranslationHandler translationHandler;
    private DatabaseHandler databaseHandler;
    private MessagingHandler messagingHandler;
    private HookManager hookManager;
    private CommandHandler commandHandler;
    private ListenerHandler listenerHandler;
    private UpdateHandler updateHandler;
    private SchedulerHandler schedulerHandler;
    private CooldownHandler cooldownHandler;
    private DenominationHandler denominationHandler;
    private AccountSaveHandlerImpl accountSaveHandler;
    private AccountManagerImpl accountManager;
    private MilkonomicsAPIProvider apiHandler;
    private EconomyImpl economyProvider;

    // Handlers list (defines order of load/enable/disable)
    private List<? extends Reloadable> handlers;

    @Override
    public void onLoad() {
        instance = this;

        configHandler = new ConfigHandler(this);
        translationHandler = new TranslationHandler(configHandler);
        databaseHandler = DatabaseHandler.builder()
            .withConfigHandler(configHandler)
            .withLogger(getComponentLogger())
            .withMigrate(true)
            .build();
        messagingHandler = MessagingHandler.builder()
            .withLogger(getComponentLogger())
            .withName(getName())
            .build();
        hookManager = new HookManager(this);
        commandHandler = new CommandHandler(this);
        listenerHandler = new ListenerHandler(this);
        updateHandler = new UpdateHandler(this);
        schedulerHandler = new SchedulerHandler();
        cooldownHandler = new CooldownHandler();
        denominationHandler = new DenominationHandler();
        accountSaveHandler = new AccountSaveHandlerImpl();
        accountManager = new AccountManagerImpl(accountSaveHandler);
        apiHandler = new MilkonomicsAPIProvider(this);
        economyProvider = new EconomyImpl(this, accountManager);

        handlers = List.of(
            configHandler,
            translationHandler,
            databaseHandler,
            messagingHandler,
            hookManager,
            commandHandler,
            listenerHandler,
            updateHandler,
            schedulerHandler,
            cooldownHandler,
            cooldownHandler,
            accountSaveHandler,
            accountManager,
            apiHandler,
            economyProvider
        );

        DB.init(databaseHandler);
        Messaging.init(messagingHandler);
        for (Reloadable handler : handlers)
            handler.onLoad(instance);
    }

    @Override
    public void onEnable() {
        for (Reloadable handler : handlers)
            handler.onEnable(instance);

        if (!DB.isStarted()) {
            Logger.get().warn(ColorParser.of("<yellow>Database handler failed to start. Database support has been disabled.").build());
            Bukkit.getPluginManager().disablePlugin(this);
        }

        if (!Messaging.isReady() && configHandler.getDatabaseConfig().messaging.enabled) {
            Logger.get().warn(ColorParser.of("<yellow>Messaging handler failed to start. Messaging support has been disabled.").build());
            Bukkit.getPluginManager().disablePlugin(this);
        }
    }

    @Override
    public void onDisable() {
        for (Reloadable handler : handlers.reversed()) // If reverse doesn't work implement a new List with your desired disable order
            handler.onDisable(instance);
    }

    /**
     * Use to reload the entire plugin.
     */
    public void onReload() {
        onDisable();
        onLoad();
        onEnable();
    }

    @Override
    public @NotNull ConfigHandler getConfigHandler() {
        return configHandler;
    }

    public @NotNull HookManager getHookManager() {
        return hookManager;
    }

    public @NotNull UpdateHandler getUpdateHandler() {
        return updateHandler;
    }

    @Override
    public @NotNull AccountManagerImpl getAccountManager() {
        return accountManager;
    }

    @Override
    public @NotNull AccountSaveHandler getAccountSaveHandler() {
        return accountSaveHandler;
    }

    @Override
    public @NonNull DenominationHandler getDenominationHandler() {
        return denominationHandler;
    }

    @Override
    public @NotNull Economy getEconomyProvider() {
        return economyProvider;
    }

    public @NotNull MilkonomicsAPI getApiHandler() {
        return apiHandler;
    }
}
