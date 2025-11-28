package com.nimonscooked.model.util;

public class Position {
    public int row;
    public int col;

    public Position(int row, int col) {
        this.row = row;
        this.col = col;
    }

    public void set(int row, int col) {
        this.row = row;
        this.col = col;
    }
}