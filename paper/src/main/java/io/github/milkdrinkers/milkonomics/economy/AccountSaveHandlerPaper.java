package io.github.milkdrinkers.milkonomics.economy;

import io.github.milkdrinkers.milkonomics.AbstractMilkonomics;
import io.papermc.paper.threadedregions.scheduler.ScheduledTask;

import java.util.concurrent.TimeUnit;

public final class AccountSaveHandlerPaper extends AccountSaveHandlerImpl {
    private ScheduledTask task;

    @Override
    public void onEnable(AbstractMilkonomics plugin) {
        super.onEnable(plugin);
        task = plugin.getServer().getAsyncScheduler()
            .runAtFixedRate(plugin, task -> flush(), 0L, 1, TimeUnit.SECONDS);
    }

    @Override
    public void onDisable(AbstractMilkonomics plugin) {
        if (task != null && !task.isCancelled()) {
            task.cancel();
            task = null;
        }
        super.onDisable(plugin);
    }
}
