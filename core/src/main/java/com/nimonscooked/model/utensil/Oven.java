package com.nimonscooked.model.utensil;

import com.nimonscooked.model.ingredient.Preparable;
import com.nimonscooked.model.item.Ingredient;
import java.util.ArrayList;
import java.util.List;

public class Oven extends KitchenUtensil implements CookingDevice {
    
    private static final int MAX_CAPACITY = 5;
    private final List<Preparable> contents;

    public Oven() {
        // Oven biasanya statis, tapi jika dianggap Item di inventory, gunakan tekstur stove
        super("Oven", "stations/stove.png");
        this.contents = new ArrayList<>();
    }

    // ... (Sisa method sama seperti sebelumnya) ...
    
    @Override public boolean isPortable() { return false; }
    @Override public int capacity() { return MAX_CAPACITY; }

    @Override
    public boolean canAccept(Preparable ingredient) {
        if (contents.size() >= MAX_CAPACITY) return false;
        if (!(ingredient instanceof Ingredient)) return false;
        Ingredient ing = (Ingredient) ingredient;
        String name = ing.getName().toLowerCase();
        return name.contains("pizza") || name.contains("dough");
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

    @Override public String toString() { return "Oven[" + contents.size() + "]"; }
    
    @Override
    public String getDisplayName() {
        return contents.isEmpty() ? "Oven (Empty)" : "Oven (" + contents.size() + " items)";
    }
}