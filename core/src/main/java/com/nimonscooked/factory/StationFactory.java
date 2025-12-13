package com.nimonscooked.factory;

import com.badlogic.gdx.Gdx;
import com.nimonscooked.config.GameConfig;
import com.nimonscooked.manager.RecipeLoader;
import com.nimonscooked.model.recipe.Recipe;
import com.nimonscooked.model.station.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StationFactory {

    private static List<Recipe> cachedRecipes;
    private static final Map<Character, String> STATION_NAMES = new HashMap<>();

    static {
        STATION_NAMES.put('C', "Cutting Board");
        STATION_NAMES.put('R', "Cooking Station");
        STATION_NAMES.put('A', "Assembly Counter");
        STATION_NAMES.put('S', "Serving Counter");
        STATION_NAMES.put('W', "Washing Station");
        STATION_NAMES.put('P', "Plate Storage");
        STATION_NAMES.put('T', "Trash Bin");
        STATION_NAMES.put('I', "Ingredient Storage");
    }

    public static void initializeRecipes(String recipePath) {
        try {
            cachedRecipes = RecipeLoader.loadRecipes(recipePath);
            Gdx.app.log("StationFactory", "Recipes loaded: " + cachedRecipes.size());
        } catch (Exception e) {
            Gdx.app.error("StationFactory", "Failed to load recipes", e);
            cachedRecipes = new ArrayList<>();
        }
    }

    public static Station createStation(char symbol, int col, int row) {
        String id = symbol + "_" + col + "_" + row;
        Station station = null;
        
        // Calculate pixel coordinates
        float pX = col * GameConfig.TILE_SIZE;
        float pY = row * GameConfig.TILE_SIZE;

        switch (symbol) {
            case 'C':
                station = new CuttingStation(id, pX, pY);
                break;
            case 'R':
                CookingStation.StoveType type = determineStoveType(col);
                station = new CookingStation(id, type, pX, pY);
                break;
            case 'A':
                station = new AssemblyStation(id, cachedRecipes != null ? cachedRecipes : new ArrayList<>(), pX, pY);
                break;
            case 'S':
                station = new ServingCounter(id, pX, pY);
                break;
            case 'W':
                station = new WashingStation(id, pX, pY);
                break;
            case 'P':
                station = new PlateStorage(id, pX, pY);
                break;
            case 'T':
                station = new TrashStation(id, pX, pY);
                break;
            case 'I':
                station = createIngredientStorage(id, col, row, pX, pY);
                break;
            default:
                Gdx.app.error("StationFactory", "Unknown station symbol: " + symbol);
                return null;
        }
        return station;
    }

    private static CookingStation.StoveType determineStoveType(int col) {
        if (col < 7) {
            return CookingStation.StoveType.RIGHT;
        } else {
            return CookingStation.StoveType.LEFT;
        }
    }

    private static IngredientStorage createIngredientStorage(String id, int col, int row, float x, float y) {
        String ingredientName = "Tomato";

        if (col < 5) {
            if (row < 4) {
                ingredientName = "Tomato";
            } else if (row < 6) {
                ingredientName = "Lettuce";
            } else {
                ingredientName = "Cheese";
            }
        } else {
            if (row < 5) {
                ingredientName = "Meat";
            } else {
                ingredientName = "Bun";
            }
        }

        Gdx.app.log("StationFactory", "Created Storage [" + ingredientName + "] at X=" + col + ", Y=" + row);

        return new IngredientStorage(id, ingredientName, x, y);
    }

    public static boolean isStationSymbol(char symbol) {
        return STATION_NAMES.containsKey(symbol);
    }

    public static String getStationName(char symbol) {
        return STATION_NAMES.getOrDefault(symbol, "Unknown");
    }

    public static List<Recipe> getCachedRecipes() {
        return cachedRecipes != null ? new ArrayList<>(cachedRecipes) : new ArrayList<>();
    }
}