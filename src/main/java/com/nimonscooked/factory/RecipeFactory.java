package com.nimonscooked.factory;

import com.nimonscooked.model.recipe.Recipe;
import com.nimonscooked.model.recipe.RecipeBuilder;

import java.util.ArrayList;
import java.util.List;

public class RecipeFactory {
    
    private static RecipeFactory instance;

    private RecipeFactory() {}

    public static RecipeFactory getInstance() {
        if (instance == null) {
            synchronized (RecipeFactory.class) {
                if (instance == null) {
                    instance = new RecipeFactory();
                }
            }
        }
        return instance;
    }

    public List<Recipe> createBurgerMenu() {
        List<Recipe> menu = new ArrayList<>();
        menu.add(createClassicBurger());
        menu.add(createCheeseburger());
        menu.add(createBLTBurger());
        menu.add(createDeluxeBurger());
        return menu;
    }

    public Recipe createClassicBurger() {
        return RecipeBuilder.create("Classic Burger")
            .addRawIngredient("Bread")
            .addCookedIngredient("Meat")
            .build();
    }

    public Recipe createCheeseburger() {
        return RecipeBuilder.create("Cheeseburger")
            .addRawIngredient("Bread")
            .addCookedIngredient("Meat")
            .addChoppedIngredient("Cheese")
            .build();
    }

    public Recipe createBLTBurger() {
        return RecipeBuilder.create("BLT Burger")
            .addRawIngredient("Bread")
            .addChoppedIngredient("Lettuce")
            .addChoppedIngredient("Tomato")
            .addCookedIngredient("Meat")
            .build();
    }

    public Recipe createDeluxeBurger() {
        return RecipeBuilder.create("Deluxe Burger")
            .addRawIngredient("Bread")
            .addChoppedIngredient("Lettuce")
            .addCookedIngredient("Meat")
            .addChoppedIngredient("Cheese")
            .build();
    }

    public List<Recipe> createSushiMenu() {
        List<Recipe> menu = new ArrayList<>();
        
        menu.add(RecipeBuilder.create("Kappa Maki")
            .addRawIngredient("Nori")
            .addCookedIngredient("Rice")
            .addChoppedIngredient("Cucumber")
            .build());

        menu.add(RecipeBuilder.create("Sakana Maki")
            .addRawIngredient("Nori")
            .addCookedIngredient("Rice")
            .addRawIngredient("Fish")
            .build());

        menu.add(RecipeBuilder.create("Ebi Maki")
            .addRawIngredient("Nori")
            .addCookedIngredient("Rice")
            .addCookedIngredient("Shrimp")
            .build());

        menu.add(RecipeBuilder.create("Fish Cucumber Roll")
            .addRawIngredient("Nori")
            .addCookedIngredient("Rice")
            .addRawIngredient("Fish")
            .addChoppedIngredient("Cucumber")
            .build());

        return menu;
    }

    public List<Recipe> createPastaMenu() {
        List<Recipe> menu = new ArrayList<>();
        
        menu.add(RecipeBuilder.create("Pasta Marinara")
            .addCookedIngredient("Pasta")
            .addCookedIngredient("Tomato")
            .build());

        menu.add(RecipeBuilder.create("Pasta Bolognese")
            .addCookedIngredient("Pasta")
            .addCookedIngredient("Meat")
            .build());

        menu.add(RecipeBuilder.create("Pasta Frutti di Mare")
            .addCookedIngredient("Pasta")
            .addCookedIngredient("Shrimp")
            .addCookedIngredient("Fish")
            .build());

        return menu;
    }

    public List<Recipe> createPizzaMenu() {
        List<Recipe> menu = new ArrayList<>();
        
        menu.add(RecipeBuilder.create("Pizza Margherita")
            .addChoppedIngredient("Dough")
            .addChoppedIngredient("Tomato")
            .addChoppedIngredient("Cheese")
            .build());

        menu.add(RecipeBuilder.create("Pizza Sosis")
            .addChoppedIngredient("Dough")
            .addChoppedIngredient("Tomato")
            .addChoppedIngredient("Cheese")
            .addChoppedIngredient("Sausage")
            .build());

        menu.add(RecipeBuilder.create("Pizza Ayam")
            .addChoppedIngredient("Dough")
            .addChoppedIngredient("Tomato")
            .addChoppedIngredient("Cheese")
            .addChoppedIngredient("Chicken")
            .build());

        return menu;
    }

    public Recipe createCustomRecipe(String name, RecipeBuilder builder) {
        return builder.setName(name).build();
    }
}