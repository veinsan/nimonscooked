package com.nimonscooked.model.utensil;

import com.nimonscooked.model.item.Item;
import com.nimonscooked.model.dish.Dish;
import com.nimonscooked.model.thread.CookingThread;

public abstract class KitchenUtensil extends Item {
    protected Dish containedDish;
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
        stopCooking();
    }

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