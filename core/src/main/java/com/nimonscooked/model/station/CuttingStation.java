package com.nimonscooked.model.station;

import com.badlogic.gdx.Gdx;
import com.nimonscooked.manager.AudioManager;
import com.nimonscooked.model.entity.Chef;
import com.nimonscooked.model.item.Item;
import com.nimonscooked.model.item.Ingredient;
import com.nimonscooked.model.thread.InteractionThread;

public class CuttingStation extends Station {
    public CuttingStation(String id) { super(id); }

    @Override
    public void interact(Chef chef) {
        if (chef.isBusy()) return;

        Item heldItem = chef.getInventory();
        Item stationItem = this.getItem();

        if (heldItem instanceof Ingredient && stationItem == null) {
            // Taruh
            if (((Ingredient) heldItem).canBeChopped()) {
                this.setItem(heldItem);
                chef.setInventory(null);
            }
        } else if (heldItem == null && stationItem != null) {
            // Ambil
            chef.setInventory(stationItem);
            this.setItem(null);
        } else if (heldItem == null && stationItem instanceof Ingredient) {
            // Potong (Concurrency: 3 Detik)
            final Ingredient ing = (Ingredient) stationItem;
            if (ing.canBeChopped()) {
                InteractionThread cutTask = new InteractionThread(chef, 3.0f) {
                    @Override
                    public void onComplete() {
                        ing.chop();
                        AudioManager.getInstance().playSound("sfx/chop.wav");
                    }
                };
                chef.isChopping = true;
                chef.setCurrentInteraction(cutTask);
                cutTask.start();
            }
        }
    }
}
