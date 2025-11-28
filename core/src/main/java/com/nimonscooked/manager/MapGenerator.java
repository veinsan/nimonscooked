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
        for (int i = 0; i < 4; i++) stations.add('R');
        for (int i = 0; i < 2; i++) stations.add('C');
        for (int i = 0; i < 4; i++) stations.add('A');
        for (int i = 0; i < 2; i++) stations.add('W');
        stations.add('S');
        stations.add('T');
        stations.add('P');
        for (int i = 0; i < 5; i++) stations.add('I');

        Collections.shuffle(stations);

        for (Character symbol : stations) {
            placeStation(map, symbol);
        }

        manager.chefs.add(new Chef(2, 2));
        manager.chefs.add(new Chef(3, 2));
        manager.activeChef = manager.chefs.get(0);

        manager.currentMap = map;
    }

    private static void placeStation(GridMap map, char symbol) {
        int attempts = 0;
        while (attempts < 100) {
            int x = random.nextInt(WIDTH - 2) + 1;
            int y = random.nextInt(HEIGHT - 2) + 1;
            Tile t = map.getTile(x, y);
            if (t.getType() == Tile.TileType.FLOOR && t.getStation() == null) {
                Station s = StationFactory.createStation(symbol, x, y);
                if (s != null) {
                    t = new Tile(Tile.TileType.STATION, symbol);
                    t.setStation(s);
                    map.setTile(x, y, t);
                    return;
                }
            }
            attempts++;
        }
    }
}