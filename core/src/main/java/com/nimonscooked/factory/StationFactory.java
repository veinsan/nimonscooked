package com.nimonscooked.factory;

import com.nimonscooked.manager.RecipeLoader;
import com.nimonscooked.model.recipe.Recipe;
import com.nimonscooked.model.station.*;
import java.util.List;

public class StationFactory {

    private static List<Recipe> cachedRecipes;

    public static void initializeRecipes(String recipePath) {
        cachedRecipes = RecipeLoader.loadRecipes(recipePath);
    }

    public static Station createStation(char symbol, int col, int row) {
        String id = symbol + "_" + col + "_" + row;

        switch (symbol) {
            case 'C': return new CuttingStation(id);
            case 'R': return new CookingStation(id);
            case 'A': return new AssemblyStation(id, cachedRecipes != null ? cachedRecipes : List.of());
            case 'S': return new ServingCounter(id);
            case 'W': return new WashingStation(id);
            case 'P': return new PlateStorage(id);
            case 'T': return new TrashStation(id);
            case 'I': return createIngredientStorage(id, col, row);
            default: return null;
        }
    }

    private static IngredientStorage createIngredientStorage(String id, int col, int row) {
        if (row == 0) {
            if (col == 1) return new IngredientStorage(id, "Lettuce");
            if (col == 8) return new IngredientStorage(id, "Bun");
        } else if (row == 1 || row == 2) {
            if (col == 0) return new IngredientStorage(id, "Lettuce");
            if (col == 13) return new IngredientStorage(id, "Tomato");
        } else if (row == 3 || row == 4) {
            if (col == 0) return new IngredientStorage(id, "Cheese");
            if (col == 13) return new IngredientStorage(id, "Tomato");
        } else if (row == 6) {
            if (col == 0) return new IngredientStorage(id, "Cheese");
            if (col == 13) return new IngredientStorage(id, "Tomato");
        } else if (row == 8) {
            if (col == 8) return new IngredientStorage(id, "Bun");
        }
        return new IngredientStorage(id, "Meat");
    }

    public static boolean isStationSymbol(char symbol) {
        return "CRASWPIT".indexOf(symbol) >= 0;
    }
}