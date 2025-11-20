package com.nimonscooked.model.map;

public class GridMap {
    private Tile[][] grid;
    private int rows, cols;

    public GridMap(int width, int height) {
        this.cols = width;
        this.rows = height;
        this.grid = new Tile[cols][rows];
    }
    public void setTile(int col, int row, Tile tile) {
        if (isValid(col, row)) grid[col][row] = tile;
    }
    public Tile getTile(int col, int row) {
        if (isValid(col, row)) return grid[col][row];
        return null;
    }
    public boolean isValid(int col, int row) {
        return col >= 0 && col < cols && row >= 0 && row < rows;
    }
    public int getWidth() { return cols; }
    public int getHeight() { return rows; }
}
