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
import com.nimonscooked.model.utensil.Plate;

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

        if (heldItem instanceof Plate && stationItem instanceof Dish) {
            handlePlatingDish(chef, (Plate) heldItem, (Dish) stationItem);
        }
        else if (heldItem instanceof Plate && stationItem instanceof Ingredient) {
            handlePlatingIngredient(chef, (Plate) heldItem, (Ingredient) stationItem);
        }
        else if (heldItem instanceof Dish && stationItem instanceof Plate) {
            handleDishToPlate(chef, (Dish) heldItem, (Plate) stationItem);
        }
        else if (heldItem instanceof Ingredient && stationItem instanceof Plate) {
            handleIngredientToPlate(chef, (Ingredient) heldItem, (Plate) stationItem);
        }
        else if (heldItem != null && stationItem == null) {
            this.setItem(heldItem);
            chef.setInventory(null);
            Gdx.app.log("AssemblyStation", "Placed " + heldItem.getDisplayName());
        } 
        else if (heldItem == null && stationItem != null) {
            chef.setInventory(stationItem);
            this.setItem(null);
            Gdx.app.log("AssemblyStation", "Took " + stationItem.getDisplayName());
        } 
        else if (heldItem instanceof Ingredient && stationItem instanceof Dish) {
            handleAddIngredientToDish(chef, (Ingredient) heldItem, (Dish) stationItem);
        }
        else if (heldItem instanceof Ingredient && stationItem instanceof Ingredient) {
            handleCombineIngredients(chef, (Ingredient) heldItem, (Ingredient) stationItem);
        }
    }

    private void handlePlatingDish(Chef chef, Plate plate, Dish dish) {
        if (!plate.isClean()) {
            Gdx.app.log("AssemblyStation", "FAIL: Cannot use dirty plate!");
            return;
        }

        if (plate.getContainedDish() != null) {
            Gdx.app.log("AssemblyStation", "FAIL: Plate already has food!");
            return;
        }

        plate.setContainedDish(dish);
        this.setItem(null);
        Gdx.app.log("AssemblyStation", "Plated " + dish.getDisplayName());
    }

    private void handlePlatingIngredient(Chef chef, Plate plate, Ingredient ingredient) {
        if (!plate.isClean()) {
            Gdx.app.log("AssemblyStation", "FAIL: Cannot use dirty plate!");
            return;
        }

        if (plate.getContainedDish() != null) {
            List<Item> components = new ArrayList<>(plate.getContainedDish().getComponents());
            components.add(ingredient);
            Dish newDish = new Dish("Unfinished Dish", components);
            newDish = (Dish) tryAssemble(newDish);
            plate.setContainedDish(newDish);
            this.setItem(null);
            Gdx.app.log("AssemblyStation", "Added ingredient to plate");
        } else {
            List<Item> components = new ArrayList<>();
            components.add(ingredient);
            Dish newDish = new Dish("Unfinished Dish", components);
            newDish = (Dish) tryAssemble(newDish);
            plate.setContainedDish(newDish);
            this.setItem(null);
            Gdx.app.log("AssemblyStation", "Placed ingredient on plate");
        }
    }

    private void handleDishToPlate(Chef chef, Dish dish, Plate plate) {
        if (!plate.isClean()) {
            Gdx.app.log("AssemblyStation", "FAIL: Plate on station is dirty!");
            return;
        }

        if (plate.getContainedDish() != null) {
            Gdx.app.log("AssemblyStation", "FAIL: Plate already has food!");
            return;
        }

        plate.setContainedDish(dish);
        chef.setInventory(plate);
        this.setItem(null);
        Gdx.app.log("AssemblyStation", "Moved dish to plate in hand");
    }

    private void handleIngredientToPlate(Chef chef, Ingredient ingredient, Plate plate) {
        if (!plate.isClean()) {
            Gdx.app.log("AssemblyStation", "FAIL: Plate on station is dirty!");
            return;
        }

        if (plate.getContainedDish() != null) {
            List<Item> components = new ArrayList<>(plate.getContainedDish().getComponents());
            components.add(ingredient);
            Dish newDish = new Dish("Unfinished Dish", components);
            newDish = (Dish) tryAssemble(newDish);
            plate.setContainedDish(newDish);
            chef.setInventory(null);
            Gdx.app.log("AssemblyStation", "Added ingredient to plate on station");
        } else {
            List<Item> components = new ArrayList<>();
            components.add(ingredient);
            Dish newDish = new Dish("Unfinished Dish", components);
            newDish = (Dish) tryAssemble(newDish);
            plate.setContainedDish(newDish);
            chef.setInventory(null);
            Gdx.app.log("AssemblyStation", "Placed ingredient on plate on station");
        }
    }

    private void handleAddIngredientToDish(Chef chef, Ingredient ingredient, Dish dish) {
        List<Item> components = new ArrayList<>(dish.getComponents());
        components.add(ingredient);
        Dish newDish = new Dish("Unfinished Dish", components);
        newDish = (Dish) tryAssemble(newDish);
        this.setItem(newDish);
        chef.setInventory(null);
        Gdx.app.log("AssemblyStation", "Added ingredient to dish");
    }

    private void handleCombineIngredients(Chef chef, Ingredient held, Ingredient station) {
        List<Item> components = new ArrayList<>();
        components.add(station);
        components.add(held);
        Dish newDish = new Dish("Unfinished Dish", components);
        newDish = (Dish) tryAssemble(newDish);
        this.setItem(newDish);
        chef.setInventory(null);
        Gdx.app.log("AssemblyStation", "Combined ingredients into dish");
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
        } else if (current instanceof Plate) {
            Plate plate = (Plate) current;
            if (plate.getContainedDish() != null) {
                return new ArrayList<>(plate.getContainedDish().getComponents());
            }
        }
        return Collections.emptyList();
    }

    public List<Recipe> getAvailableRecipes() {
        return new ArrayList<>(availableRecipes);
    }
}