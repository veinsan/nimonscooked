package com.nimonscooked.model.station;

import com.badlogic.gdx.Gdx;
import com.nimonscooked.manager.AudioManager;
import com.nimonscooked.manager.GameManager;
import com.nimonscooked.model.entity.Chef;
import com.nimonscooked.model.item.Item;
import com.nimonscooked.model.item.Ingredient;

public class CuttingStation extends Station {
    
    private float chopProgress = 0f;
    private static final float CHOP_DURATION = 3.0f;
    private boolean hasSoundPlayed = false;
    private boolean isChopping = false;
    private Chef assignedChef = null;

    public CuttingStation(String id, float x, float y) {
        super(id, x, y, 64, 64);
    }

    @Override
    public void interact(Chef chef) {
        Item heldItem = chef.getInventory();
        Item stationItem = this.getItem();

        if (heldItem instanceof Ingredient && stationItem == null) {
            if (((Ingredient) heldItem).canBeChopped()) {
                this.setItem(heldItem);
                chef.setInventory(null);
                this.chopProgress = 0f;
                startAutoChop(chef);
                Gdx.app.log("CuttingStation", "Auto-chop started!");
            }
        } 
        else if (heldItem == null && stationItem != null && !isChopping) {
            chef.setInventory(stationItem);
            this.setItem(null);
            this.chopProgress = 0f;
            Gdx.app.log("CuttingStation", "Took ingredient from station");
        }
    }

    private void startAutoChop(Chef chef) {
        this.isChopping = true;
        this.assignedChef = chef;
        this.chopProgress = 0f;
        this.hasSoundPlayed = false;
        
        chef.setBusy(true);

        Runnable choppingTask = new Runnable() {
            @Override
            public void run() {
                try {
                    if (!hasSoundPlayed) {
                        AudioManager.getInstance().playSound("sfx/chop.mp3");
                        hasSoundPlayed = true;
                    }

                    long startTime = System.currentTimeMillis();
                    long duration = (long)(CHOP_DURATION * 1000);

                    while (System.currentTimeMillis() - startTime < duration) {
                        if (Thread.interrupted()) {
                            return;
                        }
                        chopProgress = (System.currentTimeMillis() - startTime) / (float)duration;
                        Thread.sleep(16);
                    }

                    chopProgress = 1f;

                    Gdx.app.postRunnable(new Runnable() {
                        @Override
                        public void run() {
                            Item stationItem = getItem();
                            if (stationItem instanceof Ingredient) {
                                ((Ingredient) stationItem).chop();
                                Gdx.app.log("CuttingStation", "Ingredient chopped!");
                            }
                            
                            isChopping = false;
                            chopProgress = 0f;
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

        GameManager.getThreadPool().execute(choppingTask);
    }

    @Override
    public void processHold(Chef chef, float delta) {
    }

    public boolean isActive() {
        return isChopping;
    }

    public float getProgress() {
        return Math.min(1f, chopProgress);
    }
}