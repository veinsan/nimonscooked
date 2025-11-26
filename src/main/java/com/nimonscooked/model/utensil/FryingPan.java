package com.nimonscooked.model.utensil;
import com.nimonscooked.interfaces.CookingDevice;
import com.nimonscooked.interfaces.Preparable;


public class FryingPan extends KitchenUtensil implements CookingDevice {
    private int cap;

    public FryingPan(String name, int cap) {
        super(name);
        this.cap = cap;
    }

    public boolean isPortable(){ return true; }

    public int capacity() { return cap; }

    public boolean canAccept(Preparable ingredient){
        return ingredient.getState() == Preparable.State.CHOPPED;
    }

    public void addIngredient(Preparable ingredient) {
        if (canAccept(ingredient)) {
            contents.add(ingredient);
        }
    }

    public void startCooking() {
        for (Preparable p : contents) {
            p.cook();
        }
    }
}
