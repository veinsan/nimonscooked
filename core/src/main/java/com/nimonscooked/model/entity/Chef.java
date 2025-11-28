package com.nimonscooked.model.entity;

import com.badlogic.gdx.math.Vector2;
import com.nimonscooked.model.item.Item;
import com.nimonscooked.model.item.Ingredient;
import com.nimonscooked.model.map.GridMap;
import com.nimonscooked.model.map.Tile;
import com.nimonscooked.model.thread.InteractionThread;
import com.nimonscooked.model.util.Position;
import java.util.List;

public class Chef {
    public enum Direction { UP, DOWN, LEFT, RIGHT }

    public Position position;
    public Vector2 visualPos;
    public Direction direction = Direction.DOWN;

    public float stateTime = 0f;
    public boolean isMoving = false;
    public boolean isChopping = false;

    private Item inventory;
    private boolean isBusy = false;
    private InteractionThread currentInteraction;

    private float dashCooldownTimer = 0f;
    private static final float DASH_COOLDOWN = 2.0f;

    public Chef(int startCol, int startRow) {
        this.position = new Position(startRow, startCol);
        this.visualPos = new Vector2(startCol, startRow);
    }

    public void update(float delta) {
        if (dashCooldownTimer > 0) dashCooldownTimer -= delta;
    }

    public void move(Direction dir, GridMap map) {
        if (isBusy) return;

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
        if (targetTile == null || !targetTile.isWalkable()) return;
        if (map.isOccupiedByChef(targetCol, targetRow)) return;

        position.set(targetRow, targetCol);
        this.direction = dir;
    }

    public void dash(Direction dir, GridMap map) {
        if (isBusy || dashCooldownTimer > 0) return;

        this.direction = dir;
        int dCol = 0, dRow = 0;

        switch (dir) {
            case UP: dRow = 1; break;
            case DOWN: dRow = -1; break;
            case LEFT: dCol = -1; break;
            case RIGHT: dCol = 1; break;
        }

        int targetCol = position.col;
        int targetRow = position.row;

        for (int i = 1; i <= 3; i++) {
            int checkCol = position.col + (dCol * i);
            int checkRow = position.row + (dRow * i);

            if (!map.isValid(checkCol, checkRow)) break;

            Tile t = map.getTile(checkCol, checkRow);
            if (t != null && t.isWalkable() && !map.isOccupiedByChef(checkCol, checkRow)) {
                targetCol = checkCol;
                targetRow = checkRow;
            } else {
                break;
            }
        }

        if (targetCol != position.col || targetRow != position.row) {
            position.set(targetRow, targetCol);
            dashCooldownTimer = DASH_COOLDOWN;
        }
    }

    public void throwItem(GridMap map, List<Chef> allChefs) {
        if (inventory == null || !(inventory instanceof Ingredient)) return;

        Ingredient ing = (Ingredient) inventory;
        if (ing.getState() != Ingredient.State.RAW && ing.getState() != Ingredient.State.CHOPPED) return;

        int dCol = 0, dRow = 0;
        switch (direction) {
            case UP: dRow = 1; break;
            case DOWN: dRow = -1; break;
            case LEFT: dCol = -1; break;
            case RIGHT: dCol = 1; break;
        }

        int targetCol = position.col;
        int targetRow = position.row;
        boolean caught = false;
        Chef catcher = null;

        for (int i = 1; i <= 4; i++) {
            int checkCol = position.col + (dCol * i);
            int checkRow = position.row + (dRow * i);

            if (!map.isValid(checkCol, checkRow)) break;

            Tile t = map.getTile(checkCol, checkRow);
            if (t != null && !t.isWalkable()) {
                if (t.getStation() != null && !t.getStation().hasItem()) {
                    t.getStation().setItem(inventory);
                    setInventory(null);
                    return;
                }
                break;
            }

            for (Chef c : allChefs) {
                if (c != this && c.position.col == checkCol && c.position.row == checkRow) {
                    if (c.getInventory() == null) {
                        catcher = c;
                        caught = true;
                    }
                    break;
                }
            }

            if (caught) break;
            targetCol = checkCol;
            targetRow = checkRow;
        }

        Item thrownItem = inventory;
        setInventory(null);

        if (caught && catcher != null) {
            catcher.setInventory(thrownItem);
        }
    }

    public boolean canDash() {
        return dashCooldownTimer <= 0 && !isBusy;
    }

    public float getDashCooldown() {
        return Math.max(0, dashCooldownTimer);
    }

    public boolean isBusy() {
        return isBusy;
    }

    public void setBusy(boolean busy) {
        this.isBusy = busy;
    }

    public void setCurrentInteraction(InteractionThread thread) {
        this.currentInteraction = thread;
    }

    public InteractionThread getCurrentInteraction() {
        return currentInteraction;
    }

    public void interruptAction() {
        if (currentInteraction != null && currentInteraction.isAlive()) {
            currentInteraction.stopInteraction();
        }
    }

    public float getX() {
        return visualPos.x;
    }

    public float getY() {
        return visualPos.y;
    }

    public void setInventory(Item item) {
        this.inventory = item;
    }

    public Item getInventory() {
        return inventory;
    }
}