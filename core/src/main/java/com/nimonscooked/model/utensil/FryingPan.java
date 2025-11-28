package com.nimonscooked.model.utensil;

import com.nimonscooked.model.ingredient.Preparable;
import com.nimonscooked.model.item.Ingredient;
import com.nimonscooked.model.thread.CookingThread; // Import Thread
import java.util.ArrayList;
import java.util.List;

public class FryingPan extends KitchenUtensil implements CookingDevice {

    private static final int MAX_CAPACITY = 3;
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
        Ingredient ing = (Ingredient) ingredient;
        return ing.getState() == Ingredient.State.CHOPPED;
    }

    @Override
    public void addIngredient(Preparable ingredient) {
        if (canAccept(ingredient)) contents.add(ingredient);
    }

    // --- IMPLEMENTASI LOGIC M2 (THREADING) ---

    @Override
    public void startCooking() {
        if (contents.isEmpty()) return;

        // Jika thread belum jalan, buat baru
        if (cookingThread == null || !cookingThread.isAlive()) {
            cookingThread = new CookingThread(this, contents);
            cookingThread.start();
        }
    }

    // stopCooking() sudah dihandle di parent class KitchenUtensil

    @Override
    public boolean isCooking() {
        return cookingThread != null && cookingThread.isRunning();
    }

    @Override
    public float getProgress() {
        if (cookingThread != null) {
            return cookingThread.getProgress();
        }
        return 0f;
    }

    // -----------------------------------------

    public List<Preparable> getContents() { return new ArrayList<>(contents); }
    public boolean isEmpty() { return contents.isEmpty(); }

    @Override
    public void clear() {
        super.clear(); // Stop thread via parent
        contents.clear();
    }

    @Override public String toString() { return "FryingPan[" + contents.size() + "]"; }

    @Override
    public String getDisplayName() {
        return contents.isEmpty() ? "Frying Pan (Empty)" : "Frying Pan (" + contents.size() + " items)";
    }
}
