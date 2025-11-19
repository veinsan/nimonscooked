package com.nimonscooked.model.utensil;

import com.nimonscooked.model.ingredient.Preparable;
import com.nimonscooked.model.ingredient.Ingredient;

import java.util.ArrayList;
import java.util.List;

public class Oven extends KitchenUtensil implements CookingDevice {
    
    private static final int MAX_CAPACITY = 5;
    private final List<Preparable> contents;
    private boolean isCooking;

    public Oven() {
        super("Oven");
        this.contents = new ArrayList<>();
        this.isCooking = false;
    }

    @Override
    public boolean isPortable() {
        return false;
    }

    @Override
    public int capacity() {
        return MAX_CAPACITY;
    }

    @Override
    public boolean canAccept(Preparable ingredient) {
        if (contents.size() >= MAX_CAPACITY) {
            return false;
        }
        if (!(ingredient instanceof Ingredient)) {
            return false;
        }
        Ingredient ing = (Ingredient) ingredient;
        String name = ing.getName().toLowerCase();
        return name.contains("pizza") || name.contains("dough") || name.contains("adonan");
    }

    @Override
    public void addIngredient(Preparable ingredient) {
        if (canAccept(ingredient)) {
            contents.add(ingredient);
        }
    }

    @Override
    public void startCooking() {
        if (!contents.isEmpty()) {
            isCooking = true;
            for (Preparable ingredient : contents) {
                ingredient.cook();
            }
        }
    }

    public List<Preparable> getContents() {
        return new ArrayList<>(contents);
    }

    public boolean isEmpty() {
        return contents.isEmpty();
    }

    public void clear() {
        contents.clear();
        isCooking = false;
    }

    @Override
    public String toString() {
        return "Oven[" + contents.size() + "/" + MAX_CAPACITY + " items]";
    }

    @Override
    public String getDisplayName() {
        if (contents.isEmpty()) {
            return "Oven (Empty)";
        }
        return "Oven (" + contents.size() + " items)";
    }
}