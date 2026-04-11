package io.github.milkdrinkers.milkonomics.config.exception;

import org.spongepowered.configurate.serialize.SerializationException;

public class ConfigValidationException extends SerializationException {
    public ConfigValidationException(String message) {
        super(message);
    }
}
