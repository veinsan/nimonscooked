package com.nimonscooked.model.station;

import com.badlogic.gdx.Gdx;
import com.nimonscooked.manager.AudioManager;
import com.nimonscooked.manager.GameManager;
import com.nimonscooked.manager.MapManager;
import com.nimonscooked.model.entity.Chef;
import com.nimonscooked.model.item.Item;
import com.nimonscooked.model.utensil.Plate;
import com.nimonscooked.model.dish.Dish;

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
                int score = GameManager.getInstance().orderManager.submitOrder(dish);

                if (score > 0) AudioManager.getInstance().playSound("sfx/delivery_success.wav");
                else AudioManager.getInstance().playSound("sfx/delivery_fail.wav");

                chef.setInventory(null);
                startPlateReturnTimer();
            }
        }
    }

    private void startPlateReturnTimer() {
        new Thread(() -> {
            try {
                Thread.sleep(10000);
                for (Station s : MapManager.getInstance().getAllStations()) {
                    if (s instanceof PlateStorage) {
                        ((PlateStorage) s).returnDirtyPlate();
                        break;
                    }
                }
            } catch (Exception ignored) {}
        }).start();
    }
}