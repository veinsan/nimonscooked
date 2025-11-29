package com.nimonscooked.model.item;

public abstract class Item {
    protected String name;
    protected String textureName;

    public Item(String name, String textureName) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Item name cannot be null or empty");
        }
        if (textureName == null || textureName.trim().isEmpty()) {
            throw new IllegalArgumentException("Item texture name cannot be null or empty");
        }
        
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
        if (textureName != null && !textureName.trim().isEmpty()) {
            this.textureName = textureName;
        }
    }

    public abstract String getDisplayName();

    @Override
    public String toString() {
        return getDisplayName();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        
        Item other = (Item) obj;
        return name.equalsIgnoreCase(other.name);
    }

    @Override
    public int hashCode() {
        return name.toLowerCase().hashCode();
    }
}