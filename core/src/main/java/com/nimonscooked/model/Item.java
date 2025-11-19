package com.nimonscooked.model;

public abstract class Item {
    protected String name;

    public Item(String name) {
        this.name = name;
    }

    // name = texture key (PNG file)
    public String getName() {
        return name;
    }

    // overridden untuk user-facing string
    public String getDisplayName() {
        return name;
    }
}
