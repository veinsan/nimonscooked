package com.nimonscooked.model.station;

import com.badlogic.gdx.Gdx;
import com.nimonscooked.model.entity.Chef;
import com.nimonscooked.model.item.Item;
import com.nimonscooked.model.utensil.Plate;

public class WashingStation extends Station {

    public WashingStation(String id) {
        super(id);
    }

    @Override
    public void interact(Chef chef) {
        Item heldItem = chef.getInventory();
        Item stationItem = this.getItem();

        // 1. Taruh Piring Kotor
        if (heldItem instanceof Plate && stationItem == null) {
            Plate plate = (Plate) heldItem;
            if (!plate.isClean()) {
                this.setItem(plate);
                chef.setInventory(null);
            }
        }
        // 2. Cuci (Instant - M1 Spec)
        else if (heldItem == null && stationItem instanceof Plate) {
            Plate plate = (Plate) stationItem;
            if (!plate.isClean()) {
                plate.setClean(true); // Langsung bersih
                Gdx.app.log("WashingStation", "Plate cleaned instantly!");
            } else {
                // Ambil piring jika sudah bersih
                chef.setInventory(plate);
                this.setItem(null);
            }
        }
    }
}
