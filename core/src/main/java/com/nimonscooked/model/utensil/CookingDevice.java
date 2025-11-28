package com.nimonscooked.model.utensil;

import com.nimonscooked.model.ingredient.Preparable;

public interface CookingDevice {
    boolean isPortable();
    int capacity();
    boolean canAccept(Preparable ingredient);
    void addIngredient(Preparable ingredient);
    void startCooking();
    void stopCooking();
    boolean isCooking();
    float getProgress();
}