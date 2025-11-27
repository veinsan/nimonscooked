package com.nimonscooked.model.item;

// HAPUS semua import lain yang tidak perlu

public abstract class Item {
    // Gunakan protected agar subclass (Ingredient/Dish) bisa akses langsung
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

    public abstract String getDisplayName();
    
    @Override
    public String toString() {
        return getDisplayName();
    }
}