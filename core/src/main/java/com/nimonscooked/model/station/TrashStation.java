package com.nimonscooked.model.station;

import com.badlogic.gdx.Gdx;
import com.nimonscooked.model.entity.Chef;
import com.nimonscooked.model.item.Item;

public class TrashStation extends Station {

    public TrashStation(String id, float x, float y) {
        super(id, x, y, 64, 64);
    }

    @Override
    public void interact(Chef chef) {
        Item heldItem = chef.getInventory();

        if (heldItem != null) {
            Gdx.app.log("TrashStation", "Destroyed " + heldItem.getDisplayName());
            chef.setInventory(null);
        } else {
            Gdx.app.log("TrashStation", "Nothing to trash.");
        }
    }
}