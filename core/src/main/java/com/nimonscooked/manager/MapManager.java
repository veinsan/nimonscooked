package com.nimonscooked.manager;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.nimonscooked.factory.StationFactory;
import com.nimonscooked.model.entity.Chef;
import com.nimonscooked.model.map.GridMap;
import com.nimonscooked.model.map.Tile;
import com.nimonscooked.model.station.Station;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * DESIGN PATTERN: Singleton Pattern
 * SOLID: Single Responsibility - Map loading & Chef management
 */
public class MapManager {
    private static final MapManager instance = new MapManager();

    public static MapManager getInstance() {
        return instance;
    }

    public GridMap currentMap;
    public List<Chef> chefs; // Sekarang List dikenal
    public Chef activeChef;

    // BARU: Station registry untuk lookup cepat
    private Map<String, Station> stationRegistry;

    private MapManager() {
        chefs = new ArrayList<>();
        stationRegistry = new HashMap<>();
    }

    public void loadMap(String fileName) {
        chefs.clear();
        stationRegistry.clear();

        FileHandle file = Gdx.files.internal(fileName);
        if (!file.exists()) {
            Gdx.app.error("MapManager", "FILE NOT FOUND: " + fileName);
            return;
        }

        String[] lines = file.readString().split("\\r?\\n");
        int height = 0;
        int width = 0;

        // Calculate dimensions
        for (String line : lines) {
            if (line.trim().length() > 0 && !line.startsWith("//")) {
                height++;
                width = Math.max(width, line.length());
            }
        }

        currentMap = new GridMap(width, height);

        int row = 0;
        for (String line : lines) {
            if (line.trim().length() == 0 || line.startsWith("//")) continue;

            for (int col = 0; col < line.length(); col++) {
                char c = line.charAt(col);
                int gridX = col;
                int visualGridY = height - 1 - row; // Flip Y axis untuk LibGDX

                // Determine tile type
                Tile.TileType type;
                if (c == 'X') {
                    type = Tile.TileType.WALL;
                } else if (c == '.' || c == 'V') {
                    type = Tile.TileType.FLOOR;
                } else if (StationFactory.isStationSymbol(c)) {
                    type = Tile.TileType.STATION;
                } else {
                    type = Tile.TileType.FLOOR; // Default
                }

                // Create tile
                Tile tile = new Tile(type, c);
                currentMap.setTile(gridX, visualGridY, tile);

                // Handle special tiles
                if (c == 'V') {
                    // Chef spawn point
                    Chef newChef = new Chef(gridX, visualGridY);
                    chefs.add(newChef);
                } else if (StationFactory.isStationSymbol(c)) {
                    // Create station using Factory Pattern
                    Station station = StationFactory.createStation(c, gridX, visualGridY);
                    if (station != null) {
                        tile.setStation(station);
                        stationRegistry.put(station.getId(), station);
                    }
                }
            }
            row++;
        }

        if (!chefs.isEmpty()) {
            activeChef = chefs.get(0);
        }

        Gdx.app.log("MapManager", "Map loaded! Chefs: " + chefs.size() +
                                  ", Stations: " + stationRegistry.size());
    }

    // ===== STATION LOOKUP =====
    public Station getStationAt(int col, int row) {
        if (!currentMap.isValid(col, row)) return null;
        Tile tile = currentMap.getTile(col, row);
        return tile != null ? tile.getStation() : null;
    }

    public Station getStationById(String id) {
        return stationRegistry.get(id);
    }

    public List<Station> getAllStations() {
        return new ArrayList<>(stationRegistry.values());
    }
}
