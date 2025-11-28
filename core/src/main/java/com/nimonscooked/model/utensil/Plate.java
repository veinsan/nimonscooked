package com.nimonscooked.model.utensil;

public class Plate extends KitchenUtensil {
    private boolean isClean;

    public Plate() {
        super("Plate", "items/plate.png");
        this.isClean = true;
    }

    public boolean isClean() {
        return isClean;
    }

    public void setClean(boolean clean) {
        this.isClean = clean;
        updateTexture();
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