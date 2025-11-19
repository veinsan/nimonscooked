package com.nimonscooked.model.recipe;

import java.util.ArrayList;
import java.util.List;
import com.nimonscooked.model.Item;

public class Recipe {
    private String name;
    private List<Item> requiredItems;

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
}