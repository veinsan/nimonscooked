package com.nimonscooked.model.utensil;

import com.nimonscooked.model.ingredient.Preparable;
import com.nimonscooked.model.ingredient.Ingredient;

public class FryingPan extends KitchenUtensil implements CookingDevice {
    
    private static final int MAX_CAPACITY = 1;
    
    public FryingPan() {
        super("Frying Pan");
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
            return ing.getState() == Ingredient.State.CHOPPED 
                   && contents.size() < MAX_CAPACITY;
        }
        return false;
    }

    @Override
    public void addIngredient(Preparable ingredient) {
        if (canAccept(ingredient)) {
            contents.add(ingredient);
        } else {
            throw new IllegalArgumentException("Cannot add this ingredient to frying pan");
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