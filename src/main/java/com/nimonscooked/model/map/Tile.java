package com.nimonscooked.model.map;

import com.nimonscooked.model.station.Station;
import com.nimonscooked.model.Item;

public class Tile {
    private TileType type;
    private char symbol;
    private Station station; 
    private Item item;      

    public Tile(TileType type, char symbol) {
        this.type = type;
        this.symbol = symbol;
    }

    public TileType getType() { return type; }
    public char getSymbol() { return symbol; }

    public Station getStation() { return station; }
    public void setStation(Station station) { this.station = station; }

    public Item getItem() { return item; }
    public void setItem(Item item) { this.item = item; }

    public boolean hasItem() { return item != null; }
    public boolean hasStation() { return station != null; }


    public boolean isWalkable() {
        return type == TileType.FLOOR || type == TileType.SPAWN;
    }

    public boolean isSpawn() {
        return type == TileType.SPAWN;
    }
}
