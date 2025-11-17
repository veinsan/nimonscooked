package com.nimonscooked.model.ingredient;

import com.nimonscooked.model.Item;

public class Ingredient extends Item implements Preparable {
    
    public enum State {
        RAW, CHOPPED, COOKED
    }

    private State state;

    public Ingredient(String name, State state) {
        super(name);
        this.state = state;
    }

    public State getState() {
        return state;
    }

    public void setState(State s) {
        this.state = s;
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
        } else {
            throw new IllegalStateException("Cannot chop ingredient in state: " + state);
        }
    }

    @Override
    public void cook() {
        if (canBeCooked()) {
            this.state = State.COOKED;
        } else {
            throw new IllegalStateException("Cannot cook ingredient in state: " + state);
        }
    }
}