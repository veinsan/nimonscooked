package com.nimonscooked.model.dish;

import java.util.ArrayList;
import java.util.List;
import com.nimonscooked.model.Item;

public class Dish extends Item {
    private final List<Item> components;

    public Dish(String name, List<Item> components) {
        super(name);
        this.components = new ArrayList<>(components);
    }

    public List<Item> getComponents() {
        return new ArrayList<>(components);
    }

    public int getComponentCount() {
        return components.size();
    }

    @Override
    public String getDisplayName() {
        return name + " (" + components.size() + " items)";
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
}