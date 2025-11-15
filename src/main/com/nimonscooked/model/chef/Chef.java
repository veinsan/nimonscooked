package com.nimonscooked.model.chef;

import com.nimonscooked.model.Item;

public class Chef {
    private String id;
    private String name;
    private Position position;
    private Direction direction;
    private Item inventory; // holds 1 item

    public Chef(String id, String name, Position pos, Direction direction) {
        this.id = id;
        this.name = name;
        this.position = pos;
        this.direction = direction;
    }

    public Position getPosition() { return position; }
    public Direction getDirection() { return direction; }
    public Item getInventory() { return inventory; }

    public void setInventory(Item item) {
        this.inventory = item;
    }
}
