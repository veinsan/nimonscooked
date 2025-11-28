package com.nimonscooked.model.item;

public abstract class Item {
    protected String name;
    protected String textureName;

    public Item(String name, String textureName) {
        this.name = name;
        this.textureName = textureName;
    }

    public String getName() {
        return name;
    }

    public String getTextureName() {
        return textureName;
    }

    protected void setTextureName(String textureName) {
        this.textureName = textureName;
    }

    public abstract String getDisplayName();

    @Override
    public String toString() {
        return getDisplayName();
    }
}