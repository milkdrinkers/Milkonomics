package io.github.milkdrinkers.milkonomics.messaging;

import io.github.milkdrinkers.milkonomics.event.MockEventSystem;
import io.github.milkdrinkers.milkonomics.messaging.adapter.receiver.ReceiverAdapter;
import io.github.milkdrinkers.milkonomics.messaging.message.IncomingMessage;

public class MockReceiverAdapter extends ReceiverAdapter {
    @Override
    public void accept(IncomingMessage<?, ?> message) {
        MockEventSystem.fireEvent(new MockSyncMessageEvent(message));
    }
}
