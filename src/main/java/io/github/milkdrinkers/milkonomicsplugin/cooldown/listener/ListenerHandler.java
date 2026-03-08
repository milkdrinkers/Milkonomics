package io.github.milkdrinkers.milkonomicsplugin.cooldown.listener;

import io.github.milkdrinkers.milkonomicsplugin.MilkonomicsPlugin;
import io.github.milkdrinkers.milkonomicsplugin.Reloadable;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.List;

/**
 * A class to handle registration of event listeners.
 */
@SuppressWarnings("FieldCanBeLocal")
public class ListenerHandler implements Reloadable {
    private final MilkonomicsPlugin plugin;
    private final List<Listener> listeners = new ArrayList<>();

    public ListenerHandler(MilkonomicsPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void onLoad(MilkonomicsPlugin plugin) {
    }

    @Override
    public void onEnable(MilkonomicsPlugin plugin) {
        listeners.clear();
        listeners.add(new CooldownListener(plugin));

        for (Listener listener : listeners) {
            plugin.getServer().getPluginManager().registerEvents(listener, plugin);
        }
    }

    @Override
    public void onDisable(MilkonomicsPlugin plugin) {
    }
}
