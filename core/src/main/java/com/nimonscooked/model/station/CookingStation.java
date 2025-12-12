package com.nimonscooked.model.station;

import com.nimonscooked.model.entity.Chef;
import com.nimonscooked.model.item.Item;
import com.nimonscooked.model.utensil.CookingDevice; // Pastikan punya class ini (Pan/Pot)

public class CookingStation extends Station {

    public CookingStation(String id) {
        super(id);
    }

    @Override
    public void interact(Chef chef) {
        // Logika interact standar (ambil/taruh panci)
        // ... (gunakan logika standar interact kamu) ...
        
        // Contoh sederhana ambil/taruh:
        Item held = chef.getInventory();
        Item stored = this.getItem();

        if (held != null && stored == null) {
            this.setItem(held);
            chef.setInventory(null);
        } else if (held == null && stored != null) {
            this.setItem(null);
            chef.setInventory(stored);
        }
    }

    /**
     * Cek apakah kompor menyala.
     * Kompor menyala JIKA ada alat masak DAN alat masaknya sedang proses memasak.
     */
    public boolean isActive() {
        Item item = this.getItem();
        if (item instanceof CookingDevice) {
            return ((CookingDevice) item).isCooking(); // Pastikan CookingDevice punya isCooking()
        }
        return false;
    }
    
    // Untuk ambil progress masak
    public float getProgress() {
        Item item = this.getItem();
        if (item instanceof CookingDevice) {
             return ((CookingDevice) item).getProgress(); // 0.0f - 1.0f
        }
        return 0f;
    }
}