package io.github.milkdrinkers.milkonomicsplugin.messaging;

import io.github.milkdrinkers.milkonomicsplugin.event.MockEventSystem;
import io.github.milkdrinkers.milkonomicsplugin.messaging.adapter.receiver.ReceiverAdapter;
import io.github.milkdrinkers.milkonomicsplugin.messaging.message.IncomingMessage;

public class MockReceiverAdapter extends ReceiverAdapter {
    @Override
    public void accept(IncomingMessage<?, ?> message) {
        MockEventSystem.fireEvent(new MockSyncMessageEvent(message));
    }
}
