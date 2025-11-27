package com.nimonscooked.model.station;

import com.badlogic.gdx.Gdx;
import com.nimonscooked.model.entity.Chef;
import com.nimonscooked.model.item.Item;
import com.nimonscooked.model.utensil.CookingDevice;
import com.nimonscooked.model.ingredient.Preparable;

public class CookingStation extends Station {

    public CookingStation(String id) {
        super(id);
    }

    @Override
    public void interact(Chef chef) {
        Item heldItem = chef.getInventory();
        Item stationItem = this.getItem();

        // 1. Taruh Alat Masak
        if (heldItem instanceof CookingDevice && stationItem == null) {
            this.setItem(heldItem);
            chef.setInventory(null);
        }
        // 2. Ambil Alat Masak
        else if (heldItem == null && stationItem instanceof CookingDevice) {
            chef.setInventory(stationItem);
            this.setItem(null);
        }
        // 3. Masukkan Bahan & Masak (Instant)
        else if (heldItem instanceof Preparable && stationItem instanceof CookingDevice) {
            CookingDevice device = (CookingDevice) stationItem;
            if (device.canAccept((Preparable) heldItem)) {
                device.addIngredient((Preparable) heldItem);
                chef.setInventory(null);

                // PROSES INSTAN (M1 Spec)
                device.startCooking();
                Gdx.app.log("CookingStation", "Cooked instantly!");
            }
        }
    }
}
