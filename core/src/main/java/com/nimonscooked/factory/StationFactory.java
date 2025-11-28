package com.nimonscooked.factory;

import com.nimonscooked.manager.RecipeLoader;
import com.nimonscooked.model.recipe.Recipe;
import com.nimonscooked.model.station.*;
import java.util.List;
import java.util.ArrayList;

public class StationFactory {

    private static List<Recipe> cachedRecipes;
    private static final String[] INGREDIENTS_MAP_C = { "Bun", "Meat", "Cheese", "Tomato", "Lettuce" };

    public static void initializeRecipes(String recipePath) {
        cachedRecipes = RecipeLoader.loadRecipes(recipePath);
    }

    public static Station createStation(char symbol, int col, int row) {
        String id = symbol + "_" + col + "_" + row;

        switch (symbol) {
            case 'C': return new CuttingStation(id);
            case 'R': return new CookingStation(id);
            case 'A': return new AssemblyStation(id, cachedRecipes != null ? cachedRecipes : new ArrayList<>());
            case 'S': return new ServingCounter(id);
            case 'W': return new WashingStation(id);
            case 'P': return new PlateStorage(id);
            case 'T': return new TrashStation(id);
            case 'I': return createIngredientStorage(id, col, row);
            default: return null;
        }
    }

    private static IngredientStorage createIngredientStorage(String id, int col, int row) {
        int index = (col + row) % INGREDIENTS_MAP_C.length;
        String ingredientName = INGREDIENTS_MAP_C[index];
        return new IngredientStorage(id, ingredientName);
    }

    public static boolean isStationSymbol(char symbol) {
        return "CRASWPIT".indexOf(symbol) >= 0;
    }
}