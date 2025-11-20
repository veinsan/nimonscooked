package com.nimonscooked.factory;

import com.nimonscooked.model.station.*;
import com.nimonscooked.model.recipe.Recipe;
import com.nimonscooked.exception.InvalidActionException;

import java.util.List;

public class StationFactory {
    
    private static StationFactory instance;

    private StationFactory() {}

    public static StationFactory getInstance() {
        if (instance == null) {
            synchronized (StationFactory.class) {
                if (instance == null) {
                    instance = new StationFactory();
                }
            }
        }
        return instance;
    }

    public Station createStation(String type, String id) {
        return createStation(type, id, null, null);
    }

    public Station createStation(String type, String id, String ingredientType, List<Recipe> recipes) {
        if (type == null || type.isEmpty()) {
            throw new InvalidActionException("Station type cannot be null or empty");
        }

        switch (type.toUpperCase()) {
            case "C":
            case "CUTTING":
                return new CuttingStation(id);

            case "R":
            case "COOKING":
                return new CookingStation(id);

            case "A":
            case "ASSEMBLY":
                if (recipes == null) {
                    throw new InvalidActionException("Assembly station requires recipes");
                }
                return new AssemblyStation(id, recipes);

            case "I":
            case "INGREDIENT":
                if (ingredientType == null || ingredientType.isEmpty()) {
                    throw new InvalidActionException("Ingredient storage requires ingredient type");
                }
                return new IngredientStorage(id, ingredientType);

            case "P":
            case "PLATE":
                return new PlateStorage(id);

            case "S":
            case "SERVING":
                return new ServingCounter(id);

            case "W":
            case "WASHING":
                return new WashingStation(id);

            case "T":
            case "TRASH":
                return new TrashStation(id);

            default:
                throw new InvalidActionException("Unknown station type: " + type);
        }
    }

    public CuttingStation createCuttingStation(String id) {
        return new CuttingStation(id);
    }

    public CookingStation createCookingStation(String id) {
        return new CookingStation(id);
    }

    public AssemblyStation createAssemblyStation(String id, List<Recipe> recipes) {
        return new AssemblyStation(id, recipes);
    }

    public IngredientStorage createIngredientStorage(String id, String ingredientType) {
        return new IngredientStorage(id, ingredientType);
    }

    public PlateStorage createPlateStorage(String id) {
        return new PlateStorage(id);
    }

    public ServingCounter createServingCounter(String id) {
        return new ServingCounter(id);
    }

    public WashingStation createWashingStation(String id) {
        return new WashingStation(id);
    }

    public TrashStation createTrashStation(String id) {
        return new TrashStation(id);
    }
}