package io.github.milkdrinkers.milkonomicsplugin.messaging;

import io.github.milkdrinkers.milkonomicsplugin.event.MockEvent;
import io.github.milkdrinkers.milkonomicsplugin.messaging.message.IncomingMessage;

public class MockSyncMessageEvent extends MockEvent {
    private final IncomingMessage<?, ?> message;

    public MockSyncMessageEvent(IncomingMessage<?, ?> message) {
        this.message = message;
    }

    public IncomingMessage<?, ?> getMessage() {
        return message;
    }
}