package com.nimonscooked.model.utensil;

import com.nimonscooked.model.dish.Dish;

public abstract class KitchenUtensil {
    protected Dish containedDish;

    public Dish getContainedDish() {
        return containedDish;
    }

    public void setContainedDish(Dish dish) {
        this.containedDish = dish;
    }
}
