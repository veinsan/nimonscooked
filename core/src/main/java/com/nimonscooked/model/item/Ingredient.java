package com.nimonscooked.model.item;

import com.nimonscooked.model.ingredient.Preparable;

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
        this(other.getName(), other.baseTexture);
        this.state = other.state;
        updateTexture();
    }

    public State getState() {
        return state;
    }

    public void setState(State newState) {
        this.state = newState;
        updateTexture();
    }

    private void updateTexture() {
        String lowerName = this.name.toLowerCase();
        switch (state) {
            case RAW:
                if (lowerName.equals("meat")) {
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

    @Override
    public boolean canBeChopped() {
        if (state != State.RAW) return false;
        String lowerName = name.toLowerCase();
        return lowerName.equals("tomato") ||
               lowerName.equals("lettuce") ||
               lowerName.equals("cheese") ||
               lowerName.equals("meat");
    }

    @Override
    public boolean canBeCooked() {
        if (state == State.COOKED || state == State.BURNT) return false;
        String lowerName = name.toLowerCase();
        return lowerName.equals("meat");
    }

    @Override
    public boolean canBePlacedOnPlate() {
        String lowerName = name.toLowerCase();
        if (lowerName.equals("bun")) {
            return state == State.RAW;
        }
        return state == State.COOKED || state == State.CHOPPED;
    }

    @Override
    public void chop() {
        if (canBeChopped()) {
            setState(State.CHOPPED);
        }
    }

    @Override
    public void cook() {
        if (canBeCooked()) {
            setState(State.COOKED);
        }
    }

    @Override
    public String getDisplayName() {
        return name + " (" + state + ")";
    }
}