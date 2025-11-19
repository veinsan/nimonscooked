package com.nimonscooked.model.utensil;

import com.nimonscooked.model.ingredient.Preparable;
import com.nimonscooked.model.ingredient.Ingredient;

import java.util.ArrayList;
import java.util.List;

public class FryingPan extends KitchenUtensil implements CookingDevice {
    
    private static final int MAX_CAPACITY = 3;
    private final List<Preparable> contents;
    private boolean isCooking;

    public FryingPan() {
        super("frying_pan"); // texture key
        this.contents = new ArrayList<>();
        this.isCooking = false;
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
        if (contents.size() >= MAX_CAPACITY) {
            return false;
        }
        if (!(ingredient instanceof Ingredient)) {
            return false;
        }
        Ingredient ing = (Ingredient) ingredient;
        return ing.getState() == Ingredient.State.CHOPPED;
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
        return "FryingPan[" + contents.size() + "/" + MAX_CAPACITY + " items]";
    }

    @Override
    public String getDisplayName() {
        if (contents.isEmpty()) {
            return "Frying Pan (Empty)";
        }
        return "Frying Pan (" + contents.size() + " items)";
    }
}