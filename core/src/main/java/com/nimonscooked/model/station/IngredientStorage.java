package com.nimonscooked.model.station;

import java.util.Collections;

import com.badlogic.gdx.Gdx;
import com.nimonscooked.model.dish.Dish;
import com.nimonscooked.model.entity.Chef;
import com.nimonscooked.model.item.Ingredient;
import com.nimonscooked.model.item.Item;
import com.nimonscooked.model.utensil.Plate;

public class IngredientStorage extends Station {
    private String ingredientName;

    public IngredientStorage(String id, String ingredientName, float x, float y) {
        super(id, x, y, 64, 64);
        this.ingredientName = ingredientName;
        
        if (ingredientName.equalsIgnoreCase("Bun")) {
            Gdx.app.log("StorageDebug", "Bun Storage Created at ID: " + id);
        }
    }

    public String getIngredientName() {
        return ingredientName;
    }

    @Override
    public void interact(Chef chef) {
        Item heldItem = chef.getInventory();

        if (heldItem == null) {
            Ingredient newIngredient = createIngredient(ingredientName);
            chef.setInventory(newIngredient);
            Gdx.app.log("Storage", "SUCCESS: Chef took " + newIngredient.getName());
        } 
        
        else if (heldItem instanceof Plate) {
            Plate plate = (Plate) heldItem;
            
            if (!plate.isClean()) {
                Gdx.app.log("Storage", "FAIL: Plate is dirty! Wash it first.");
                return;
            }
            
            if (plate.getContainedDish() == null) {
                Ingredient newIngredient = createIngredient(ingredientName);
                
                if (newIngredient.canBePlacedOnPlate()) {
                    Dish newDish = new Dish(
                        newIngredient.getName(), 
                        Collections.singletonList((Item) newIngredient)
                    );
                    plate.setContainedDish(newDish);
                    Gdx.app.log("Storage", "SUCCESS: Plated " + newIngredient.getName());
                } else {
                    Gdx.app.log("Storage", "FAIL: Cannot plate raw " + newIngredient.getName());
                }
            } else {
                Gdx.app.log("Storage", "FAIL: Plate is already full!");
            }
        }
        
        else {
            Gdx.app.log("Storage", "FAIL: Hands full! Holding: " + heldItem.getName());
        }
    }

    private Ingredient createIngredient(String name) {
        String texturePath = "ingredients/" + name.toLowerCase() + ".png";
        return new Ingredient(name, texturePath);
    }
}