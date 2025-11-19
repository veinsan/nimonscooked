package com.nimonscooked.model.ingredient;

import com.nimonscooked.model.Item;

public class Ingredient extends Item implements Preparable {

    public enum State {
        RAW,
        CHOPPED,
        COOKED
    }

    private State state;

    public Ingredient(String name, State state) {
        super(name.toLowerCase());
        this.state = state;
    }

    public State getState() {
        return state;
    }

    public void setState(State newState) {
        this.state = newState;
    }

    public String getTextureKey() {
        switch (state) {
            case RAW:
                return name.equals("bread") ? "bread" : name + "_raw";
            case CHOPPED:
                return name + "_chopped";
            case COOKED:
                return name + "_cooked";
            default:
                return name;
        }
    }

    @Override
    public String getDisplayName() {
        String capitalizedName = name.substring(0, 1).toUpperCase() + name.substring(1);
        return capitalizedName + " (" + state + ")";
    }

    public Ingredient withState(State newState) {
        return new Ingredient(this.name, newState);
    }

    @Override
    public boolean canBeChopped() {
        return state == State.RAW;
    }

    @Override
    public boolean canBeCooked() {
        return state == State.RAW || state == State.CHOPPED;
    }

    @Override
    public boolean canBePlacedOnPlate() {
        return state == State.CHOPPED || state == State.COOKED;
    }

    @Override
    public void chop() {
        if (canBeChopped()) {
            this.state = State.CHOPPED;
        }
    }

    @Override
    public void cook() {
        if (canBeCooked()) {
            this.state = State.COOKED;
        }
    }
}