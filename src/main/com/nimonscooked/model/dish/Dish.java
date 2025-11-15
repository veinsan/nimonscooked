package com.nimonscooked.model.dish;

import java.util.List;
import com.nimonscooked.model.Item;

public class Dish {
    private String name;
    private List<Item> components;

    public Dish(String name, List<Item> components) {
        this.name = name;
        this.components = components;
    }

    public String getName() {
        return name;
    }

    public List<Item> getComponents() {
        return components;
    }
}
