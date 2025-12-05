package com.nimonscooked.model.station;

import com.badlogic.gdx.Gdx;
import com.nimonscooked.model.entity.Chef;
import com.nimonscooked.model.item.Item;
import com.nimonscooked.model.utensil.Plate;
import com.nimonscooked.model.thread.InteractionThread;

public class WashingStation extends Station {

    public WashingStation(String id) {
        super(id);
    }

    @Override
    public void interact(Chef chef) {
        if (chef.isBusy()) return;

        Item heldItem = chef.getInventory();
        Item stationItem = this.getItem();

        if (heldItem instanceof Plate && stationItem == null) {
            Plate plate = (Plate) heldItem;
            if (!plate.isClean()) {
                this.setItem(plate);
                chef.setInventory(null);
                Gdx.app.log("WashingStation", "Placed dirty plate.");
            }
        } else if (heldItem == null && stationItem instanceof Plate) {
            Plate plate = (Plate) stationItem;
            if (plate.isClean()) {
                chef.setInventory(plate);
                this.setItem(null);
                Gdx.app.log("WashingStation", "Took clean plate.");
            } else {
                InteractionThread washTask = new InteractionThread(chef, 3.0f) {
                    @Override
                    public void onComplete() {
                        plate.setClean(true);
                        Gdx.app.log("WashingStation", "Plate is now CLEAN!");
                    }
                };

                chef.isChopping = true;
                chef.setCurrentInteraction(washTask);
                washTask.start();
                Gdx.app.log("WashingStation", "Washing started (3s)...");
            }
        }
    }
}