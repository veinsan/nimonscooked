package com.nimonscooked.model.station;

import com.badlogic.gdx.Gdx;
import com.nimonscooked.manager.GameManager;
import com.nimonscooked.model.entity.Chef;
import com.nimonscooked.model.item.Item;
import com.nimonscooked.model.utensil.Plate;
import com.nimonscooked.model.dish.Dish;

public class ServingCounter extends Station {

    // Simpan skor lokal atau akses global score di GameManager

    public ServingCounter(String id) {
        super(id);
    }

    @Override
    public void interact(Chef chef) {
        Item heldItem = chef.getInventory();

        if (heldItem instanceof Plate) {
            Plate plate = (Plate) heldItem;

            // Validasi: Piring harus ada isinya (Dish)
            if (!plate.isEmpty() && plate.getContainedDish() != null) {
                Dish dishToServe = plate.getContainedDish();

                // --- INTEGRASI KE ORDER MANAGER ---
                int resultScore = GameManager.getInstance().orderManager.submitOrder(dishToServe);

                if (resultScore > 0) {
                    Gdx.app.log("ServingCounter", "SUCCESS! Score +" + resultScore);
                    // TODO: Tambahkan score ke GameManager (nanti buat field score di GameManager)
                } else {
                    Gdx.app.log("ServingCounter", "FAILED! Wrong Order.");
                }

                // Mekanisme Pengembalian Piring
                // Sesuai spec: Plate jadi kotor dan kembali ke PlateStorage (atau hilang dari tangan chef dulu)
                // Untuk M1 sederhana: Piring hilang dari tangan chef.
                chef.setInventory(null);

            } else {
                Gdx.app.log("ServingCounter", "Cannot serve empty plate!");
            }
        }
    }
}
