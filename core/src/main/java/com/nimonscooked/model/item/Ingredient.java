package com.nimonscooked.model.item; // Package sudah benar

import com.nimonscooked.model.ingredient.Preparable;

// Tidak perlu import Item karena berada di package yang sama

public class Ingredient extends Item implements Preparable {
    
    public enum State { RAW, CHOPPED, COOKED, BURNT }

    private State state;
    private String baseTexture;

    public Ingredient(String name, String baseTexture) {
        super(name, baseTexture + ".png");
        this.baseTexture = baseTexture;
        this.state = State.RAW;
        updateTexture();
    }

    public Ingredient(Ingredient other) {
        this(other.getName(), other.baseTexture); // Gunakan getter getName()
        this.state = other.state;
        updateTexture();
    }

    public State getState() { return state; }

    public void setState(State newState) {
        this.state = newState;
        updateTexture();
    }

    private void updateTexture() {
        switch (state) {
            case RAW: 
                // Field 'name' dan 'textureName' diwarisi dari Item (protected)
                if (this.name.equalsIgnoreCase("Meat")) {
                    this.textureName = baseTexture + "_raw.png";
                } else {
                    this.textureName = baseTexture + ".png";
                }
                break;
            case CHOPPED: 
                this.textureName = baseTexture + "_chopped.png"; 
                break;
            case COOKED: 
                this.textureName = baseTexture + "_cooked.png"; 
                break;
            case BURNT: 
                this.textureName = baseTexture + "_burnt.png"; 
                break;
        }
    }

    // --- Implementasi Interface Preparable ---

    @Override
    public boolean canBeChopped() {
        return state == State.RAW && (name.equalsIgnoreCase("Tomato") || name.equalsIgnoreCase("Lettuce") || name.equalsIgnoreCase("Cheese"));
    }

    @Override
    public boolean canBeCooked() {
        return (state == State.RAW || state == State.CHOPPED) && (name.equalsIgnoreCase("Meat") || name.equalsIgnoreCase("Bun"));
    }

    @Override
    public boolean canBePlacedOnPlate() {
        return state == State.COOKED || state == State.CHOPPED || (name.equalsIgnoreCase("Bun") && state == State.RAW);
    }

    @Override
    public void chop() {
        if (canBeChopped()) setState(State.CHOPPED);
    }

    @Override
    public void cook() {
        if (canBeCooked()) setState(State.COOKED);
    }

    @Override
    public String getDisplayName() {
        return name + " (" + state + ")";
    }
}