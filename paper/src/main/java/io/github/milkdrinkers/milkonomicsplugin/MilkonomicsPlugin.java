package io.github.milkdrinkers.milkonomicsplugin;

import io.github.milkdrinkers.milkonomicsplugin.api.MilkonomicsAPI;
import io.github.milkdrinkers.milkonomicsplugin.command.CommandHandler;
import io.github.milkdrinkers.milkonomicsplugin.config.ConfigHandler;
import io.github.milkdrinkers.milkonomicsplugin.cooldown.CooldownHandler;
import io.github.milkdrinkers.milkonomicsplugin.database.handler.DatabaseHandler;
import io.github.milkdrinkers.milkonomicsplugin.economy.AccountManagerImpl;
import io.github.milkdrinkers.milkonomicsplugin.api.AccountSaveHandler;
import io.github.milkdrinkers.milkonomicsplugin.economy.AccountSaveHandlerImpl;
import io.github.milkdrinkers.milkonomicsplugin.economy.EconomyImpl;
import io.github.milkdrinkers.milkonomicsplugin.economy.denomination.DenominationHandler;
import io.github.milkdrinkers.milkonomicsplugin.hook.HookManager;
import io.github.milkdrinkers.milkonomicsplugin.listener.ListenerHandler;
import io.github.milkdrinkers.milkonomicsplugin.messaging.MessagingHandler;
import io.github.milkdrinkers.milkonomicsplugin.threadutil.SchedulerHandler;
import io.github.milkdrinkers.milkonomicsplugin.translation.TranslationHandler;
import io.github.milkdrinkers.milkonomicsplugin.updatechecker.UpdateHandler;
import io.github.milkdrinkers.milkonomicsplugin.utility.DB;
import io.github.milkdrinkers.milkonomicsplugin.utility.Logger;
import io.github.milkdrinkers.milkonomicsplugin.utility.Messaging;
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
public class MilkonomicsPlugin extends AbstractMilkonomicsPlugin {
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
