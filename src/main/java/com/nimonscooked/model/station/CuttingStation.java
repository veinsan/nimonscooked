package com.nimonscooked.model.station;

import com.nimonscooked.model.Item;

public class CuttingStation extends Station {
    private Item itemOnStation; // buat naro item

    public CuttingStation(String id) {
        super(id);
    }
    
    public Item getItemOnStation() {
        return itemOnStation;
    }
    
    public void setItemOnStation(Item item) {
        this.itemOnStation = item;
    }
    
    public boolean hasItem() {
        return itemOnStation != null;
    }
}