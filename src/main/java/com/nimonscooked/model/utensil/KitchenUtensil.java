package com.nimonscooked.model.utensil;

import com.nimonscooked.model.Item;
import com.nimonscooked.model.ingredient.Preparable;
import java.util.ArrayList;
import java.util.List;

public abstract class KitchenUtensil extends Item {
    protected List<Preparable> contents;

    public KitchenUtensil(String name) {
        super(name);
        this.contents = new ArrayList<>();
    }

    public List<Preparable> getContents() {
        return new ArrayList<>(contents);
    }

    public void addContent(Preparable ingredient) {
        contents.add(ingredient);
    }
    
    public void clearContents() {
        contents.clear();
    }
    
    public boolean isEmpty() {
        return contents.isEmpty();
    }
}