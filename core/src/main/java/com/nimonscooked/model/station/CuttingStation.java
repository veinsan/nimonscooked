package com.nimonscooked.model.station;

import com.nimonscooked.model.Item;
import com.nimonscooked.model.ingredient.Ingredient;

public class CuttingStation extends Station {

    public CuttingStation(String id) {
        super(id);
    }

    public Item process(Item input) {
        if (!(input instanceof Ingredient)) {
            return null;
        }

        Ingredient ingredient = (Ingredient) input;

        if (ingredient.getState() != Ingredient.State.RAW) {
            return null;
        }

        return new Ingredient(ingredient.getName(), Ingredient.State.CHOPPED);
    }
}