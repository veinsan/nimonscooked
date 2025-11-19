package com.nimonscooked.model.station;

import com.nimonscooked.model.Item;
import com.nimonscooked.model.utensil.Plate;

public class WashingStation extends Station {

    public WashingStation(String id) {
        super(id);
    }

    public Item wash(Item input) {
        if (!(input instanceof Plate)) {
            return input;
        }

        Plate plate = (Plate) input;
        plate.setClean(true);

        if (plate.getContainedDish() != null) {
            plate.setContainedDish(null);
        }

        return plate;
    }

    @Override
    public String toString() {
        return "WashingStation[" + id + "]";
    }
}