package com.nimonscooked.model.utensil;

import com.nimonscooked.model.ingredient.Preparable;
import com.nimonscooked.model.item.Ingredient;
import com.nimonscooked.model.thread.CookingThread;
import java.util.ArrayList;
import java.util.List;

public class Oven extends KitchenUtensil implements CookingDevice {

    private static final int MAX_CAPACITY = 5;
    private final List<Preparable> contents;

    public Oven() {
        super("Oven", "stations/stove.png");
        this.contents = new ArrayList<>();
    }

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
        if (contents.isEmpty()) return;
        if (cookingThread == null || !cookingThread.isAlive()) {
            cookingThread = new CookingThread(this, contents);
            cookingThread.start();
        }
    }

    @Override
    public boolean isCooking() {
        return cookingThread != null && cookingThread.isRunning();
    }

    @Override
    public float getProgress() {
        return (cookingThread != null) ? cookingThread.getProgress() : 0f;
    }

    public List<Preparable> getContents() { return new ArrayList<>(contents); }
    public boolean isEmpty() { return contents.isEmpty(); }

    @Override
    public void clear() {
        super.clear();
        contents.clear();
    }

    @Override public String toString() { return "Oven[" + contents.size() + "]"; }

    @Override
    public String getDisplayName() {
        return contents.isEmpty() ? "Oven (Empty)" : "Oven (" + contents.size() + " items)";
    }
}
