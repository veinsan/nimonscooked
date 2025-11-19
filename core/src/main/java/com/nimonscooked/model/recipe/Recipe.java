package com.nimonscooked.model.recipe;

import com.nimonscooked.model.Item;
import com.nimonscooked.model.ingredient.Ingredient;
import com.nimonscooked.model.dish.Dish;

import java.util.ArrayList;
import java.util.List;

public class Recipe {

    private final String name;
    private final List<Item> requiredItems;

    public Recipe(String name, List<Item> requiredItems) {
        this.name = name;
        this.requiredItems = new ArrayList<>(requiredItems);
    }

    public String getName() {
        return name;
    }

    public List<Item> getRequiredItems() {
        return new ArrayList<>(requiredItems);
    }

    public boolean matches(Item assembledItem) {
        if (!(assembledItem instanceof Dish)) {
            return false;
        }

        Dish dish = (Dish) assembledItem;
        List<Item> components = dish.getComponents();

        if (components.size() != requiredItems.size()) {
            return false;
        }

        boolean[] usedComponents = new boolean[components.size()];

        for (Item requiredItem : requiredItems) {
            boolean foundMatch = false;

            for (int i = 0; i < components.size(); i++) {
                if (usedComponents[i]) {
                    continue;
                }

                Item component = components.get(i);

                if (itemsMatch(requiredItem, component)) {
                    usedComponents[i] = true;
                    foundMatch = true;
                    break;
                }
            }

            if (!foundMatch) {
                return false;
            }
        }

        return true;
    }

    private boolean itemsMatch(Item required, Item actual) {
        if (!required.getName().equalsIgnoreCase(actual.getName())) {
            return false;
        }

        if (required instanceof Ingredient requiredIngredient) {
            if (!(actual instanceof Ingredient actualIngredient)) {
                return false;
            }
            return requiredIngredient.getState() == actualIngredient.getState();
        }

        return true;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Recipe: ").append(name).append("\n");
        sb.append("Required Items:\n");
        for (Item item : requiredItems) {
            sb.append("  - ").append(item.getDisplayName()).append("\n");
        }
        return sb.toString();
    }
}