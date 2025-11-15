package com.nimonscooked.model.station;

import com.nimonscooked.model.ingredient.Ingredient;

public class IngredientStorage extends Station {
    private Ingredient.State ingredientType; // jenis ingredient yang disimpan
    private String ingredientName; // e.g., "Tomat", "Nori"

    public IngredientStorage(String id, String ingredientName) {
        super(id);
        this.ingredientName = ingredientName;
    }
    
    public Ingredient getIngredient() {
        // Return fresh raw ingredient (unlimited stock)
        return new Ingredient(ingredientName, Ingredient.State.RAW);
    }
    
    public String getIngredientName() {
        return ingredientName;
    }
}