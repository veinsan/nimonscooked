package com.nimonscooked.exception;

public class GameLoadException extends RuntimeException {
    public GameLoadException(String message) {
        super(message);
    }

    public GameLoadException(String message, Throwable cause) {
        super(message, cause);
    }
}