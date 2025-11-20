package com.nimonscooked.factory;

import com.nimonscooked.model.ingredient.Ingredient;
import com.nimonscooked.model.ingredient.IngredientState;

public class IngredientFactory {
    
    private static IngredientFactory instance;

    private IngredientFactory() {}

    public static IngredientFactory getInstance() {
        if (instance == null) {
            synchronized (IngredientFactory.class) {
                if (instance == null) {
                    instance = new IngredientFactory();
                }
            }
        }
        return instance;
    }

    public Ingredient createIngredient(String type) {
        return createIngredient(type, IngredientState.RAW);
    }

    public Ingredient createIngredient(String type, IngredientState state) {
        if (type == null || type.isEmpty()) {
            throw new IllegalArgumentException("Ingredient type cannot be null or empty");
        }

        switch (type.toLowerCase()) {
            case "bread":
            case "roti":
                return new Ingredient("Bread", state);
            
            case "meat":
            case "daging":
                return new Ingredient("Meat", state);
            
            case "cheese":
            case "keju":
                return new Ingredient("Cheese", state);
            
            case "lettuce":
            case "selada":
                return new Ingredient("Lettuce", state);
            
            case "tomato":
            case "tomat":
                return new Ingredient("Tomato", state);
            
            case "rice":
            case "beras":
            case "nasi":
                return new Ingredient("Rice", state);
            
            case "pasta":
                return new Ingredient("Pasta", state);
            
            case "fish":
            case "ikan":
                return new Ingredient("Fish", state);
            
            case "shrimp":
            case "udang":
                return new Ingredient("Shrimp", state);
            
            case "nori":
                return new Ingredient("Nori", state);
            
            case "cucumber":
            case "timun":
                return new Ingredient("Cucumber", state);
            
            default:
                return new Ingredient(type, state);
        }
    }

    public Ingredient createRawIngredient(String type) {
        return createIngredient(type, IngredientState.RAW);
    }

    public Ingredient createChoppedIngredient(String type) {
        return createIngredient(type, IngredientState.CHOPPED);
    }

    public Ingredient createCookedIngredient(String type) {
        return createIngredient(type, IngredientState.COOKED);
    }
}