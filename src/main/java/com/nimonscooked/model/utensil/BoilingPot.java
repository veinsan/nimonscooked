package com.nimonscooked.model.utensil;

import com.nimonscooked.model.ingredient.Preparable;
import com.nimonscooked.model.ingredient.Ingredient;

public class BoilingPot extends KitchenUtensil implements CookingDevice {
    
    private static final int MAX_CAPACITY = 1;
    
    public BoilingPot() {
        super("Boiling Pot");
    }

    @Override
    public boolean isPortable() {
        return true;
    }

    @Override
    public int capacity() {
        return MAX_CAPACITY;
    }

    @Override
    public boolean canAccept(Preparable ingredient) {
        if (ingredient instanceof Ingredient) {
            Ingredient ing = (Ingredient) ingredient;
            String name = ing.getName().toLowerCase();
            return (name.contains("beras") || name.contains("pasta")) 
                   && contents.size() < MAX_CAPACITY;
        }
        return false;
    }

    @Override
    public void addIngredient(Preparable ingredient) {
        if (canAccept(ingredient)) {
            contents.add(ingredient);
        } else {
            throw new IllegalArgumentException("Cannot add this ingredient to boiling pot");
        }
    }

    @Override
    public void startCooking() {
        for (Preparable p : contents) {
            if (p.canBeCooked()) {
                p.cook();
            }
        }
    }
}