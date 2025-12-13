package com.nimonscooked.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.nimonscooked.factory.StationFactory;
import com.nimonscooked.model.entity.Chef;
import com.nimonscooked.model.map.GridMap;
import com.nimonscooked.model.map.Tile;
import com.nimonscooked.model.station.Station;

public class MapManager {
    private static MapManager instance;
    
    public GridMap currentMap;
    public List<Chef> chefs;
    public Chef activeChef;

    private Map<String, Station> stationRegistry;
    private String currentMapFile;

    private MapManager() {
        chefs = new ArrayList<>();
        stationRegistry = new HashMap<>();
    }

    public static MapManager getInstance() {
        if (instance == null) {
            instance = new MapManager();
        }
        return instance;
    }
    private void cleanupCurrentMap() {
        // 1. Stop Chefs (cancel interactions)
        for (Chef c : chefs) {
            c.interruptAction();
            c.setInventory(null);
        }
        
        // 2. Stop Stations (stop frying pan sounds/threads)
        for (Station s : stationRegistry.values()) {
            if (s.getItem() instanceof com.nimonscooked.model.utensil.CookingDevice) {
                ((com.nimonscooked.model.utensil.CookingDevice) s.getItem()).stopCooking();
            }
        }

        chefs.clear();
        stationRegistry.clear();
    }
    public void loadMap(String fileName) {
        // [NEW] Cleanup existing state before loading the new one
        cleanupCurrentMap();

        currentMapFile = fileName;

        FileHandle file = Gdx.files.internal(fileName);
        if (!file.exists()) {
            Gdx.app.error("MapManager", "FILE NOT FOUND: " + fileName);
            createDefaultMap();
            return;
        }

        String[] lines = file.readString().split("\\r?\\n");
        int height = 0;
        int width = 0;

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
                int visualGridY = height - 1 - row;

                Tile.TileType type = determineTileType(c);
                Tile tile = new Tile(type, c);
                currentMap.setTile(gridX, visualGridY, tile);

                if (c == 'V') {
                    spawnChef(gridX, visualGridY);
                } else if (StationFactory.isStationSymbol(c)) {
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
        } else {
            Gdx.app.error("MapManager", "No chefs spawned! Creating default chef.");
            spawnChef(1, 1);
            activeChef = chefs.get(0);
        }

        Gdx.app.log("MapManager", "Map loaded/reset: " + fileName);
    }

    private Tile.TileType determineTileType(char symbol) {
        if (symbol == 'X') {
            return Tile.TileType.WALL;
        } else if (symbol == '.' || symbol == 'V') {
            return Tile.TileType.FLOOR;
        } else if (StationFactory.isStationSymbol(symbol)) {
            return Tile.TileType.STATION;
        }
        return Tile.TileType.FLOOR;
    }

    private void spawnChef(int col, int row) {
        Chef.Type type = (chefs.size() % 2 == 0) ? Chef.Type.CHEF_A : Chef.Type.CHEF_B;
        
        Chef newChef = new Chef(col, row, type);
        
        chefs.add(newChef);
        Gdx.app.log("MapManager", "Spawned " + type + " at (" + col + ", " + row + ")");
    }

    private void createDefaultMap() {
        Gdx.app.log("MapManager", "Creating default fallback map");
        currentMap = new GridMap(10, 10);
        
        for (int x = 0; x < 10; x++) {
            for (int y = 0; y < 10; y++) {
                Tile tile = new Tile(Tile.TileType.FLOOR, '.');
                currentMap.setTile(x, y, tile);
            }
        }
        
        spawnChef(5, 5);
        activeChef = chefs.get(0);
    }

    public void reloadCurrentMap() {
        if (currentMapFile != null) {
            loadMap(currentMapFile);
        }
    }

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

    public int getStationCount() {
        return stationRegistry.size();
    }

    public void dispose() {
        for (Station station : stationRegistry.values()) {
            if (station instanceof com.badlogic.gdx.utils.Disposable) {
                ((com.badlogic.gdx.utils.Disposable) station).dispose();
            }
        }
        stationRegistry.clear();
        chefs.clear();
        activeChef = null;
    }
}