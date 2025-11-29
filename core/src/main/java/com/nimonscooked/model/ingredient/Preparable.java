package com.nimonscooked.model.ingredient;

public interface Preparable {
    boolean canBeChopped();
    boolean canBeCooked();
    boolean canBePlacedOnPlate();
    
    void chop();
    void cook();
    
    default boolean isReadyToServe() {
        return canBePlacedOnPlate();
    }
    
    default boolean requiresPreparation() {
        return canBeChopped() || canBeCooked();
    }
}