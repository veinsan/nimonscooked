package com.nimonscooked.model.station;

import com.badlogic.gdx.Gdx;
import com.nimonscooked.model.entity.Chef;
import com.nimonscooked.model.utensil.Plate;
import java.util.Stack;

public class PlateStorage extends Station {

    // Stack untuk menyimpan piring (Bersih di bawah, Kotor di atas)
    private Stack<Plate> plateStack;

    public PlateStorage(String id) {
        super(id);
        this.plateStack = new Stack<>();

        // Inisialisasi 4 piring bersih (sesuai spec Map C)
        for (int i = 0; i < 4; i++) {
            Plate p = new Plate();
            p.setClean(true);
            plateStack.push(p);
        }
    }

    @Override
    public void interact(Chef chef) {
        // Chef hanya bisa ambil piring jika tangan kosong
        if (chef.getInventory() == null) {
            if (!plateStack.isEmpty()) {
                // Ambil piring teratas (bisa kotor/bersih)
                Plate takenPlate = plateStack.pop();
                chef.setInventory(takenPlate);

                String status = takenPlate.isClean() ? "Clean" : "Dirty";
                Gdx.app.log("PlateStorage", "Took a " + status + " plate. Remaining: " + plateStack.size());
            } else {
                Gdx.app.log("PlateStorage", "No plates available!");
            }
        }
    }

    /**
     * Dipanggil oleh ServingCounter setelah 10 detik.
     * Mengembalikan piring kotor ke tumpukan paling atas.
     */
    public synchronized void returnDirtyPlate() {
        Plate dirtyPlate = new Plate();
        dirtyPlate.setClean(false);
        plateStack.push(dirtyPlate);
        Gdx.app.log("PlateStorage", "Dirty plate returned! Total: " + plateStack.size());
    }
}
