package com.nimonscooked.model.item;

import com.nimonscooked.interfaces.Preparable;

public class Ingredient extends Item implements Preparable {

    public enum State {
        RAW,
        CHOPPED,
        COOKED,
    }

    private State state;

    public Ingredient(String name, State state) {
        super(name);
        this.state = state;
    }

    public boolean canBeChopped() {
        return state == State.RAW;
    }

    public boolean canBeCooked() {
        return state != State.COOKED;
    }

    public boolean canBePlacedOnPlate(){
        return false;
    }

    public void chop(){
        if (state == State.RAW){
            state = State.CHOPPED;
        }
    }

    public void cook(){
        state = State.COOKED;
    }

    public State getState() {
        return state;
    }

    public void setState(State s) {
        this.state = s;
    }
}
