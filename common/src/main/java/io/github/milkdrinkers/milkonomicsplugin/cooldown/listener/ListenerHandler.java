package io.github.milkdrinkers.milkonomicsplugin.cooldown.listener;

import io.github.milkdrinkers.milkonomicsplugin.AbstractMilkonomics;
import io.github.milkdrinkers.milkonomicsplugin.Reloadable;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.List;

/**
 * A class to handle registration of event listeners.
 */
@SuppressWarnings("FieldCanBeLocal")
public class ListenerHandler implements Reloadable {
    private final AbstractMilkonomics plugin;
    private final List<Listener> listeners = new ArrayList<>();

    public ListenerHandler(AbstractMilkonomics plugin) {
        this.plugin = plugin;
    }

    @Override
    public void onLoad(AbstractMilkonomics plugin) {
    }

    @Override
    public void onEnable(AbstractMilkonomics plugin) {
        listeners.clear();
        listeners.add(new CooldownListener(plugin));

        for (Listener listener : listeners) {
            plugin.getServer().getPluginManager().registerEvents(listener, plugin);
        }
    }

    @Override
    public void onDisable(AbstractMilkonomics plugin) {
    }
}
