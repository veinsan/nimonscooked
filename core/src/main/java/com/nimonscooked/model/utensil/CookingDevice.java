package com.nimonscooked.model.utensil;

import com.nimonscooked.model.ingredient.Preparable;

public interface CookingDevice {
    // Fitur Dasar (M1)
    boolean isPortable();
    int capacity();
    boolean canAccept(Preparable ingredient);
    void addIngredient(Preparable ingredient);
    void startCooking();

    // Fitur Konkurensi & UI (M2 - Wajib Ditambahkan ke Interface)
    void stopCooking();
    boolean isCooking();
    float getProgress();
}
