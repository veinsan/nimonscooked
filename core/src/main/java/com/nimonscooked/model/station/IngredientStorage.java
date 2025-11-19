package com.nimonscooked.model.station;

import com.nimonscooked.model.ingredient.Ingredient;

public class IngredientStorage extends Station {
    private final String ingredientName;

    public IngredientStorage(String id, String ingredientName) {
        super(id);
        this.ingredientName = ingredientName;
    }

    public Ingredient getIngredient() {
        return new Ingredient(ingredientName, Ingredient.State.RAW);
    }

    public String getIngredientName() {
        return ingredientName;
    }

    @Override
    public String toString() {
        return "IngredientStorage[" + id + ": " + ingredientName + "]";
    }
}