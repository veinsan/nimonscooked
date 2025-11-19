package com.nimonscooked.model.station;

import com.nimonscooked.model.Item;

public abstract class Station {
    protected final String id;
    protected Item itemOnStation;

    public Station(String id) {
        this.id = id;
        this.itemOnStation = null;
    }

    public String getId() {
        return id;
    }

    public boolean hasItem() {
        return itemOnStation != null;
    }

    public Item getItemOnStation() {
        return itemOnStation;
    }

    public void setItemOnStation(Item item) {
        this.itemOnStation = item;
    }

    public void clearItem() {
        this.itemOnStation = null;
    }
}