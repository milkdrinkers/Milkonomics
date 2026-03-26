package io.github.milkdrinkers.milkonomicsplugin.event;

@FunctionalInterface
public interface MockEventListener {
    void onEvent(MockEvent event);
}