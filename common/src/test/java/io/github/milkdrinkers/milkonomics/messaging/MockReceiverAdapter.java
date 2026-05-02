package io.github.milkdrinkers.milkonomics.messaging;

import io.github.milkdrinkers.milkonomics.event.MockEventSystem;
import io.github.milkdrinkers.milkonomics.messaging.adapter.receiver.ReceiverAdapter;
import io.github.milkdrinkers.milkonomics.messaging.message.Message;

public class MockReceiverAdapter extends ReceiverAdapter {
    @Override
    public void accept(Message<?> message) {
        MockEventSystem.fireEvent(new MockSyncMessageEvent(message));
    }
}
