package com.nimonscooked.model.utensil;

import com.nimonscooked.model.dish.Dish;

public class Plate extends KitchenUtensil {
    private boolean isClean;

    public Plate() {
        super("plate"); // texture key
        this.isClean = true;
    }

    public boolean isClean() {
        return isClean;
    }

    public void setClean(boolean clean) {
        this.isClean = clean;
    }

    @Override
    public String getDisplayName() {
        if (containedDish != null) {
            return "Plate with " + containedDish.getName();
        }
        return "Plate (" + (isClean ? "Clean" : "Dirty") + ")";
    }
}
