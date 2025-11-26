package com.nimonscooked.interfaces;

public interface Preparable {
    enum State {
        RAW,
        CHOPPED,
        COOKING,
        COOKED,
        BURNED,
    }
    boolean canBeChopped();
    boolean canBeCooked();
    boolean canBePlacedOnPlate();
    void chop();
    void cook();
    State getState();
}