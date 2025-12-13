package com.nimonscooked.model.station;

import com.badlogic.gdx.Gdx;
import com.nimonscooked.manager.AudioManager;
import com.nimonscooked.model.entity.Chef;
import com.nimonscooked.model.item.Item;
import com.nimonscooked.model.item.Ingredient;

public class CuttingStation extends Station {
    
    private float holdProgress = 0f;
    private static final float CHOP_DURATION = 3.0f;
    private boolean hasPlayedSound = false;
    private boolean isBeingHeld = false;

    public CuttingStation(String id, float x, float y) {
        super(id, x, y, 64, 64); // ← FIX: Add x, y, 64, 64
    }

    @Override
    public void interact(Chef chef) {
        Item heldItem = chef.getInventory();
        Item stationItem = this.getItem();

        // PLACE ingredient untuk di-chop
        if (heldItem instanceof Ingredient && stationItem == null) {
            if (((Ingredient) heldItem).canBeChopped()) {
                this.setItem(heldItem);
                chef.setInventory(null);
                this.holdProgress = 0f;
                Gdx.app.log("CuttingStation", "Placed ingredient for chopping");
            }
        } 
        // TAKE ingredient dari station (hanya kalau tidak lagi di-hold)
        else if (heldItem == null && stationItem != null && !isBeingHeld) {
            chef.setInventory(stationItem);
            this.setItem(null);
            this.holdProgress = 0f;
            Gdx.app.log("CuttingStation", "Took ingredient from station");
        }
    }

    @Override
    public void processHold(Chef chef, float delta) {
        Item stationItem = this.getItem();
        
        if (stationItem instanceof Ingredient && chef.getInventory() == null) {
            Ingredient ing = (Ingredient) stationItem;
            
            if (ing.canBeChopped()) {
                isBeingHeld = true;
                chef.setBusy(true); // LOCK CHEF MOVEMENT
                holdProgress += delta;
                
                if (!hasPlayedSound) {
                    AudioManager.getInstance().playSound("sfx/chop.mp3");
                    hasPlayedSound = true;
                }
                
                if (holdProgress >= CHOP_DURATION) {
                    ing.chop();
                    holdProgress = 0f;
                    hasPlayedSound = false;
                    isBeingHeld = false;
                    chef.setBusy(false); // UNLOCK CHEF
                    Gdx.app.log("CuttingStation", "Ingredient chopped!");
                }
            }
        } else {
            if (isBeingHeld) {
                isBeingHeld = false;
                hasPlayedSound = false;
                chef.setBusy(false); // UNLOCK CHEF kalau release key
            }
        }
    }

    public boolean isActive() {
        return isBeingHeld;
    }

    public float getProgress() {
        return Math.min(1f, holdProgress / CHOP_DURATION);
    }
}