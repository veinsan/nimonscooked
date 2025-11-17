package com.nimonscooked.model.dish;

import java.util.ArrayList;
import java.util.List;
import com.nimonscooked.model.Item;
import com.nimonscooked.model.ingredient.Preparable;

public class Dish extends Item {
    private List<Preparable> components;

    public Dish(String name, List<Preparable> components) {
        super(name);
        this.components = new ArrayList<>(components);
    }

    public List<Preparable> getComponents() {
        return new ArrayList<>(components);
    }
    
    public void addComponent(Preparable ingredient) {
        this.components.add(ingredient);
    }
}