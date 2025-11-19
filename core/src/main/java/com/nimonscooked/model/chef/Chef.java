package com.nimonscooked.model.chef;

import com.nimonscooked.map.GameMap;
import com.nimonscooked.map.Tile;
import com.nimonscooked.map.TileType;
import com.nimonscooked.model.Item;

public class Chef {
    private final String id;
    private final String name;
    private Position position;
    private Direction direction;
    private Item inventory;

    public Chef(String id, String name, Position spawnPosition) {
        this.id = id;
        this.name = name;
        this.position = spawnPosition != null ? spawnPosition : new Position(0, 0);
        this.direction = Direction.DOWN;
        this.inventory = null;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Position getPosition() {
        return position;
    }

    public Direction getDirection() {
        return direction;
    }

    public Item getInventory() {
        return inventory;
    }

    public Item getHeldItem() {
        return inventory;
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    public void setPosition(Position newPosition) {
        if (newPosition != null) {
            this.position = newPosition;
        }
    }

    public void move(Direction dir, GameMap map) {
        this.direction = dir;
        int currentRow = position.getRow();
        int currentCol = position.getCol();

        int targetRow = currentRow;
        int targetCol = currentCol;

        switch (dir) {
            case UP:
                targetRow--;
                break;
            case DOWN:
                targetRow++;
                break;
            case LEFT:
                targetCol--;
                break;
            case RIGHT:
                targetCol++;
                break;
        }

        if (!map.isValid(targetRow, targetCol)) {
            return;
        }

        Tile targetTile = map.getTile(targetRow, targetCol);
        if (targetTile.getType() == TileType.WALL || targetTile.getType() == TileType.STATION) {
            return;
        }

        position.set(targetRow, targetCol);
    }

    public boolean hasItem() {
        return inventory != null;
    }

    public void setInventory(Item item) {
        this.inventory = item;
    }

    public Item removeInventory() {
        Item removedItem = this.inventory;
        this.inventory = null;
        return removedItem;
    }

    public int getFrontRow() {
        int currentRow = position.getRow();
        switch (direction) {
            case UP:
                return currentRow - 1;
            case DOWN:
                return currentRow + 1;
            default:
                return currentRow;
        }
    }

    public int getFrontCol() {
        int currentCol = position.getCol();
        switch (direction) {
            case LEFT:
                return currentCol - 1;
            case RIGHT:
                return currentCol + 1;
            default:
                return currentCol;
        }
    }

    public Tile getFrontTile(GameMap map) {
        int frontRow = getFrontRow();
        int frontCol = getFrontCol();

        if (!map.isValid(frontRow, frontCol)) {
            return null;
        }

        return map.getTile(frontRow, frontCol);
    }
}