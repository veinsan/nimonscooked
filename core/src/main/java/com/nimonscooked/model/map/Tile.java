package com.nimonscooked.model.map;

import com.nimonscooked.model.station.Station;

public class Tile {
    public enum TileType { 
        FLOOR("Floor", true), 
        WALL("Wall", false), 
        STATION("Station", false), 
        EMPTY("Empty", false);

        private final String displayName;
        private final boolean walkableByDefault;

        TileType(String displayName, boolean walkableByDefault) {
            this.displayName = displayName;
            this.walkableByDefault = walkableByDefault;
        }

        public String getDisplayName() {
            return displayName;
        }

        public boolean isWalkableByDefault() {
            return walkableByDefault;
        }
    }

    private final TileType type;
    private final char symbol;
    private Station station;

    public Tile(TileType type, char symbol) {
        if (type == null) {
            throw new IllegalArgumentException("TileType cannot be null");
        }
        
        this.type = type;
        this.symbol = symbol;
        this.station = null;
    }

    public TileType getType() {
        return type;
    }

    public boolean isWalkable() {
        return type.isWalkableByDefault();
    }

    public char getSymbol() {
        return symbol;
    }

    public void setStation(Station station) {
        this.station = station;
    }

    public Station getStation() {
        return station;
    }

    public boolean hasStation() {
        return station != null;
    }

    public boolean isFloor() {
        return type == TileType.FLOOR;
    }

    public boolean isWall() {
        return type == TileType.WALL;
    }

    public boolean isStation() {
        return type == TileType.STATION;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Tile[").append(type.getDisplayName());
        sb.append(", '").append(symbol).append("'");
        if (station != null) {
            sb.append(", station=").append(station.getId());
        }
        sb.append("]");
        return sb.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Tile)) return false;
        
        Tile other = (Tile) obj;
        return type == other.type && symbol == other.symbol;
    }

    @Override
    public int hashCode() {
        return type.hashCode() * 31 + Character.hashCode(symbol);
    }
}