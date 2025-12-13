package com.nimonscooked.model.dish;

import com.nimonscooked.model.item.Item;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Dish extends Item {
    private final List<Item> components;
    private final int componentCount;

    public Dish(String name, List<Item> components) {
        super(name, "ingredients/burger_random.png");
        this.components = new ArrayList<>(components);
        this.componentCount = components.size();
    }

    public void setMatchedRecipe(String recipeName) {
        this.name = recipeName;
        this.textureName = getTextureForRecipe(recipeName);
    }

    private String getTextureForRecipe(String recipeName) {
        String lower = recipeName.toLowerCase();
        
        if (lower.contains("classic")) {
            return "ingredients/classic_burger.png";
        } else if (lower.contains("cheese") && !lower.contains("deluxe")) {
            return "ingredients/cheese_burger.png";
        } else if (lower.contains("blt")) {
            return "ingredients/blt_burger.png";
        } else if (lower.contains("deluxe")) {
            return "ingredients/deluxe_burger.png";
        }
        
        return "ingredients/burger_random.png";
    }

    public List<Item> getComponents() {
        return Collections.unmodifiableList(components);
    }

    public int getComponentCount() {
        return componentCount;
    }

    public boolean isEmpty() {
        return componentCount == 0;
    }

    public boolean containsIngredient(String ingredientName) {
        for (Item item : components) {
            if (item.getName().equalsIgnoreCase(ingredientName)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String getDisplayName() {
        return name + " (" + componentCount + " items)";
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(name).append(" [");
        for (int i = 0; i < components.size(); i++) {
            if (i > 0) sb.append(", ");
            sb.append(components.get(i).getName());
        }
        sb.append("]");
        return sb.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Dish)) return false;
        
        Dish other = (Dish) obj;
        if (componentCount != other.componentCount) return false;
        if (!name.equalsIgnoreCase(other.name)) return false;
        
        return components.equals(other.components);
    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + componentCount;
        result = 31 * result + components.hashCode();
        return result;
    }
}