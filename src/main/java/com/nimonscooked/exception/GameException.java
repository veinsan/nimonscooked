package com.nimonscooked.exception;

public class GameException extends RuntimeException {
    
    public GameException(String message) {
        super(message);
    }

    public GameException(String message, Throwable cause) {
        super(message, cause);
    }

    public GameException(Throwable cause) {
        super(cause);
    }

    @Override
    public String toString() {
        return "GameException: " + getMessage();
    }
}