package io.github.milkdrinkers.milkonomics.messaging;

import io.github.milkdrinkers.milkonomics.messaging.message.IncomingMessage;

/**
 * A class with the capability of receiving incoming messages.
 */
public interface MessageConsumer {
    /**
     * Handle receiving a message
     *
     * @param message the message
     */
    void consumeMessage(final IncomingMessage<?, ?> message);
}
