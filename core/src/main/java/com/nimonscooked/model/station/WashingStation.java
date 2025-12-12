package com.nimonscooked.model.station;

import com.badlogic.gdx.Gdx;
import com.nimonscooked.manager.AudioManager; // Pastikan import ini ada
import com.nimonscooked.model.entity.Chef;
import com.nimonscooked.model.item.Item;
import com.nimonscooked.model.utensil.Plate;
import com.nimonscooked.model.thread.InteractionThread;

public class WashingStation extends Station {

    // Flag untuk Visual Renderer
    private boolean isProcessing = false;
    private InteractionThread currentTask = null;

    public WashingStation(String id) {
        super(id);
    }

    @Override
    public void interact(Chef chef) {
        if (chef.isBusy()) return;

        Item heldItem = chef.getInventory();
        Item stationItem = this.getItem();

        // 1. Taruh Piring Kotor
        if (heldItem instanceof Plate && stationItem == null) {
            Plate plate = (Plate) heldItem;
            // Hanya terima piring kotor (sesuai spec [118])
            if (!plate.isClean()) {
                this.setItem(plate);
                chef.setInventory(null);
                Gdx.app.log("WashingStation", "Placed dirty plate.");
            }
        } 
        // 2. Ambil Piring / Cuci Piring
        else if (heldItem == null && stationItem instanceof Plate) {
            final Plate plate = (Plate) stationItem;
            
            // Jika piring sudah bersih -> Ambil
            if (plate.isClean()) {
                chef.setInventory(plate);
                this.setItem(null);
                Gdx.app.log("WashingStation", "Took clean plate.");
            } 
            // Jika piring kotor -> Cuci (Spec [283]: 3 Detik)
            else {
                // Set flag active
                this.isProcessing = true;

                InteractionThread washTask = new InteractionThread(chef, 3.0f) {
                    @Override
                    public void onComplete() {
                        plate.setClean(true);
                        // Reset flag active
                        isProcessing = false;
                        currentTask = null;
                        
                        Gdx.app.log("WashingStation", "Plate is now CLEAN!");
                        // Play SFX (Pastikan file audio ada atau handle exception)
                        try {
                            AudioManager.getInstance().playSound("sfx/splash.wav"); // Ganti nama file sesuai aset
                        } catch (Exception e) {}
                    }
                };
                
                this.currentTask = washTask;

                // Chef jadi busy
                // Kita gunakan isChopping atau flag busy umum lainnya biar animasinya diam/kerja
                chef.isChopping = true; 
                chef.setCurrentInteraction(washTask);
                washTask.start();
                
                Gdx.app.log("WashingStation", "Washing started (3s)...");
            }
        }
    }

    // --- Method untuk WorldRenderer ---

    public boolean isActive() {
        return isProcessing;
    }

    public float getProgress() {
        if (currentTask != null) {
            return currentTask.getProgress();
        }
        return 0f;
    }
}