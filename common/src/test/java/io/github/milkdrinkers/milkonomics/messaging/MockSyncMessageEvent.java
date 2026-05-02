package io.github.milkdrinkers.milkonomics.messaging;

import io.github.milkdrinkers.milkonomics.event.MockEvent;
import io.github.milkdrinkers.milkonomics.messaging.message.Message;

public class MockSyncMessageEvent extends MockEvent {
    private final Message<?> message;

    public MockSyncMessageEvent(Message<?> message) {
        this.message = message;
    }

    public Message<?> getMessage() {
        return message;
    }
}