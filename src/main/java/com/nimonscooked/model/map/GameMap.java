package com.nimonscooked.model.map;

public class GameMap {
    private final Tile[][] grid;
    private final int rows;
    private final int cols;

    public GameMap(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;
        this.grid = new Tile[rows][cols];
    }

    public Tile getTile(int row, int col) {
        if (isValid(row, col)) {
            return grid[row][col];
        }
        return null;
    }

    public void setTile(int row, int col, Tile tile) {
        if (isValid(row, col)) {
            grid[row][col] = tile;
        }
    }

    public boolean isValid(int row, int col) {
        return row >= 0 && row < rows && col >= 0 && col < cols;
    }

    public int getRows() {
        return rows;
    }

    public int getCols() {
        return cols;
    }

    public void render() {
        for (int row = 0; row < rows; row++) {
            StringBuilder lineBuilder = new StringBuilder();
            for (int col = 0; col < cols; col++) {
                Tile tile = grid[row][col];
                if (tile != null) {
                    lineBuilder.append(tile.getSymbol());
                } else {
                    lineBuilder.append('?');
                }
            }
            System.out.println(lineBuilder.toString());
        }
    }
}