package com.nimonscooked.interfaces;

public interface Preparable {
    enum State {
        RAW,
        CHOPPED,
        COOKED,
    }
    boolean canBeChopped();
    boolean canBeCooked();
    boolean canBePlacedOnPlate();
    void chop();
    void cook();
    State getState();
}