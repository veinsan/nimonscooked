package com.nimonscooked.exception;

public class InvalidActionException extends GameException {
    
    private final String action;
    private final String reason;

    public InvalidActionException(String action, String reason) {
        super("Invalid action '" + action + "': " + reason);
        this.action = action;
        this.reason = reason;
    }

    public InvalidActionException(String message) {
        super(message);
        this.action = "Unknown";
        this.reason = message;
    }

    public String getAction() {
        return action;
    }

    public String getReason() {
        return reason;
    }

    @Override
    public String toString() {
        return "InvalidActionException: Action='" + action + "', Reason='" + reason + "'";
    }
}