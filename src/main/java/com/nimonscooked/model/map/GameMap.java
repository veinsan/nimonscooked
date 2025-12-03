package com.nimonscooked.model.map;

import java.util.ArrayList;
import java.util.List;

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

    public int getRows() { return rows; }
    public int getCols() { return cols; }


    public boolean isWalkable(int row, int col) {
        Tile t = getTile(row, col);
        return t != null && t.isWalkable();
    }

    public List<int[]> getSpawnPositions() {
        List<int[]> result = new ArrayList<>();
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                Tile t = grid[r][c];
                if (t != null && t.isSpawn()) {
                    result.add(new int[]{r, c});
                }
            }
        }
        return result;
    }
}
