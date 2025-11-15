package com.nimonscooked.model.station;

public abstract class Station {
    private String id;

    public Station(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }
}
