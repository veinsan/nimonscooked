package com.nimonscooked.model.station;

import com.nimonscooked.model.entity.Chef;
import com.nimonscooked.model.item.Item;

public abstract class Station {
    protected String id;
    protected Item item;

    public Station(String id) {
        this.id = id;
    }

    public abstract void interact(Chef chef);
    
    public void processHold(Chef chef, float delta) {
    }

    public boolean hasItem() {
        return item != null;
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public String getId() {
        return id;
    }
}