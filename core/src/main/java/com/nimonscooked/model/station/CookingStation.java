package com.nimonscooked.model.station;

import com.badlogic.gdx.Gdx;
import com.nimonscooked.manager.AudioManager;
import com.nimonscooked.model.entity.Chef;
import com.nimonscooked.model.item.Item;
import com.nimonscooked.model.utensil.CookingDevice;
import com.nimonscooked.model.ingredient.Preparable;

public class CookingStation extends Station {
    
    private boolean isActive = false;

    public CookingStation(String id) {
        super(id);
    }

    @Override
    public void interact(Chef chef) {
        if (chef.isBusy()) return;

        Item heldItem = chef.getInventory();
        Item stationItem = this.getItem();

        if (heldItem instanceof CookingDevice && stationItem == null) {
            placeCookingDevice(chef, (CookingDevice) heldItem);
        } else if (heldItem == null && stationItem instanceof CookingDevice) {
            removeCookingDevice(chef, (CookingDevice) stationItem);
        } else if (heldItem instanceof Preparable && stationItem instanceof CookingDevice) {
            addIngredientToDevice(chef, (Preparable) heldItem, (CookingDevice) stationItem);
        }
    }

    private void placeCookingDevice(Chef chef, CookingDevice device) {
        this.setItem((Item) device);
        chef.setInventory(null);
        device.startCooking();
        isActive = true;
        AudioManager.getInstance().playSound("sfx/catch.mp3");
        Gdx.app.log("CookingStation", "Placed cooking device and started cooking");
    }

    private void removeCookingDevice(Chef chef, CookingDevice device) {
        device.stopCooking();
        chef.setInventory((Item) device);
        this.setItem(null);
        isActive = false;
        Gdx.app.log("CookingStation", "Removed cooking device, cooking stopped");
    }

    private void addIngredientToDevice(Chef chef, Preparable ingredient, CookingDevice device) {
        if (device.canAccept(ingredient)) {
            device.addIngredient(ingredient);
            chef.setInventory(null);
            
            if (!device.isCooking()) {
                device.startCooking();
                isActive = true;
            }
            
            AudioManager.getInstance().playSound("sfx/catch.mp3");
            Gdx.app.log("CookingStation", "Added ingredient to device");
        } else {
            Gdx.app.log("CookingStation", "Device cannot accept this ingredient");
        }
    }

    public boolean isActive() {
        return isActive;
    }

    public void update(float delta) {
        Item stationItem = this.getItem();
        if (stationItem instanceof CookingDevice) {
            CookingDevice device = (CookingDevice) stationItem;
            isActive = device.isCooking();
        } else {
            isActive = false;
        }
    }
}