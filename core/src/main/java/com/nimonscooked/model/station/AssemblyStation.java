package com.nimonscooked.model.station;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.nimonscooked.model.dish.Dish;
import com.nimonscooked.model.entity.Chef;
import com.nimonscooked.model.item.Ingredient;
import com.nimonscooked.model.item.Item;
import com.nimonscooked.model.recipe.Recipe;

public class AssemblyStation extends Station {

    private final List<Recipe> availableRecipes;

    public AssemblyStation(String id, List<Recipe> recipes, float x, float y) {
        super(id, x, y, 64, 64);
        this.availableRecipes = recipes != null ? new ArrayList<>(recipes) : new ArrayList<>();
    }

    @Override
    public void interact(Chef chef) {
        Item heldItem = chef.getInventory();
        Item stationItem = this.getItem();

        if (heldItem != null && stationItem == null) {
            this.setItem(heldItem);
            chef.setInventory(null);
            Gdx.app.log("AssemblyStation", "Placed " + heldItem.getDisplayName());
        } 
        else if (heldItem == null && stationItem != null) {
            chef.setInventory(stationItem);
            this.setItem(null);
            Gdx.app.log("AssemblyStation", "Took " + stationItem.getDisplayName());
        } 
        else if (heldItem instanceof Ingredient && stationItem != null) {
            Item result = combine(stationItem, heldItem);
            if (result != null) {
                this.setItem(result);
                chef.setInventory(null);
                Gdx.app.log("AssemblyStation", "Combined item into: " + result.getDisplayName());
            }
        }
        else if (heldItem instanceof com.nimonscooked.model.utensil.Plate && stationItem instanceof Dish) {
            com.nimonscooked.model.utensil.Plate plate = (com.nimonscooked.model.utensil.Plate) heldItem;
        
            if (!plate.isClean()) {
                 Gdx.app.log("AssemblyStation", "FAIL: Cannot plate on dirty plate!");
                 return;
            }
            
            if (plate.getContainedDish() == null) {
                plate.setContainedDish((Dish) stationItem);
                this.setItem(null); 
                Gdx.app.log("AssemblyStation", "Plated " + plate.getContainedDish().getDisplayName());
            }
        }
    }
    private Item combine(Item base, Item added) {
        List<Item> components = new ArrayList<>();

        if (base instanceof Dish) {
            components.addAll(((Dish) base).getComponents());
        } else if (base instanceof Ingredient) {
            components.add(base);
        }

        components.add(added);
        Dish potentialDish = new Dish("Unfinished Dish", components);
        return tryAssemble(potentialDish);
    }

    public Item tryAssemble(Item input) {
        if (!(input instanceof Dish)) return input;

        Dish inputDish = (Dish) input;

        for (Recipe recipe : availableRecipes) {
            if (recipe.matches(inputDish)) {
                Gdx.app.log("AssemblyStation", "RECIPE COMPLETED: " + recipe.getName());
                Dish completedDish = new Dish(recipe.getName(), inputDish.getComponents());
                completedDish.setMatchedRecipe(recipe.getName());
                return completedDish;
            }
        }

        return input;
    }

    public List<Item> getCurrentIngredients() {
        Item current = this.getItem();
        if (current instanceof Dish) {
            return new ArrayList<>(((Dish) current).getComponents());
        }
        return Collections.emptyList();
    }

    public List<Recipe> getAvailableRecipes() {
        return new ArrayList<>(availableRecipes);
    }
}