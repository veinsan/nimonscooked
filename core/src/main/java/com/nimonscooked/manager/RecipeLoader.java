package com.nimonscooked.manager;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.nimonscooked.model.item.Ingredient;
import com.nimonscooked.model.item.Item;
import com.nimonscooked.model.recipe.Recipe;
import com.nimonscooked.exception.GameLoadException;

import java.util.ArrayList;
import java.util.List;

public class RecipeLoader {

    public static List<Recipe> loadRecipes(String jsonPath) {
        List<Recipe> recipes = new ArrayList<>();
        
        FileHandle file = Gdx.files.internal(jsonPath);
        if (!file.exists()) {
            throw new GameLoadException("CRITICAL: Recipe file not found at " + jsonPath);
        }

        try {
            JsonReader reader = new JsonReader();
            JsonValue root = reader.parse(file);

            for (JsonValue recipeJson : root) {
                if (!recipeJson.has("name") || !recipeJson.has("ingredients")) {
                     throw new GameLoadException("Invalid recipe format: Missing name or ingredients array.");
                }

                String recipeName = recipeJson.getString("name");
                List<Item> requiredItems = new ArrayList<>();

                for (String ingredientStr : recipeJson.get("ingredients").asStringArray()) {
                    requiredItems.add(parseIngredientString(ingredientStr));
                }

                recipes.add(new Recipe(recipeName, requiredItems));
                Gdx.app.log("RecipeLoader", "Loaded Recipe: " + recipeName);
            }

        } catch (Exception e) {
            throw new GameLoadException("Failed to parse recipe file: " + jsonPath, e);
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
                throw new GameLoadException("Unknown ingredient state: " + stateStr + " in " + rawString);
            }
        } else {
            name = rawString; 
        }

        Ingredient ing = new Ingredient(name, "ingredients/" + name.toLowerCase());
        
        ing.setState(state);
        
        return ing;
    }
}