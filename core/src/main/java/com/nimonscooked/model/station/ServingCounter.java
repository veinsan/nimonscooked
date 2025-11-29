package com.nimonscooked.model.station;

import com.badlogic.gdx.Gdx;
import com.nimonscooked.manager.GameManager;
import com.nimonscooked.manager.MapManager;
import com.nimonscooked.model.entity.Chef;
import com.nimonscooked.model.item.Item;
import com.nimonscooked.model.utensil.Plate;
import com.nimonscooked.model.dish.Dish;

import java.util.concurrent.TimeUnit;

public class ServingCounter extends Station {
    public ServingCounter(String id) {
        super(id);
    }

    @Override
    public void interact(Chef chef) {
        Item heldItem = chef.getInventory();
        if (heldItem instanceof Plate) {
            Plate plate = (Plate) heldItem;
            if (!plate.isEmpty() && plate.getContainedDish() != null) {
                Dish dish = plate.getContainedDish();
                GameManager.getInstance().orderManager.submitOrder(dish);
                chef.setInventory(null);
                startPlateReturnTimer();
            }
        }
    }

    private void startPlateReturnTimer() {
        GameManager.getThreadPool().execute(() -> {
            try {
                TimeUnit.SECONDS.sleep(10);
                Gdx.app.postRunnable(() -> {
                    for (Station s : MapManager.getInstance().getAllStations()) {
                        if (s instanceof PlateStorage) {
                            ((PlateStorage) s).returnDirtyPlate();
                            break;
                        }
                    }
                });
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
    }
}