package com.nimonscooked.model.menu;

public enum StageOption {
    BURGER_C("Burger Map (Type C)");

    private final String displayName;

    StageOption(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
