package com.nimonscooked.model.map;

import com.nimonscooked.manager.MapManager;
import com.nimonscooked.model.entity.Chef;

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

    // --- TAMBAHAN UNTUK FITUR DASH (BONUS) ---
    public boolean isOccupiedByChef(int col, int row) {
        // Cek ke MapManager apakah ada chef di koordinat ini
        if (MapManager.getInstance().chefs == null) return false;

        for (Chef c : MapManager.getInstance().chefs) {
            if (c.position.col == col && c.position.row == row) {
                return true;
            }
        }
        return false;
    }
}
