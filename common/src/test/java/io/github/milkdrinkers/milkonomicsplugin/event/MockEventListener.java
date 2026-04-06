package io.github.milkdrinkers.milkonomics.event;

@FunctionalInterface
public interface MockEventListener {
    void onEvent(MockEvent event);
}