package io.github.milkdrinkers.milkonomics.cache;

import io.github.milkdrinkers.milkonomics.AbstractMilkonomics;
import io.github.milkdrinkers.milkonomics.Reloadable;
import io.github.milkdrinkers.milkonomics.api.cache.Cache;
public abstract class CacheImpl implements Reloadable, Cache {
    private final AbstractMilkonomics plugin;

    CacheImpl(AbstractMilkonomics plugin) {
        this.plugin = plugin;
    }

}
