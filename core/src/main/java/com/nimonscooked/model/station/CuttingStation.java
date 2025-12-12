package com.nimonscooked.model.station;

import com.nimonscooked.manager.AudioManager;
import com.nimonscooked.model.entity.Chef;
import com.nimonscooked.model.item.Item;
import com.nimonscooked.model.item.Ingredient;
import com.nimonscooked.model.thread.InteractionThread;

public class CuttingStation extends Station {
    
    // Flag untuk memberi tahu Renderer ganti gambar
    private boolean isProcessing = false;
    // Referensi ke thread aktif untuk mengambil nilai progress bar
    private InteractionThread currentTask = null;

    public CuttingStation(String id) {
        super(id);
    }

    @Override
    public void interact(Chef chef) {
        if (chef.isBusy()) return;

        Item heldItem = chef.getInventory();
        Item stationItem = this.getItem();

        // 1. Taruh barang ke meja
        if (heldItem instanceof Ingredient && stationItem == null) {
            if (((Ingredient) heldItem).canBeChopped()) {
                this.setItem(heldItem);
                chef.setInventory(null);
            }
        } 
        // 2. Ambil barang dari meja
        else if (heldItem == null && stationItem != null) {
            // Kalau sedang motong, jangan boleh diambil (opsional, biar ga bug)
            if (!isProcessing) {
                chef.setInventory(stationItem);
                this.setItem(null);
            }
        } 
        // 3. Aksi Memotong (Interact)
        else if (heldItem == null && stationItem instanceof Ingredient) {
            final Ingredient ing = (Ingredient) stationItem;
            if (ing.canBeChopped()) {
                
                // Set status station jadi AKTIF
                this.isProcessing = true;

                InteractionThread cutTask = new InteractionThread(chef, 3.0f) {
                    @Override
                    public void onComplete() {
                        ing.chop();
                        AudioManager.getInstance().playSound("sfx/chop.wav");
                        // Reset status station jadi TIDAK AKTIF
                        isProcessing = false;
                        currentTask = null;
                    }
                };
                
                // Simpan referensi task biar bisa diambil progress-nya
                this.currentTask = cutTask;

                chef.isChopping = true;
                chef.setCurrentInteraction(cutTask);
                cutTask.start();
            }
        }
    }

    // --- Method untuk WorldRenderer ---

    public boolean isActive() {
        return isProcessing;
    }

    public float getProgress() {
        if (currentTask != null) {
            return currentTask.getProgress(); // Pastikan InteractionThread punya method getProgress()
        }
        return 0f;
    }
}