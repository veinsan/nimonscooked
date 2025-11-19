package com.nimonscooked.model.map;

public class GameMap {
    private Tile[][] grid;
    private int rows;
    private int cols;

    public GameMap(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;
        this.grid = new Tile[rows][cols];
        initializeMap();
    }
    
    private void initializeMap() {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                grid[i][j] = new Tile(TileType.FLOOR, '.');
            }
        }
    }

    public Tile[][] getGrid() {
        return grid;
    }
    
    public Tile getTile(int row, int col) {
        if (isValidPosition(row, col)) {
            return grid[row][col];
        }
        return null;
    }
    
    public boolean isValidPosition(int row, int col) {
        return row >= 0 && row < rows && col >= 0 && col < cols;
    }
    
    public int getRows() { return rows; }
    public int getCol() { return cols; }
}