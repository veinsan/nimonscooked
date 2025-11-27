package com.nimonscooked.model.station;

import com.badlogic.gdx.Gdx;
import com.nimonscooked.model.entity.Chef;
import com.nimonscooked.model.utensil.Plate;

public class PlateStorage extends Station {

    public PlateStorage(String id) {
        super(id);
    }

    @Override
    public void interact(Chef chef) {
        // Chef hanya bisa ambil piring jika tangan kosong
        if (chef.getInventory() == null) {
            // Spawn piring bersih baru (Unlimited supply sesuai spec dasar, atau limit nanti)
            Plate newPlate = new Plate();
            chef.setInventory(newPlate);
            Gdx.app.log("PlateStorage", "Took a clean plate.");
        }
    }
}