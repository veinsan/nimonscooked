package com.nimonscooked.model.utensil;

public class Oven extends KitchenUtensil {
    public Oven() {
        super("Oven");
    }
    
    public boolean isPortable() {
        return false;
    }
}