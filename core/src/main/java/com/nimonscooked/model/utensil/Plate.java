package com.nimonscooked.model.utensil;

import com.nimonscooked.model.dish.Dish;

public class Plate extends KitchenUtensil {
    private boolean isClean;
    private Dish containedDish;

    public Plate() {
        super("Plate", "items/plate.png");
        this.isClean = true;
        this.containedDish = null;
    }

    public boolean isClean() {
        return isClean;
    }

    public void setClean(boolean clean) {
        this.isClean = clean;
        updateTexture();
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

    private void updateTexture() {
        if (isClean) {
            this.textureName = "items/plate.png";
        } else {
            this.textureName = "items/plate_dirty.png";
        }
    }

    @Override
    public String getDisplayName() {
        if (containedDish != null) {
            return "Plate with " + containedDish.getName();
        }
        return "Plate (" + (isClean ? "Clean" : "Dirty") + ")";
    }
}