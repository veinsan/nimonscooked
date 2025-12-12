package com.nimonscooked.factory;

import com.badlogic.gdx.Gdx;
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

        switch (symbol) {
            case 'C': station = new CuttingStation(id); break;
            case 'R': station = new CookingStation(id); break;
            case 'A': station = new AssemblyStation(id, cachedRecipes != null ? cachedRecipes : new ArrayList<>()); break;
            case 'S': station = new ServingCounter(id); break;
            case 'W': station = new WashingStation(id); break;
            case 'P': station = new PlateStorage(id); break;
            case 'T': station = new TrashStation(id); break;
            case 'I': 
                station = createIngredientStorage(id, col, row); 
                break;
            default:
                Gdx.app.error("StationFactory", "Unknown station symbol: " + symbol);
                return null;
        }
        return station;
    }

    /**
     * Logika Penentuan Bahan (FIXED CLEAN VERSION)
     */
    private static IngredientStorage createIngredientStorage(String id, int col, int row) {
        String ingredientName = "Meat"; // Default fallback

        // --- SISI KIRI (Sayuran & Keju) ---
        // Ada 3 Crate bertumpuk vertikal di kolom 0.
        // Y ~ 7 (Atas), Y ~ 5 (Tengah), Y ~ 3 (Bawah).
        if (col < 5) {
            if (row > 6) {
                // Posisi Paling Atas -> Cheese
                ingredientName = "Cheese"; 
            } else if (row > 4) {
                // Posisi Tengah -> Lettuce
                ingredientName = "Lettuce"; 
            } else {
                // Posisi Bawah -> Tomato
                ingredientName = "Tomato";
            }
        } 
        // --- SISI TENGAH/KANAN (Roti & Daging) ---
        // Ada 1 di Atas (Y=9) dan 1 di Bawah (Y=1).
        else {
            if (row > 5) {
                // Posisi Atas -> Bun (Roti)
                ingredientName = "Bun";
            } else {
                // Posisi Bawah -> Meat (Daging)
                ingredientName = "Meat";
            }
        }

        Gdx.app.log("StationFactory", "Created Storage [" + ingredientName + "] at X=" + col + ", Y=" + row);
        
        return new IngredientStorage(id, ingredientName);
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