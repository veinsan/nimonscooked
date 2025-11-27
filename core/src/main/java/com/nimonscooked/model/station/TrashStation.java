package com.nimonscooked.model.station;

import com.badlogic.gdx.Gdx;
import com.nimonscooked.model.entity.Chef;
import com.nimonscooked.model.item.Item;

public class TrashStation extends Station {
    
    public TrashStation(String id) {
        super(id);
    }

    @Override
    public void interact(Chef chef) {
        Item heldItem = chef.getInventory();
        
        if (heldItem != null) {
            // Hapus item dari tangan chef selamanya
            Gdx.app.log("TrashStation", "Destroyed " + heldItem.getDisplayName());
            chef.setInventory(null);
        } else {
            Gdx.app.log("TrashStation", "Nothing to trash.");
        }
    }
}