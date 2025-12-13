package com.nimonscooked.model.station;

import com.badlogic.gdx.Gdx;
import com.nimonscooked.manager.AudioManager;
import com.nimonscooked.model.entity.Chef;
import com.nimonscooked.model.item.Item;
import com.nimonscooked.model.utensil.Plate;

public class WashingStation extends Station {

    private float holdProgress = 0f;
    private static final float WASH_DURATION = 3.0f;
    private boolean hasPlayedSound = false;
    private boolean isBeingHeld = false;

    public WashingStation(String id, float x, float y) {
        super(id, x, y, 64, 64);
    }

    @Override
    public void interact(Chef chef) {
        Item heldItem = chef.getInventory();
        Item stationItem = this.getItem();

        // PLACE dirty plate
        if (heldItem instanceof Plate && stationItem == null) {
            Plate plate = (Plate) heldItem;
            if (!plate.isClean()) {
                this.setItem(plate);
                chef.setInventory(null);
                this.holdProgress = 0f;
                Gdx.app.log("WashingStation", "Placed dirty plate");
            }
        } 
        // TAKE clean plate (hanya kalau tidak lagi di-hold)
        else if (heldItem == null && stationItem instanceof Plate) {
            Plate plate = (Plate) stationItem;
            
            if (plate.isClean() && !isBeingHeld) {
                chef.setInventory(plate);
                this.setItem(null);
                this.holdProgress = 0f;
                Gdx.app.log("WashingStation", "Took clean plate");
            }
        }
    }

    @Override
    public void processHold(Chef chef, float delta) {
        Item stationItem = this.getItem();
        
        if (stationItem instanceof Plate && chef.getInventory() == null) {
            Plate plate = (Plate) stationItem;
            
            if (!plate.isClean()) {
                isBeingHeld = true;
                chef.setBusy(true); // LOCK CHEF MOVEMENT ← FROM YOUR VERSION
                holdProgress += delta;
                
                if (!hasPlayedSound) {
                    AudioManager.getInstance().playSound("sfx/trash.wav");
                    hasPlayedSound = true;
                }
                
                if (holdProgress >= WASH_DURATION) {
                    plate.setClean(true);
                    holdProgress = 0f;
                    hasPlayedSound = false;
                    isBeingHeld = false;
                    chef.setBusy(false); // UNLOCK CHEF ← FROM YOUR VERSION
                    Gdx.app.log("WashingStation", "Plate is now clean!");
                }
            }
        } else {
            if (isBeingHeld) {
                isBeingHeld = false;
                hasPlayedSound = false;
                chef.setBusy(false); // UNLOCK CHEF ← FROM YOUR VERSION
            }
        }
    }

    public boolean isActive() {
        return isBeingHeld;
    }

    public float getProgress() {
        return Math.min(1f, holdProgress / WASH_DURATION);
    }
}