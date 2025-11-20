package com.nimonscooked.strategy;

import com.nimonscooked.interfaces.Preparable;
import com.nimonscooked.model.ingredient.Ingredient;
import com.nimonscooked.model.ingredient.IngredientState;

public class BakingStrategy implements CookingStrategy {
    
    @Override
    public boolean canCook(Preparable ingredient) {
        if (!(ingredient instanceof Ingredient)) {
            return false;
        }
        Ingredient ing = (Ingredient) ingredient;
        String name = ing.getName().toLowerCase();
        return (name.contains("pizza") || name.contains("dough") || name.contains("adonan"))
               && ing.getState() == IngredientState.CHOPPED;
    }

    @Override
    public void cook(Preparable ingredient) {
        if (canCook(ingredient)) {
            ingredient.cook();
            System.out.println("🍕 Baking: " + ((Ingredient) ingredient).getName());
        }
    }

    @Override
    public String getMethodName() {
        return "Baking";
    }
}