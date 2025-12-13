package com.nimonscooked.model.station;

import java.util.Stack;

import com.badlogic.gdx.Gdx;
import com.nimonscooked.model.entity.Chef;
import com.nimonscooked.model.utensil.Plate;

public class PlateStorage extends Station {

    private Stack<Plate> plateStack;

    public PlateStorage(String id, float x, float y) {
        super(id, x, y, 64, 64);
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
        else if (chef.getInventory() instanceof Plate) {
            Plate held = (Plate) chef.getInventory();
            if (held.isEmpty() && held.isClean()) {
                plateStack.push(held);
                chef.setInventory(null);
                Gdx.app.log("PlateStorage", "Put back a clean plate.");
            }
        }
    }

    public synchronized void returnDirtyPlate() {
        Plate dirtyPlate = new Plate();
        dirtyPlate.setClean(false);
        plateStack.push(dirtyPlate);
        Gdx.app.log("PlateStorage", "Dirty plate returned! Total: " + plateStack.size());
    }
    
    public Plate peekTopPlate() {
        if (plateStack.isEmpty()) return null;
        return plateStack.peek();
    }
}