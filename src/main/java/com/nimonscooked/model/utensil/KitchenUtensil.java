package com.nimonscooked.model.utensil;

import com.nimonscooked.interfaces.CookingDevice;
import com.nimonscooked.interfaces.Preparable;
import com.nimonscooked.model.item.Item;

import java.util.ArrayList;
import java.util.List;

public abstract class KitchenUtensil extends Item {

    protected List<Preparable> contents;
    protected int cap;

    public KitchenUtensil(String name) {
        super(name);
        contents = new ArrayList<>(cap);
    }
}
