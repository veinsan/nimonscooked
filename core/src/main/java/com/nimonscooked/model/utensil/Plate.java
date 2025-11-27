package com.nimonscooked.model.utensil;

// Hapus import Dish yang tidak terpakai (Warning removal)
// import com.nimonscooked.model.dish.Dish; 

public class Plate extends KitchenUtensil {
    private boolean isClean;

    public Plate() {
        // Sesuaikan path dengan ResourceManager ("items/plate.png")
        super("Plate", "items/plate.png"); 
        this.isClean = true;
    }

    public boolean isClean() { return isClean; }
    public void setClean(boolean clean) { this.isClean = clean; }

    @Override
    public String getDisplayName() {
        if (containedDish != null) {
            return "Plate with " + containedDish.getName();
        }
        return "Plate (" + (isClean ? "Clean" : "Dirty") + ")";
    }
}