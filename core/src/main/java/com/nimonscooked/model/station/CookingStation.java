package com.nimonscooked.model.station;

import com.nimonscooked.model.Item;
import com.nimonscooked.model.ingredient.Ingredient;
import com.nimonscooked.model.utensil.FryingPan;
import com.nimonscooked.model.utensil.BoilingPot;
import com.nimonscooked.model.utensil.CookingDevice;

public class CookingStation extends Station {
    
    private CookingDevice cookingDevice;

    public CookingStation(String id) {
        super(id);
        this.cookingDevice = null;
    }

    public boolean placeCookingDevice(CookingDevice device) {
        if (cookingDevice != null) {
            return false;
        }
        this.cookingDevice = device;
        return true;
    }

    public CookingDevice removeCookingDevice() {
        CookingDevice removed = this.cookingDevice;
        this.cookingDevice = null;
        return removed;
    }

    public CookingDevice getCookingDevice() {
        return cookingDevice;
    }

    public boolean hasCookingDevice() {
        return cookingDevice != null;
    }

    public Item process(Item input) {
        if (cookingDevice == null) {
            System.out.println("No cooking device on this station! Place a FryingPan or BoilingPot first.");
            return null;
        }

        if (!(input instanceof Ingredient)) {
            return null;
        }

        Ingredient ingredient = (Ingredient) input;

        if (!cookingDevice.canAccept(ingredient)) {
            System.out.println("This cooking device cannot accept this ingredient!");
            return null;
        }

        if (ingredient.getState() == Ingredient.State.COOKED) {
            return null;
        }

        return new Ingredient(ingredient.getName(), Ingredient.State.COOKED);
    }

    @Override
    public String toString() {
        String deviceInfo = cookingDevice != null ? cookingDevice.toString() : "Empty";
        return "CookingStation[" + id + ", Device: " + deviceInfo + "]";
    }
}