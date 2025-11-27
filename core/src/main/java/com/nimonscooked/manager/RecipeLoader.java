package com.nimonscooked.manager;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;

// --- PERBAIKAN IMPORT DI SINI ---
// Pastikan mengimport Ingredient dari model.item, BUKAN model.ingredient
import com.nimonscooked.model.item.Ingredient; 
import com.nimonscooked.model.item.Item;
import com.nimonscooked.model.recipe.Recipe;

import java.util.ArrayList;
import java.util.List;

public class RecipeLoader {

    public static List<Recipe> loadRecipes(String jsonPath) {
        List<Recipe> recipes = new ArrayList<>();
        
        FileHandle file = Gdx.files.internal(jsonPath);
        if (!file.exists()) {
            Gdx.app.error("RecipeLoader", "Recipe file not found: " + jsonPath);
            return recipes;
        }

        try {
            JsonReader reader = new JsonReader();
            JsonValue root = reader.parse(file);

            for (JsonValue recipeJson : root) {
                String recipeName = recipeJson.getString("name");
                // List<Item> ini hanya bisa menerima objek yang merupakan turunan Item
                List<Item> requiredItems = new ArrayList<>();

                for (String ingredientStr : recipeJson.get("ingredients").asStringArray()) {
                    // Ingredient sekarang sudah benar (extends Item), jadi baris ini aman
                    requiredItems.add(parseIngredientString(ingredientStr));
                }

                recipes.add(new Recipe(recipeName, requiredItems));
                Gdx.app.log("RecipeLoader", "Loaded Recipe: " + recipeName);
            }

        } catch (Exception e) {
            Gdx.app.error("RecipeLoader", "Error parsing recipes: " + e.getMessage());
        }

        return recipes;
    }

    private static Ingredient parseIngredientString(String rawString) {
        String name;
        Ingredient.State state = Ingredient.State.RAW;

        if (rawString.contains("_")) {
            String[] parts = rawString.split("_");
            name = parts[0];
            
            String stateStr = parts[1].toUpperCase();
            try {
                state = Ingredient.State.valueOf(stateStr);
            } catch (IllegalArgumentException e) {
                Gdx.app.error("RecipeLoader", "Unknown state: " + stateStr + " for " + name);
            }
        } else {
            name = rawString;
        }

        // Constructor ini sekarang sudah dikenali (String, String)
        Ingredient ing = new Ingredient(name, "ingredients/" + name.toLowerCase());
        ing.setState(state);
        
        return ing;
    }
}