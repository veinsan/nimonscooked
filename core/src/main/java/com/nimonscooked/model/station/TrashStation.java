package com.nimonscooked.model.station;

public class TrashStation extends Station {
    
    public TrashStation(String id) {
        super(id);
    }

    @Override
    public String toString() {
        return "TrashStation[" + id + "]";
    }
}