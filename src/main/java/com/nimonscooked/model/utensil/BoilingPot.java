package com.nimonscooked.model.utensil;

import com.nimonscooked.interfaces.CookingDevice;
import com.nimonscooked.interfaces.Preparable;

import java.util.ArrayList;
import java.util.List;

public class BoilingPot extends KitchenUtensil implements CookingDevice {

    private int cap;

    public BoilingPot(String name, int cap){
        super(name);
        this.cap = cap;
    }

    public boolean isPortable(){
        return true;
    }

    public int capacity() { return cap; }

    public boolean canAccept(Preparable ingredient){
        return false; //tbf ini kelas ga kepake jd diginiin dulu aja
    }

    public void addIngredient(Preparable ingredient){
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
