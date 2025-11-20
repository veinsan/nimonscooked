package com.nimonscooked.exception;

public class StationFullException extends GameException {
    
    private final String stationId;
    private final String stationType;

    public StationFullException(String stationId, String stationType) {
        super("Station '" + stationId + "' (" + stationType + ") is full!");
        this.stationId = stationId;
        this.stationType = stationType;
    }

    public StationFullException(String message) {
        super(message);
        this.stationId = "Unknown";
        this.stationType = "Unknown";
    }

    public String getStationId() {
        return stationId;
    }

    public String getStationType() {
        return stationType;
    }

    @Override
    public String toString() {
        return "StationFullException: Station='" + stationId + "' Type='" + stationType + "'";
    }
}