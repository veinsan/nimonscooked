package com.nimonscooked.model.utensil;

import com.nimonscooked.model.ingredient.Preparable;
import com.nimonscooked.model.item.Ingredient; // Import yang sudah diperbaiki
import java.util.ArrayList;
import java.util.List;

public class BoilingPot extends KitchenUtensil implements CookingDevice {
    
    private static final int MAX_CAPACITY = 2;
    private final List<Preparable> contents;

    public BoilingPot() {
        // Gunakan "items/pan.png" sementara jika belum ada tekstur khusus pot, atau tambahkan "items/pot.png"
        super("Boiling Pot", "items/pan.png"); 
        this.contents = new ArrayList<>();
    }

    // ... (Sisa method sama seperti sebelumnya) ...
    
    @Override public boolean isPortable() { return true; }
    @Override public int capacity() { return MAX_CAPACITY; }

    @Override
    public boolean canAccept(Preparable ingredient) {
        if (contents.size() >= MAX_CAPACITY) return false;
        if (!(ingredient instanceof Ingredient)) return false;
        Ingredient ing = (Ingredient) ingredient;
        String name = ing.getName().toLowerCase();
        return (name.contains("rice") || name.contains("pasta")) && ing.getState() == Ingredient.State.RAW;
    }

    @Override
    public void addIngredient(Preparable ingredient) {
        if (canAccept(ingredient)) contents.add(ingredient);
    }

    @Override
    public void startCooking() {
        if (!contents.isEmpty()) {
            for (Preparable ingredient : contents) ingredient.cook();
        }
    }

    public List<Preparable> getContents() { return new ArrayList<>(contents); }
    public boolean isEmpty() { return contents.isEmpty(); }
    public void clear() { contents.clear(); }

    @Override public String toString() { return "BoilingPot[" + contents.size() + "]"; }
    
    @Override
    public String getDisplayName() {
        return contents.isEmpty() ? "Boiling Pot (Empty)" : "Boiling Pot (" + contents.size() + " items)";
    }
}