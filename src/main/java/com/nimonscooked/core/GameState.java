package com.nimonscooked.core;

public enum GameState {
    MENU("Main Menu"),
    STAGE_SELECT("Stage Selection"),
    PLAYING("Playing"),
    PAUSED("Paused"),
    STAGE_CLEARED("Stage Cleared"),
    GAME_OVER("Game Over");

    private final String displayName;

    GameState(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public boolean isPlayable() {
        return this == PLAYING;
    }

    public boolean isEnded() {
        return this == STAGE_CLEARED || this == GAME_OVER;
    }

    @Override
    public String toString() {
        return displayName;
    }
}