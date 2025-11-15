package com.nimonscooked.model.utensil;

import com.nimonscooked.model.Item;
import com.nimonscooked.model.dish.Dish;

public abstract class KitchenUtensil extends Item {
    protected Dish containedDish;

    public KitchenUtensil(String name) {
        super(name);
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
}