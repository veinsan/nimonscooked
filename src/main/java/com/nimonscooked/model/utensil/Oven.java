package com.nimonscooked.model.utensil;

import com.nimonscooked.interfaces.CookingDevice;
import com.nimonscooked.interfaces.Preparable;

public class Oven extends KitchenUtensil implements CookingDevice {

    public Oven() {
        super("Oven");
    }

    public boolean isPortable() {
        return false;
    }

    public int capacity() { return 1; }

    public boolean canAccept(Preparable ingredient) { return false; }

    public void addIngredient(Preparable ingredient) {
        if (canAccept(ingredient)) {
            contents.add(ingredient);
        }
    }

    public void startCooking() {
        for (Preparable p : contents) {
            p.cook();
        }
    }
}
