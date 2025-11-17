package com.nimonscooked.model.ingredient;

import com.nimonscooked.model.Item;

public class Ingredient extends Item {

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
}
