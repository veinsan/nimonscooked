package com.nimonscooked.model.ingredient;

public interface Preparable {
    
    String getName();

    boolean canBeChopped();
    void chop();

    boolean canBeCooked();
    void cook();
    
    boolean canBePlacedOnPlate(); // ← ADD THIS
}