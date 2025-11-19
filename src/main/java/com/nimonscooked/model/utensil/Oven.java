package com.nimonscooked.model.utensil;

public class Oven extends KitchenUtensil implements CookingDevice {

    public Oven() {
        super("Oven");
    }

    public boolean isPortable() {
        return false;
    }
}
