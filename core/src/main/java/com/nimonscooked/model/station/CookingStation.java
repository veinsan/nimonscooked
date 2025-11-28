package com.nimonscooked.model.station;

import com.badlogic.gdx.Gdx;
import com.nimonscooked.model.entity.Chef;
import com.nimonscooked.model.item.Item;
import com.nimonscooked.model.utensil.CookingDevice;
import com.nimonscooked.model.ingredient.Preparable;

public class CookingStation extends Station {
    public CookingStation(String id) { super(id); }

    @Override
    public void interact(Chef chef) {
        if (chef.isBusy()) return;

        Item heldItem = chef.getInventory();
        Item stationItem = this.getItem();

        if (heldItem instanceof CookingDevice && stationItem == null) {
            this.setItem(heldItem);
            chef.setInventory(null);
            // Resume cooking if contents exist
            ((CookingDevice) heldItem).startCooking();
        }
        else if (heldItem == null && stationItem instanceof CookingDevice) {
            CookingDevice device = (CookingDevice) stationItem;
            device.stopCooking(); // STOP THREAD
            chef.setInventory(stationItem);
            this.setItem(null);
        }
        else if (heldItem instanceof Preparable && stationItem instanceof CookingDevice) {
            CookingDevice device = (CookingDevice) stationItem;
            if (device.canAccept((Preparable) heldItem)) {
                device.addIngredient((Preparable) heldItem);
                chef.setInventory(null);
                device.startCooking(); // START THREAD
            }
        }
    }
}
