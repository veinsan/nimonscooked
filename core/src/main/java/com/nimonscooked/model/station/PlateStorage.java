package com.nimonscooked.model.station;

import com.nimonscooked.model.utensil.Plate;

public class PlateStorage extends Station {

    public PlateStorage(String id) {
        super(id);
    }

    public Plate getPlate() {
        return new Plate();
    }

    @Override
    public String toString() {
        return "PlateStorage[" + id + "]";
    }
}