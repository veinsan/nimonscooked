package com.nimonscooked.model.utensil;

import com.nimonscooked.model.item.Item;
import com.nimonscooked.model.dish.Dish;
import com.nimonscooked.model.thread.CookingThread; // Import Thread

public abstract class KitchenUtensil extends Item {
    protected Dish containedDish;

    // FIELD BARU UNTUK M2: Menyimpan status memasak
    protected CookingThread cookingThread;

    public KitchenUtensil(String name, String textureName) {
        super(name, textureName);
        this.containedDish = null;
    }

    public Dish getContainedDish() {
        return containedDish;
    }

    public void setContainedDish(Dish dish) {
        this.containedDish = dish;
    }

    public boolean isEmpty() {
        return containedDish == null;
    }

    public void clear() {
        this.containedDish = null;
        stopCooking(); // Pastikan stop masak saat dibersihkan
    }

    // Helper untuk menghentikan thread dengan aman
    public void stopCooking() {
        if (cookingThread != null && cookingThread.isAlive()) {
            cookingThread.stopCooking();
        }
        cookingThread = null;
    }

    @Override
    public String getDisplayName() {
        if (containedDish != null) {
            return name + " with " + containedDish.getName();
        }
        return name + " (Empty)";
    }
}
