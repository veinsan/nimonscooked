package com.nimonscooked.manager;

import com.badlogic.gdx.Gdx;
import com.nimonscooked.factory.StationFactory;
import com.nimonscooked.model.entity.Chef;
import com.nimonscooked.model.map.GridMap;
import com.nimonscooked.model.map.Tile;
import com.nimonscooked.model.station.Station;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class MapGenerator {
    private static final int WIDTH = 14;
    private static final int HEIGHT = 10;
    private static final Random random = new Random();

    public static void generateRandomMapC() {
        MapManager manager = MapManager.getInstance();
        GridMap map = new GridMap(WIDTH, HEIGHT);

        manager.chefs.clear();

        for (int x = 0; x < WIDTH; x++) {
            for (int y = 0; y < HEIGHT; y++) {
                if (x == 0 || x == WIDTH - 1 || y == 0 || y == HEIGHT - 1) {
                    map.setTile(x, y, new Tile(Tile.TileType.WALL, 'X'));
                } else {
                    map.setTile(x, y, new Tile(Tile.TileType.FLOOR, '.'));
                }
            }
        }

        List<Character> stations = new ArrayList<>();
        addStations(stations, 'R', 4);
        addStations(stations, 'C', 2);
        addStations(stations, 'A', 4);
        addStations(stations, 'W', 2);
        addStations(stations, 'S', 1);
        addStations(stations, 'T', 1);
        addStations(stations, 'P', 1);

        Collections.shuffle(stations);

        for (Character symbol : stations) {
            placeStationRandomly(map, symbol);
        }

        placeStrategicIngredient(map, 1, 4, 0, 3);
        placeStrategicIngredient(map, 1, 4, 4, 5);
        placeStrategicIngredient(map, 1, 4, 6, 8);

        placeStrategicIngredient(map, 5, 12, 0, 4);
        placeStrategicIngredient(map, 5, 12, 5, 8);

        spawnChefRandomly(map, manager);
        spawnChefRandomly(map, manager);

        if (!manager.chefs.isEmpty()) {
            manager.activeChef = manager.chefs.get(0);
        }

        manager.currentMap = map;
        Gdx.app.log("MapGenerator", "Smart Random Level Generated (Valid Burger Map)!");
    }

    private static void addStations(List<Character> list, char symbol, int count) {
        for (int i = 0; i < count; i++) list.add(symbol);
    }

    private static void placeStationRandomly(GridMap map, char symbol) {
        int attempts = 0;
        while (attempts < 100) {
            int x = random.nextInt(WIDTH - 2) + 1;
            int y = random.nextInt(HEIGHT - 2) + 1;

            Tile t = map.getTile(x, y);
            if (t.getType() == Tile.TileType.FLOOR && t.getStation() == null) {
                createAndSetStation(map, x, y, symbol);
                return;
            }
            attempts++;
        }
    }

    private static void placeStrategicIngredient(GridMap map, int minX, int maxX, int minY, int maxY) {
        int attempts = 0;
        while (attempts < 100) {
            int x = random.nextInt(maxX - minX + 1) + minX;
            int y = random.nextInt(maxY - minY + 1) + minY;

            if (x <= 0 || x >= WIDTH - 1 || y <= 0 || y >= HEIGHT - 1) continue;

            Tile t = map.getTile(x, y);
            if (t.getType() == Tile.TileType.FLOOR && t.getStation() == null) {
                createAndSetStation(map, x, y, 'I');
                return;
            }
            attempts++;
        }
        Gdx.app.error("MapGenerator", "Failed to place ingredient in zone: X[" + minX + "-" + maxX + "] Y[" + minY + "-" + maxY + "]");
    }

    private static void createAndSetStation(GridMap map, int x, int y, char symbol) {
        Station s = StationFactory.createStation(symbol, x, y);
        if (s != null) {
            Tile newTile = new Tile(Tile.TileType.STATION, symbol);
            newTile.setStation(s);
            map.setTile(x, y, newTile);
        }
    }

    private static void spawnChefRandomly(GridMap map, MapManager manager) {
        int attempts = 0;
        while (attempts < 100) {
            int x = random.nextInt(WIDTH - 2) + 1;
            int y = random.nextInt(HEIGHT - 2) + 1;
            Tile t = map.getTile(x, y);

            if (t.getType() == Tile.TileType.FLOOR && t.getStation() == null && !map.isOccupiedByChef(x, y)) {
                Chef.Type type = (manager.chefs.size() % 2 == 0) ? Chef.Type.CHEF_A : Chef.Type.CHEF_B;
                manager.chefs.add(new Chef(x, y, type));
                return;
            }
            attempts++;
        }
    }
}