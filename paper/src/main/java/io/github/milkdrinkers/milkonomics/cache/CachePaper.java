package io.github.milkdrinkers.milkonomics.cache;

import io.github.milkdrinkers.milkonomics.AbstractMilkonomics;
import io.papermc.paper.threadedregions.scheduler.ScheduledTask;

import java.util.concurrent.TimeUnit;

public class CachePaper extends CacheImpl {
    public CachePaper(AbstractMilkonomics plugin) {
        super(plugin);
    }
}
