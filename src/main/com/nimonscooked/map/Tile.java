package com.nimonscooked.map;

public class Tile {
    private TileType type;
    private char symbol;

    public Tile(TileType type, char symbol) {
        this.type = type;
        this.symbol = symbol;
    }

    public TileType getType() { return type; }
    public char getSymbol() { return symbol; }
}
