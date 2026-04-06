package io.github.milkdrinkers.milkonomics.messaging;

import io.github.milkdrinkers.milkonomics.event.MockEvent;
import io.github.milkdrinkers.milkonomics.messaging.message.IncomingMessage;

public class MockSyncMessageEvent extends MockEvent {
    private final IncomingMessage<?, ?> message;

    public MockSyncMessageEvent(IncomingMessage<?, ?> message) {
        this.message = message;
    }

    public IncomingMessage<?, ?> getMessage() {
        return message;
    }
}