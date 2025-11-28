package com.nimonscooked.model.station;

import com.badlogic.gdx.Gdx;
import com.nimonscooked.model.entity.Chef;
import com.nimonscooked.model.item.Item;
import com.nimonscooked.model.dish.Dish;
import com.nimonscooked.model.recipe.Recipe;
import com.nimonscooked.model.item.Ingredient;
import java.util.ArrayList;
import java.util.List;

public class AssemblyStation extends Station {

    private final List<Recipe> availableRecipes;

    public AssemblyStation(String id, List<Recipe> recipes) {
        super(id);
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
        } else if (heldItem == null && stationItem != null) {
            chef.setInventory(stationItem);
            this.setItem(null);
            Gdx.app.log("AssemblyStation", "Took " + stationItem.getDisplayName());
        } else if (heldItem instanceof Ingredient && stationItem != null) {
            Item result = combine(stationItem, heldItem);
            if (result != null) {
                this.setItem(result);
                chef.setInventory(null);
                Gdx.app.log("AssemblyStation", "Combined item into: " + result.getDisplayName());
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
                return new Dish(recipe.getName(), inputDish.getComponents());
            }
        }

        return input;
    }

    public List<Recipe> getAvailableRecipes() {
        return new ArrayList<>(availableRecipes);
    }
}