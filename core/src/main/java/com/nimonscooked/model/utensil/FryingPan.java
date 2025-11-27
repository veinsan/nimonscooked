package com.nimonscooked.model.utensil;

import com.nimonscooked.model.ingredient.Preparable;
import com.nimonscooked.model.item.Ingredient;
import java.util.ArrayList;
import java.util.List;

public class FryingPan extends KitchenUtensil implements CookingDevice {

    private static final int MAX_CAPACITY = 3; //
    private final List<Preparable> contents;

    public FryingPan() {
        super("Frying Pan", "items/pan.png");
        this.contents = new ArrayList<>();
    }

    @Override public boolean isPortable() { return true; }
    @Override public int capacity() { return MAX_CAPACITY; }

    @Override
    public boolean canAccept(Preparable ingredient) {
        if (contents.size() >= MAX_CAPACITY) return false;
        if (!(ingredient instanceof Ingredient)) return false;
        // Frying pan hanya terima Chopped [cite: 137]
        return ((Ingredient)ingredient).getState() == Ingredient.State.CHOPPED;
    }

    @Override
    public void addIngredient(Preparable ingredient) {
        if (canAccept(ingredient)) contents.add(ingredient);
    }

    @Override
    public void startCooking() {
        // LOGIKA M1: Instant Cooking
        // Spesifikasi[cite: 110]: "memasak bahan... menjadi COOKED"
        if (!contents.isEmpty()) {
            for (Preparable ingredient : contents) {
                ingredient.cook(); // Langsung matang seketika
            }
        }
    }

    public List<Preparable> getContents() { return new ArrayList<>(contents); }
    public boolean isEmpty() { return contents.isEmpty(); }
    public void clear() { contents.clear(); }

    @Override public String toString() { return "FryingPan[" + contents.size() + "]"; }

    @Override
    public String getDisplayName() {
        return contents.isEmpty() ? "Frying Pan (Empty)" : "Frying Pan (" + contents.size() + " items)";
    }
}
