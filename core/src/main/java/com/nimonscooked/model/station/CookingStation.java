package com.nimonscooked.model.station;

import com.badlogic.gdx.Gdx;
import com.nimonscooked.model.entity.Chef;
import com.nimonscooked.model.item.Item;
import com.nimonscooked.model.utensil.CookingDevice;

public class CookingStation extends Station {

    public enum StoveType { LEFT, RIGHT }
    private final StoveType stoveType;

    public CookingStation(String id, StoveType type) {
        super(id);
        this.stoveType = type;
    }

    @Override
    public void interact(Chef chef) {
        Item held = chef.getInventory();
        Item stored = this.getItem();

        if (held != null && stored == null) {
            this.setItem(held);
            chef.setInventory(null);
        } else if (held == null && stored != null) {
            this.setItem(null);
            chef.setInventory(stored);
        }
    }

    @Override
    public void processHold(Chef chef, float delta) {
        Item item = this.getItem();
        if (item instanceof CookingDevice) {
            CookingDevice device = (CookingDevice) item;
            if (!device.isCooking()) {
                device.startCooking();
                Gdx.app.log("CookingStation", "Started cooking!");
            }
        }
    }

    public boolean isActive() {
        Item item = this.getItem();
        if (item instanceof CookingDevice) {
            return ((CookingDevice) item).isCooking();
        }
        return false;
    }
    
    public float getProgress() {
        Item item = this.getItem();
        if (item instanceof CookingDevice) {
             return ((CookingDevice) item).getProgress();
        }
        return 0f;
    }

    public StoveType getStoveType() {
        return stoveType;
    }
}