package com.nimonscooked.model.station;

import com.badlogic.gdx.math.Rectangle;
import com.nimonscooked.model.entity.Chef;
import com.nimonscooked.model.item.Item;

public abstract class Station {
    protected String id;
    protected Item item;
    
    // Bounding box untuk collision detection
    protected Rectangle bounds;
    
    public Station(String id, float x, float y, float width, float height) {
        this.id = id;
        this.item = null;
        
        // Padding untuk hitbox yang lebih akurat
        float sidePadding = 6.0f;
        float topPadding = 6.0f;
        float bottomPadding = 6.0f;
        
        this.bounds = new Rectangle(
            x + sidePadding,
            y + bottomPadding,
            width - (sidePadding * 2),
            height - (bottomPadding + topPadding)
        );
    }

    public abstract void interact(Chef chef);

    // DEFAULT IMPLEMENTATION: Override di subclass yang butuh hold
    public void processHold(Chef chef, float delta) {
        // Do nothing by default
    }

    public String getId() {
        return id;
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public boolean hasItem() {
        return item != null;
    }
    
    // Getter untuk collision system
    public Rectangle getBounds() {
        return bounds;
    }
}