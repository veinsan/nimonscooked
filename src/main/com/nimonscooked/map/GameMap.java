package com.nimonscooked.map;

public class GameMap {
    private Tile[][] grid;

    public GameMap(int rows, int cols) {
        grid = new Tile[rows][cols];
    }

    public Tile[][] getGrid() {
        return grid;
    }
}
