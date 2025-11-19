package com.nimonscooked.controller;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.nimonscooked.map.GameMap;
import com.nimonscooked.map.Tile;
import com.nimonscooked.map.TileType;
import com.nimonscooked.model.chef.Chef;
import com.nimonscooked.model.chef.Position;
import com.nimonscooked.model.station.*;
import com.nimonscooked.model.recipe.Recipe;
import com.nimonscooked.model.utensil.FryingPan;

public class MapParser {

    private static final int MAP_ROWS = 10;
    private static final int MAP_COLS = 14;

    public static GameMap loadTypeCMap(String resourcePath, Chef chef1, Chef chef2, List<Recipe> menu) {
        GameMap map = new GameMap(MAP_ROWS, MAP_COLS);
        List<Position> chefSpawnPositions = new ArrayList<>();
        List<CookingStation> cookingStations = new ArrayList<>();

        try {
            InputStream inputStream = Gdx.files.internal(resourcePath).read();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            int currentRow = 0;
            int ingredientStorageCount = 0;

            while ((line = reader.readLine()) != null && currentRow < MAP_ROWS) {
                if (line.length() < MAP_COLS) {
                    line = String.format("%-" + MAP_COLS + "s", line);
                }

                for (int currentCol = 0; currentCol < MAP_COLS && currentCol < line.length(); currentCol++) {
                    char symbol = line.charAt(currentCol);
                    Tile tile = createTileFromSymbol(
                        symbol, currentRow, currentCol, menu, 
                        chefSpawnPositions, ingredientStorageCount, cookingStations
                    );
                    
                    if (symbol == 'I') {
                        ingredientStorageCount++;
                    }

                    map.setTile(currentRow, currentCol, tile);
                }
                currentRow++;
            }

            reader.close();

            int fryingPansToPlace = Math.min(4, cookingStations.size());
            for (int i = 0; i < fryingPansToPlace; i++) {
                CookingStation station = cookingStations.get(i);
                station.placeCookingDevice(new FryingPan());
            }

            setChefSpawnPositions(chef1, chef2, chefSpawnPositions);

        } catch (Exception e) {
            System.err.println("❌ Error loading map: " + e.getMessage());
            e.printStackTrace();
        }

        return map;
    }

    private static Tile createTileFromSymbol(
        char symbol, int row, int col, List<Recipe> menu,
        List<Position> spawnPositions, int ingredientCount, 
        List<CookingStation> cookingStations
    ) {
        Tile tile;
        String stationId = generateStationId(symbol, row, col);

        switch (symbol) {
            case 'X':
                tile = new Tile(TileType.WALL, 'X');
                break;

            case '.':
                tile = new Tile(TileType.FLOOR, '.');
                break;

            case 'V':
                tile = new Tile(TileType.FLOOR, '.');
                spawnPositions.add(new Position(row, col));
                break;

            case 'C':
                tile = new Tile(TileType.STATION, 'C');
                tile.setStation(new CuttingStation(stationId));
                break;

            case 'R':
                tile = new Tile(TileType.STATION, 'R');
                CookingStation cookingStation = new CookingStation(stationId);
                tile.setStation(cookingStation);
                cookingStations.add(cookingStation);
                break;

            case 'A':
                tile = new Tile(TileType.STATION, 'A');
                tile.setStation(new AssemblyStation(stationId, menu));
                break;

            case 'I':
                String ingredientType = determineIngredientType(row, col, ingredientCount);
                tile = new Tile(TileType.STATION, 'I');
                tile.setStation(new IngredientStorage(stationId, ingredientType));
                break;

            case 'P':
                tile = new Tile(TileType.STATION, 'P');
                tile.setStation(new PlateStorage(stationId));
                break;

            case 'S':
                tile = new Tile(TileType.STATION, 'S');
                tile.setStation(new ServingCounter(stationId));
                break;

            case 'W':
                tile = new Tile(TileType.STATION, 'W');
                tile.setStation(new WashingStation(stationId));
                break;

            case 'T':
                tile = new Tile(TileType.STATION, 'T');
                tile.setStation(new TrashStation(stationId));
                break;

            default:
                tile = new Tile(TileType.FLOOR, '.');
                break;
        }

        return tile;
    }

    private static String determineIngredientType(int row, int col, int count) {
        if (row == 0 && col == 7) return "bread";
        if (row == 2 && col == 0) return "meat";
        if (row == 4 && col == 0) return "cheese";
        if (row == 6 && col == 0) return "lettuce";
        if (row == 8 && col == 7) return "tomato";

        String[] types = {"bread", "meat", "cheese", "lettuce", "tomato"};
        return types[count % types.length];
    }

    private static String generateStationId(char symbol, int row, int col) {
        String prefix;
        switch (symbol) {
            case 'C': prefix = "cut"; break;
            case 'R': prefix = "cook"; break;
            case 'A': prefix = "asm"; break;
            case 'I': prefix = "ing"; break;
            case 'P': prefix = "plate"; break;
            case 'S': prefix = "serve"; break;
            case 'W': prefix = "wash"; break;
            case 'T': prefix = "trash"; break;
            default: prefix = "unknown"; break;
        }
        return prefix + "_" + row + "_" + col;
    }

    private static void setChefSpawnPositions(Chef chef1, Chef chef2, List<Position> spawnPositions) {
        if (spawnPositions.size() >= 2) {
            chef1.setPosition(spawnPositions.get(0));
            chef2.setPosition(spawnPositions.get(1));
        } else if (spawnPositions.size() == 1) {
            chef1.setPosition(spawnPositions.get(0));
            chef2.setPosition(new Position(5, 5));
        } else {
            chef1.setPosition(new Position(3, 3));
            chef2.setPosition(new Position(6, 6));
        }
    }
}