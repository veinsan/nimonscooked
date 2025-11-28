package com.nimonscooked.model.map;

import com.nimonscooked.model.station.Station;

public class Tile {
    public enum TileType { FLOOR, WALL, STATION, EMPTY }

    private TileType type;
    private char symbol;
    private Station station;

    public Tile(TileType type, char symbol) {
        this.type = type;
        this.symbol = symbol;
        this.station = null;
    }

    public TileType getType() {
        return type;
    }

    public boolean isWalkable() {
        return type == TileType.FLOOR || type == TileType.EMPTY;
    }

    public char getSymbol() {
        return symbol;
    }

    public void setStation(Station station) {
        this.station = station;
    }

    public Station getStation() {
        return station;
    }
}