package com.nimonscooked.factory;

import com.nimonscooked.model.station.*;
// Hapus import Ingredient jika tidak dipakai langsung di sini
// import com.nimonscooked.model.item.Ingredient; 

public class StationFactory {
    
    public static Station createStation(char symbol, int col, int row) {
        String id = symbol + "_" + col + "_" + row;
        
        switch (symbol) {
            case 'C': return new CuttingStation(id);
            case 'R': return new CookingStation(id);
            // Pass null untuk recipes sementara, nanti diisi oleh GameManager/LevelLoader
            case 'A': return new AssemblyStation(id, new java.util.ArrayList<>()); 
            case 'S': return new ServingCounter(id);
            case 'W': return new WashingStation(id);
            case 'P': return new PlateStorage(id); 
            case 'T': return new TrashStation(id);
            case 'I': return createIngredientStorage(id, col, row);
            default: return null;
        }
    }

    private static IngredientStorage createIngredientStorage(String id, int col, int row) {
        // Gunakan String nama ingredient saja
        if (row >= 7) {
            return new IngredientStorage(id, col <= 3 ? "Cheese" : "Meat");
        } else if (row <= 3) {
            return new IngredientStorage(id, col <= 3 ? "Lettuce" : "Tomato");
        } else {
            return new IngredientStorage(id, "Bun");
        }
    }

    public static boolean isStationSymbol(char symbol) {
        return "CRASWPIT".indexOf(symbol) >= 0;
    }
}