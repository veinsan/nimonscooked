package com.nimonscooked.model.station;

import com.nimonscooked.model.Item;
import com.nimonscooked.model.dish.Dish;
import com.nimonscooked.model.recipe.Recipe;

import java.util.ArrayList;
import java.util.List;

public class AssemblyStation extends Station {

    private final List<Recipe> availableRecipes;

    public AssemblyStation(String id, List<Recipe> recipes) {
        super(id);
        this.availableRecipes = new ArrayList<>(recipes);
    }

    public Item tryAssemble(Item input) {
        if (!(input instanceof Dish)) {
            return input;
        }

        Dish inputDish = (Dish) input;

        for (Recipe recipe : availableRecipes) {
            if (recipe.matches(inputDish)) {
                return new Dish(recipe.getName(), inputDish.getComponents());
            }
        }

        return input;
    }

    public List<Recipe> getAvailableRecipes() {
        return new ArrayList<>(availableRecipes);
    }
}