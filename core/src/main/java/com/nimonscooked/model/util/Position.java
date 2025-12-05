package com.nimonscooked.model.util;

public class Position {
    public int row;
    public int col;

    public Position(int row, int col) {
        this.row = row;
        this.col = col;
    }

    public Position(Position other) {
        this.row = other.row;
        this.col = other.col;
    }

    public void set(int row, int col) {
        this.row = row;
        this.col = col;
    }

    public void set(Position other) {
        this.row = other.row;
        this.col = other.col;
    }

    public Position copy() {
        return new Position(this.row, this.col);
    }

    public int manhattanDistance(Position other) {
        return Math.abs(this.row - other.row) + Math.abs(this.col - other.col);
    }

    public boolean isAdjacent(Position other) {
        return manhattanDistance(other) == 1;
    }

    public boolean isDiagonal(Position other) {
        int rowDiff = Math.abs(this.row - other.row);
        int colDiff = Math.abs(this.col - other.col);
        return rowDiff == 1 && colDiff == 1;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Position)) return false;
        
        Position other = (Position) obj;
        return this.row == other.row && this.col == other.col;
    }

    @Override
    public int hashCode() {
        return row * 31 + col;
    }

    @Override
    public String toString() {
        return "Position(" + row + ", " + col + ")";
    }
}