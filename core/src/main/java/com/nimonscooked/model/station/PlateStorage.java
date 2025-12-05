package com.nimonscooked.model.station;

import com.badlogic.gdx.Gdx;
import com.nimonscooked.model.entity.Chef;
import com.nimonscooked.model.utensil.Plate;
import java.util.Stack;

public class PlateStorage extends Station {

    private Stack<Plate> plateStack;

    public PlateStorage(String id) {
        super(id);
        this.plateStack = new Stack<>();
        for (int i = 0; i < 4; i++) {
            Plate p = new Plate();
            p.setClean(true);
            plateStack.push(p);
        }
    }

    @Override
    public void interact(Chef chef) {
        if (chef.getInventory() == null) {
            if (!plateStack.isEmpty()) {
                Plate takenPlate = plateStack.pop();
                chef.setInventory(takenPlate);
                String status = takenPlate.isClean() ? "Clean" : "Dirty";
                Gdx.app.log("PlateStorage", "Took a " + status + " plate. Remaining: " + plateStack.size());
            } else {
                Gdx.app.log("PlateStorage", "No plates available!");
            }
        }
    }

    public synchronized void returnDirtyPlate() {
        Plate dirtyPlate = new Plate();
        dirtyPlate.setClean(false);
        plateStack.push(dirtyPlate);
        Gdx.app.log("PlateStorage", "Dirty plate returned! Total: " + plateStack.size());
    }
}