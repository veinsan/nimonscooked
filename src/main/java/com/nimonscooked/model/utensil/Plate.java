package com.nimonscooked.model.utensil;

public class Plate extends KitchenUtensil {
    private boolean isClean;

    public Plate() {
        super("Plate");
        this.isClean = true;
    }

    public boolean isClean() {
        return isClean;
    }

    public void setClean(boolean clean) {
        this.isClean = clean;
    }
    
    @Override
    public void setContainedDish(com.nimonscooked.model.dish.Dish dish) {
        if (!isClean && dish != null) {
            throw new IllegalStateException("Cannot plate on dirty plate");
        }
        super.setContainedDish(dish);
    }
}