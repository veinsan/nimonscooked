package com.nimonscooked.model.station;

import com.badlogic.gdx.Gdx;
import com.nimonscooked.model.entity.Chef;
import com.nimonscooked.model.item.Ingredient;

public class IngredientStorage extends Station {
    private String ingredientName;

    public IngredientStorage(String id, String ingredientName) {
        super(id);
        this.ingredientName = ingredientName;
    }

    @Override
    public void interact(Chef chef) {
        if (chef.getInventory() == null) {
            Ingredient newIngredient = createIngredient(ingredientName);
            chef.setInventory(newIngredient);
            Gdx.app.log("Storage", "Chef took: " + newIngredient.getName());
        }
    }

    private Ingredient createIngredient(String name) {
        String basePath = "ingredients/" + name.toLowerCase();
        return new Ingredient(name, basePath);
    }
}