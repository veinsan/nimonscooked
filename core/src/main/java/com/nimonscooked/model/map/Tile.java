package com.nimonscooked.map;

import com.nimonscooked.model.Item;
import com.nimonscooked.model.station.Station;

public class Tile {
    private TileType type;
    private char symbol;
    private Station station;
    private Item item;

    public Tile(TileType type, char symbol) {
        this.type = type;
        this.symbol = symbol;
        this.station = null;
        this.item = null;
    }

    public TileType getType() {
        return type;
    }

    public char getSymbol() {
        return symbol;
    }

    public void setType(TileType type) {
        this.type = type;
    }

    public Station getStation() {
        return station;
    }

    public void setStation(Station station) {
        this.station = station;
        if (station != null) {
            this.type = TileType.STATION;
        }
    }

    public boolean hasStation() {
        return station != null;
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public boolean hasItem() {
        return item != null;
    }
}