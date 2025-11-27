package com.nimonscooked.model.station;

import com.nimonscooked.model.entity.Chef; // IMPORT SUDAH BENAR
import com.nimonscooked.model.item.Item;

public abstract class Station {
    protected String id;
    protected Item item;

    public Station(String id) {
        this.id = id;
    }

    // Method wajib diimplementasi subclass
    public abstract void interact(Chef chef);

    public boolean hasItem() { return item != null; }
    public Item getItem() { return item; }
    public void setItem(Item item) { this.item = item; }
    public String getId() { return id; }
}
