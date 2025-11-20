package com.nimonscooked.model.entity;

import com.badlogic.gdx.math.Vector2;
import com.nimonscooked.model.map.GridMap;
import com.nimonscooked.model.map.Tile;
import com.nimonscooked.model.util.Position;

public class Chef {
    public enum Direction { UP, DOWN, LEFT, RIGHT }
    public Position position;
    public Vector2 visualPos;
    public Direction direction = Direction.DOWN;

    // --- STATE ANIMASI ---
    public float stateTime = 0f;
    public boolean isMoving = false;
    public boolean isChopping = false; // TAMBAHAN BARU
    // ---------------------

    public Chef(int startCol, int startRow) {
        this.position = new Position(startRow, startCol);
        this.visualPos = new Vector2(startCol, startRow);
    }

    public void move(Direction dir, GridMap map) {
        // Reset chopping kalau bergerak
        isChopping = false;

        int targetCol = position.col;
        int targetRow = position.row;

        switch (dir) {
            case UP: targetRow++; break;
            case DOWN: targetRow--; break;
            case LEFT: targetCol--; break;
            case RIGHT: targetCol++; break;
        }

        if (!map.isValid(targetCol, targetRow)) return;
        Tile targetTile = map.getTile(targetCol, targetRow);
        if (!targetTile.isWalkable()) return;

        position.set(targetRow, targetCol);
        this.direction = dir;
    }

    public float getX() { return visualPos.x; }
    public float getY() { return visualPos.y; }
}
