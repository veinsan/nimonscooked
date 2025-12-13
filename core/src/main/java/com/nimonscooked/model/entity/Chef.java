package com.nimonscooked.model.entity;

import java.util.List;

import com.badlogic.gdx.math.Vector2;
import com.nimonscooked.model.item.Item;
import com.nimonscooked.model.map.GridMap;
import com.nimonscooked.model.map.Tile;
import com.nimonscooked.model.thread.InteractionThread;
import com.nimonscooked.model.util.Position;

public class Chef {
    public enum Direction { UP, DOWN, LEFT, RIGHT }
    public enum Type { CHEF_A, CHEF_B }
    
    private final Type type;
    public Position position;
    public Vector2 visualPos;
    public Direction direction = Direction.DOWN;

    public float stateTime = 0f;
    public boolean isMoving = false;

    private Item inventory;
    private boolean isBusy = false;
    private InteractionThread currentInteraction;
    
    private float dashCooldownTimer = 0f;
    private static final float DASH_COOLDOWN = 2.0f;
    
    private static final float MOVE_SPEED = 3.5f;
    
    // PERBAIKAN: Collision box yang lebih konsisten
    private static final float COLLISION_RADIUS = 0.35f; // Radius dari center chef
    private static final float COLLISION_OFFSET_Y = -0.15f; // Offset ke bawah untuk feet

    public Chef(int startCol, int startRow, Type type) {
        this.position = new Position(startRow, startCol);
        this.visualPos = new Vector2(startCol, startRow);
        this.type = type;
    }
    
    public Type getType() {
        return type;
    }

    public void update(float delta) {
        if (dashCooldownTimer > 0) {
            dashCooldownTimer -= delta;
        }
        
        stateTime += delta;
    }

    public void move(Direction dir, GridMap map, float delta) {
        if (isBusy) return;

        this.direction = dir;
        this.isMoving = true;

        float moveAmount = MOVE_SPEED * delta;
        float newX = visualPos.x;
        float newY = visualPos.y;

        switch (dir) {
            case UP: newY += moveAmount; break;
            case DOWN: newY -= moveAmount; break;
            case LEFT: newX -= moveAmount; break;
            case RIGHT: newX += moveAmount; break;
        }

        if (canMoveTo(newX, newY, map)) {
            visualPos.x = newX;
            visualPos.y = newY;
            
            position.col = Math.round(visualPos.x);
            position.row = Math.round(visualPos.y);
        }
    }

    /**
     * PERBAIKAN UTAMA: Collision detection yang lebih konsisten
     * Menggunakan circular collision box dengan 4 test points
     */
    private boolean canMoveTo(float x, float y, GridMap map) {
        // Center point dengan offset untuk feet
        float centerX = x;
        float centerY = y + COLLISION_OFFSET_Y;
        
        // Test 4 points di sekitar collision circle
        // Top, Bottom, Left, Right dari collision radius
        float[][] testPoints = {
            {centerX, centerY + COLLISION_RADIUS},  // Top
            {centerX, centerY - COLLISION_RADIUS},  // Bottom
            {centerX - COLLISION_RADIUS, centerY},  // Left
            {centerX + COLLISION_RADIUS, centerY}   // Right
        };
        
        // Cek semua test points
        for (float[] point : testPoints) {
            int col = Math.round(point[0]);
            int row = Math.round(point[1]);
            
            if (!isTileWalkable(col, row, map)) {
                return false;
            }
        }
        
        return true;
    }

    private boolean isTileWalkable(int col, int row, GridMap map) {
        if (!map.isValid(col, row)) {
            return false;
        }

        Tile tile = map.getTile(col, row);
        
        if (tile == null) {
            return false;
        }

        if (!tile.isWalkable()) {
            return false;
        }

        if (tile.hasStation()) {
            return false;
        }

        return true;
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
        
        // Test setiap tile dalam dash range
        for(int i = 1; i <= 3; i++) { 
            int checkCol = position.col + (dCol * i);
            int checkRow = position.row + (dRow * i);
            
            if(map.isValid(checkCol, checkRow)) {
                Tile t = map.getTile(checkCol, checkRow);
                if(t != null && t.isWalkable() && !t.hasStation()) {
                    targetCol = checkCol;
                    targetRow = checkRow;
                } else {
                    break; // Stop jika ketemu obstacle
                }
            } else {
                break; // Stop jika keluar map
            }
        }
        
        // Hanya dash jika ada perubahan posisi
        if(targetCol != position.col || targetRow != position.row) {
            position.set(targetRow, targetCol);
            visualPos.set(targetCol, targetRow);
            dashCooldownTimer = DASH_COOLDOWN;
        }
    }

    public void throwItem(GridMap map, List<Chef> allChefs) {
        if (inventory == null) return;
        
        int dCol = 0, dRow = 0;
        switch (direction) {
            case UP: dRow = 1; break;
            case DOWN: dRow = -1; break;
            case LEFT: dCol = -1; break;
            case RIGHT: dCol = 1; break;
        }

        boolean caught = false;
        Chef catcher = null;

        for(int i = 1; i <= 4; i++) { 
            int checkCol = position.col + (dCol * i);
            int checkRow = position.row + (dRow * i);
            
            if(!map.isValid(checkCol, checkRow)) break;
            
            Tile t = map.getTile(checkCol, checkRow);
            if(t != null && !t.isWalkable()) {
                if(t.getStation() != null && !t.getStation().hasItem()) {
                    t.getStation().setItem(inventory);
                    setInventory(null);
                    return;
                }
                break;
            }
            
            for(Chef c : allChefs) {
                if(c != this && c.position.col == checkCol && c.position.row == checkRow) {
                    if(c.getInventory() == null) {
                        catcher = c;
                        caught = true;
                    }
                    break;
                }
            }
            if(caught) break;
        }
        
        Item thrownItem = inventory;
        setInventory(null);
        
        if(caught && catcher != null) {
            catcher.setInventory(thrownItem);
        }
    }

    public void dropItem(GridMap map) {
        if (inventory == null) return;
        
        Tile currentTile = map.getTile(position.col, position.row);
        if (currentTile != null && currentTile.isFloor() && !currentTile.hasDroppedItem()) {
            currentTile.setDroppedItem(inventory);
            setInventory(null);
        }
    }
    
    public void pickupFromFloor(GridMap map) {
        if (inventory != null) return;
        
        Tile currentTile = map.getTile(position.col, position.row);
        if (currentTile != null && currentTile.hasDroppedItem()) {
            setInventory(currentTile.getDroppedItem());
            currentTile.setDroppedItem(null);
        }
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
    
    public boolean canDash() { 
        return dashCooldownTimer <= 0 && !isBusy; 
    }
    
    public float getDashCooldown() { 
        return Math.max(0, dashCooldownTimer); 
    }
}