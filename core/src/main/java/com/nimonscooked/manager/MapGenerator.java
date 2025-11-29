package com.nimonscooked.manager;

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
        
        // Reset Chef
        manager.chefs.clear();
        
        // 1. Inisialisasi Lantai & Tembok Pinggir
        for (int x = 0; x < WIDTH; x++) {
            for (int y = 0; y < HEIGHT; y++) {
                if (x == 0 || x == WIDTH - 1 || y == 0 || y == HEIGHT - 1) {
                    map.setTile(x, y, new Tile(Tile.TileType.WALL, 'X'));
                } else {
                    map.setTile(x, y, new Tile(Tile.TileType.FLOOR, '.'));
                }
            }
        }

        // 2. Daftar Station Wajib untuk Map C (Burger)
        List<Character> stations = new ArrayList<>();
        // Komposisi agar tidak macet:
        addStations(stations, 'R', 4); // Cooking (Stove)
        addStations(stations, 'C', 2); // Cutting
        addStations(stations, 'A', 4); // Assembly (Meja kosong)
        addStations(stations, 'W', 2); // Washing
        addStations(stations, 'S', 1); // Serving
        addStations(stations, 'T', 1); // Trash
        addStations(stations, 'P', 1); // Plate Storage
        addStations(stations, 'I', 5); // Ingredients (Bun, Meat, Cheese, Tomato, Lettuce)

        // Acak urutan station
        Collections.shuffle(stations);

        // 3. Tempatkan Station di Grid (Cari tempat kosong secara acak)
        for (Character symbol : stations) {
            placeStationRandomly(map, symbol);
        }

        // 4. Spawn 2 Chef di posisi aman (Lantai kosong)
        spawnChefRandomly(map, manager);
        spawnChefRandomly(map, manager);
        
        if (!manager.chefs.isEmpty()) {
            manager.activeChef = manager.chefs.get(0);
        }

        manager.currentMap = map;
        System.out.println("Random Level Generated (Map C Variant)!");
    }

    private static void addStations(List<Character> list, char symbol, int count) {
        for (int i = 0; i < count; i++) list.add(symbol);
    }

    private static void placeStationRandomly(GridMap map, char symbol) {
        int attempts = 0;
        while (attempts < 100) {
            // Hindari pinggir tembok (x=1..12, y=1..8)
            int x = random.nextInt(WIDTH - 2) + 1;
            int y = random.nextInt(HEIGHT - 2) + 1;

            Tile t = map.getTile(x, y);
            // Syarat: Harus lantai kosong dan belum ada station
            if (t.getType() == Tile.TileType.FLOOR && t.getStation() == null) {
                Station s = StationFactory.createStation(symbol, x, y);
                if (s != null) {
                    Tile newTile = new Tile(Tile.TileType.STATION, symbol);
                    newTile.setStation(s);
                    map.setTile(x, y, newTile);
                    return; // Berhasil taruh
                }
            }
            attempts++;
        }
    }
    
    private static void spawnChefRandomly(GridMap map, MapManager manager) {
        int attempts = 0;
        while (attempts < 100) {
            int x = random.nextInt(WIDTH - 2) + 1;
            int y = random.nextInt(HEIGHT - 2) + 1;
            Tile t = map.getTile(x, y);
            if (t.getType() == Tile.TileType.FLOOR && t.getStation() == null) {
                manager.chefs.add(new Chef(x, y));
                return;
            }
            attempts++;
        }
    }
}