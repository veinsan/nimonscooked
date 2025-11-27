package com.nimonscooked.model.entity; // Package ini HARUS sesuai folder

import com.badlogic.gdx.math.Vector2;
import com.nimonscooked.model.item.Item;
import com.nimonscooked.model.map.GridMap;
import com.nimonscooked.model.map.Tile;
import com.nimonscooked.model.util.Position;

public class Chef {
    public enum Direction { UP, DOWN, LEFT, RIGHT } // Enum harus public

    public Position position;
    public Vector2 visualPos;
    public Direction direction = Direction.DOWN;

    public float stateTime = 0f;
    public boolean isMoving = false;
    public boolean isChopping = false;

    private Item inventory;

    public Chef(int startCol, int startRow) {
        this.position = new Position(startRow, startCol);
        this.visualPos = new Vector2(startCol, startRow);
    }

    public void move(Direction dir, GridMap map) {
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

        // Pastikan Tile punya isWalkable()
        if (targetTile == null || !targetTile.isWalkable()) return;

        position.set(targetRow, targetCol);
        this.direction = dir;
    }

    public float getX() { return visualPos.x; }
    public float getY() { return visualPos.y; }
    public void setInventory(Item item) { this.inventory = item; }
    public Item getInventory() { return inventory; }
}
