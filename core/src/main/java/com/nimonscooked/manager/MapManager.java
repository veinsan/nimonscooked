package com.nimonscooked.manager;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.nimonscooked.model.entity.Chef;
import com.nimonscooked.model.map.GridMap;
import com.nimonscooked.model.map.Tile;
import java.util.ArrayList;
import java.util.List;

public class MapManager {
    private static final MapManager instance = new MapManager();
    public static MapManager getInstance() { return instance; }

    public GridMap currentMap;
    public List<Chef> chefs;
    public Chef activeChef;

    private MapManager() {
        chefs = new ArrayList<>();
    }

    public void loadMap(String fileName) {
        chefs.clear();

        FileHandle file = Gdx.files.internal(fileName);
        if (!file.exists()) {
            Gdx.app.error("MapManager", "FILE NOT FOUND: " + fileName);
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

                Tile.TileType type = Tile.TileType.FLOOR;
                if (c == 'X') type = Tile.TileType.WALL;
                else if (c == '.') type = Tile.TileType.FLOOR;
                else if (c == 'V') type = Tile.TileType.FLOOR;
                else type = Tile.TileType.STATION;

                Tile tile = new Tile(type, c);
                currentMap.setTile(gridX, visualGridY, tile);

                if (c == 'V') {
                    Chef newChef = new Chef(gridX, visualGridY);
                    chefs.add(newChef);
                }
            }
            row++;
        }

        if (!chefs.isEmpty()) activeChef = chefs.get(0);
        Gdx.app.log("MapManager", "Map Loaded! Chefs: " + chefs.size());
    }
}
