package com.nimonscooked.model.station;

import com.badlogic.gdx.Gdx;
import com.nimonscooked.manager.AudioManager;
import com.nimonscooked.manager.GameManager;
import com.nimonscooked.model.entity.Chef;
import com.nimonscooked.model.item.Item;
import com.nimonscooked.model.utensil.Plate;

public class WashingStation extends Station {

    private float washProgress = 0f;
    private static final float WASH_DURATION = 3.0f;
    private boolean hasSoundPlayed = false;
    private boolean isWashing = false;
    private Chef assignedChef = null;

    public WashingStation(String id, float x, float y) {
        super(id, x, y, 64, 64);
    }

    @Override
    public void interact(Chef chef) {
        Item heldItem = chef.getInventory();
        Item stationItem = this.getItem();

        if (heldItem instanceof Plate && stationItem == null) {
            Plate plate = (Plate) heldItem;
            if (!plate.isClean()) {
                this.setItem(plate);
                chef.setInventory(null);
                this.washProgress = 0f;
                startAutoWash(chef);
                Gdx.app.log("WashingStation", "Auto-wash started!");
            }
        } 
        else if (heldItem == null && stationItem instanceof Plate) {
            Plate plate = (Plate) stationItem;
            
            if (plate.isClean() && !isWashing) {
                chef.setInventory(plate);
                this.setItem(null);
                this.washProgress = 0f;
                Gdx.app.log("WashingStation", "Took clean plate");
            }
        }
    }

    private void startAutoWash(Chef chef) {
        this.isWashing = true;
        this.assignedChef = chef;
        this.washProgress = 0f;
        this.hasSoundPlayed = false;
        
        chef.setBusy(true);

        Runnable washingTask = new Runnable() {
            @Override
            public void run() {
                try {
                    if (!hasSoundPlayed) {
                        AudioManager.getInstance().playSound("sfx/wash.mp3");
                        hasSoundPlayed = true;
                    }

                    long startTime = System.currentTimeMillis();
                    long duration = (long)(WASH_DURATION * 1000);

                    while (System.currentTimeMillis() - startTime < duration) {
                        if (Thread.interrupted()) {
                            return;
                        }
                        washProgress = (System.currentTimeMillis() - startTime) / (float)duration;
                        Thread.sleep(16);
                    }

                    washProgress = 1f;

                    Gdx.app.postRunnable(new Runnable() {
                        @Override
                        public void run() {
                            Item stationItem = getItem();
                            if (stationItem instanceof Plate) {
                                ((Plate) stationItem).setClean(true);
                                Gdx.app.log("WashingStation", "Plate is now clean!");
                            }
                            
                            isWashing = false;
                            washProgress = 0f;
                            hasSoundPlayed = false;

                            if (assignedChef != null) {
                                assignedChef.setBusy(false);
                                assignedChef = null;
                            }
                        }
                    });

                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        };

        GameManager.getThreadPool().execute(washingTask);
    }

    @Override
    public void processHold(Chef chef, float delta) {
    }

    public boolean isActive() {
        return isWashing;
    }

    public float getProgress() {
        return Math.min(1f, washProgress);
    }
}