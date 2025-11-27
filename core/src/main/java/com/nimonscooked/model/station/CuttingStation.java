package com.nimonscooked.model.station;

import com.badlogic.gdx.Gdx;
import com.nimonscooked.model.entity.Chef;
import com.nimonscooked.model.item.Item;
import com.nimonscooked.model.item.Ingredient;

public class CuttingStation extends Station {

    public CuttingStation(String id) {
        super(id);
    }

    @Override
    public void interact(Chef chef) {
        Item heldItem = chef.getInventory();
        Item stationItem = this.getItem();

        // 1. Taruh Bahan
        if (heldItem instanceof Ingredient && stationItem == null) {
            this.setItem(heldItem);
            chef.setInventory(null);
        }
        // 2. Ambil Bahan
        else if (heldItem == null && stationItem != null) {
            chef.setInventory(stationItem);
            this.setItem(null);
        }
        // 3. Potong (Instant - M1 Spec)
        else if (heldItem == null && stationItem instanceof Ingredient) {
            Ingredient ing = (Ingredient) stationItem;
            if (ing.canBeChopped()) {
                ing.chop(); // Berubah jadi CHOPPED seketika
                Gdx.app.log("CuttingStation", "Chopped instantly!");
            }
        }
    }
}
