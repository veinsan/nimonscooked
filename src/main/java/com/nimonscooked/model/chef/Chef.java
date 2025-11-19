package com.nimonscooked.model.chef;

import com.nimonscooked.model.Item;

public class Chef {
    private String id;
    private String name;
    private Position position;
    private Direction direction;
    private Item inventory; // max 1 item

    public Chef(String id, String name, Position pos, Direction dir) {
        this.id = id;
        this.name = name;
        this.position = pos;
        this.direction = dir;
        this.inventory = null;
    }

    // Getters
    public String getId() { return id; }
    public String getName() { return name; }
    public Position getPosition() { return position; }
    public Direction getDirection() { return direction; }
    public Item getInventory() { return inventory; }

    // Movement
    public void setDirection(Direction dir) {
        this.direction = dir;
    }
    
    public void move(int newRow, int newCol) {
        this.position.set(newRow, newCol);
    }

    // Inventory management
    public void setInventory(Item item) {
        this.inventory = item;
    }
    
    public boolean hasItem() {
        return inventory != null;
    }
    
    public Item removeInventory() {
        Item temp = this.inventory;
        this.inventory = null;
        return temp;
    }
}