package com.nimonscooked.model.map;

public class Tile {
    public enum TileType { FLOOR, WALL, STATION, EMPTY }
    private TileType type;
    private char symbol;

    public Tile(TileType type, char symbol) {
        this.type = type;
        this.symbol = symbol;
    }
    public TileType getType() { return type; }
    public boolean isWalkable() { return type == TileType.FLOOR || type == TileType.EMPTY; }
    public char getSymbol() { return symbol; }
}
