package io.github.milkdrinkers.milkonomics.cache;

import io.github.milkdrinkers.milkonomics.AbstractMilkonomics;
import io.papermc.paper.threadedregions.scheduler.ScheduledTask;

import java.util.concurrent.TimeUnit;

public class CachePaper extends CacheImpl {
    private ScheduledTask task;

    public CachePaper(AbstractMilkonomics plugin) {
        super(plugin);
    }

    @Override
    public void onEnable(AbstractMilkonomics plugin) {
        super.onEnable(plugin);
        task = plugin.getServer().getAsyncScheduler().runAtFixedRate(
            plugin,
            task -> updateBalanceTop(),
            0L,
            plugin.getConfigHandler().getConfig().balanceTop.cacheDuration.toMillis(),
            TimeUnit.MILLISECONDS
        );
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
