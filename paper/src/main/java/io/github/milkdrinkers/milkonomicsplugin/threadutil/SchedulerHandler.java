package io.github.milkdrinkers.milkonomicsplugin.threadutil;

import io.github.milkdrinkers.milkonomicsplugin.AbstractMilkonomics;
import io.github.milkdrinkers.milkonomicsplugin.Reloadable;
import io.github.milkdrinkers.threadutil.PlatformBukkit;
import io.github.milkdrinkers.threadutil.Scheduler;

import java.time.Duration;

/**
 * A wrapper handler class for handling thread-util lifecycle.
 */
public class SchedulerHandler implements Reloadable {
    @Override
    public void onLoad(AbstractMilkonomics plugin) {
        Scheduler.init(new PlatformBukkit(plugin)); // Initialize thread-util
        Scheduler.setErrorHandler(e -> plugin.getSLF4JLogger().error("[Scheduler]: {}", e.getMessage()));
    }

    @Override
    public void onEnable(AbstractMilkonomics plugin) {

    }

    @Override
    public void onDisable(AbstractMilkonomics plugin) {
        if (Scheduler.isInitialized())
            Scheduler.shutdown(Duration.ofSeconds(60));
    }
}
