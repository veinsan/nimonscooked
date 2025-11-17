package com.nimonscooked.model.dish;

import java.util.ArrayList;
import java.util.List;
import com.nimonscooked.model.Item;

public class Dish extends Item {
    private List<Item> components;

    public Dish(String name, List<Item> components) {
        super(name);
        this.components = new ArrayList<>(components);
    }

    public List<Item> getComponents() {
        return new ArrayList<>(components);
    }
    
    public void addComponent(Item item) {
        this.components.add(item);
    }
}