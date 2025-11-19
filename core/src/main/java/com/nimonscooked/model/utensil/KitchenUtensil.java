package com.nimonscooked.model.utensil;

import com.nimonscooked.model.Item;
import com.nimonscooked.model.dish.Dish;

public abstract class KitchenUtensil extends Item {
    protected Dish containedDish;

    public KitchenUtensil(String name) {
        super(name);
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
    }

    @Override
    public String getDisplayName() {
        if (containedDish != null) {
            return name + " with " + containedDish.getName();
        }
        return name + " (Empty)";
    }
}