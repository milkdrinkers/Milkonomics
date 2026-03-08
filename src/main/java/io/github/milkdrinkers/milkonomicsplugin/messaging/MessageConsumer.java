package io.github.milkdrinkers.milkonomicsplugin.messaging;

import io.github.milkdrinkers.milkonomicsplugin.messaging.message.IncomingMessage;

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
