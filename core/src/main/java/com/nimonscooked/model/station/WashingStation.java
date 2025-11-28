package com.nimonscooked.model.station;

import com.badlogic.gdx.Gdx;
import com.nimonscooked.model.entity.Chef;
import com.nimonscooked.model.item.Item;
import com.nimonscooked.model.utensil.Plate;
import com.nimonscooked.model.thread.InteractionThread; // Pastikan import ini

public class WashingStation extends Station {

    public WashingStation(String id) {
        super(id);
    }

    @Override
    public void interact(Chef chef) {
        if (chef.isBusy()) return;

        Item heldItem = chef.getInventory();
        Item stationItem = this.getItem();

        // 1. Taruh Piring Kotor (Hanya bisa jika piring kotor)
        if (heldItem instanceof Plate && stationItem == null) {
            Plate plate = (Plate) heldItem;
            if (!plate.isClean()) {
                this.setItem(plate);
                chef.setInventory(null);
                Gdx.app.log("WashingStation", "Placed dirty plate.");
            }
        }
        // 2. Ambil Piring (Hanya jika sudah bersih)
        else if (heldItem == null && stationItem instanceof Plate) {
            Plate plate = (Plate) stationItem;
            if (plate.isClean()) {
                chef.setInventory(plate);
                this.setItem(null);
                Gdx.app.log("WashingStation", "Took clean plate.");
            }
            // 3. PROSES CUCI (Concurrency M2)
            // Spesifikasi: Durasi 3 detik per piring, Chef Busy
            else {
                InteractionThread washTask = new InteractionThread(chef, 3.0f) {
                    @Override
                    public void onComplete() {
                        plate.setClean(true); // Jadi bersih setelah 3 detik
                        Gdx.app.log("WashingStation", "Plate is now CLEAN!");
                        // Play Sound 'Wash' disini nanti
                    }
                };

                chef.isChopping = true; // Gunakan animasi chop sementara untuk cuci
                chef.setCurrentInteraction(washTask);
                washTask.start();
                Gdx.app.log("WashingStation", "Washing started (3s)...");
            }
        }
    }
}
