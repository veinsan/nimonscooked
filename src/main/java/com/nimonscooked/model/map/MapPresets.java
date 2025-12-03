package com.nimonscooked.model.map;

public final class MapPresets {

    private MapPresets() {}

    public static GameMap createBurgerMap() {
        String[] layout = {
                "AARRAAXXXXXXXX", // row 0 (baris 1)
                "I....AXXX....W", // row 1
                "I....AXXX....W", // row 2
                "I.V..AXXX....A", // row 3
                "A....XXXX....R", // row 4
                "P....XXXC....R", // row 5
                "S....XXXC..V.I", // row 6
                "S....XXXA....I", // row 7
                "A............T", // row 8
                "XXXXXXXXXXXXXX"  // row 9 (baris 10)
        };

        int rows = layout.length;
        int cols = layout[0].length();
        GameMap map = new GameMap(rows, cols);

        for (int r = 0; r < rows; r++) {
            String line = layout[r];
            if (line.length() != cols) {
                throw new IllegalStateException("Panjang baris " + r + " tidak konsisten");
            }
            for (int c = 0; c < cols; c++) {
                char ch = line.charAt(c);
                TileType type = charToTileType(ch);
                Tile tile = new Tile(type, ch);
                map.setTile(r, c, tile);
            }
        }

        return map;
    }

    private static TileType charToTileType(char ch) {
        switch (ch) {
            case 'X': return TileType.WALL;
            case '.': return TileType.FLOOR;
            case 'V': return TileType.SPAWN;
            case 'C': // Cutting
            case 'R': // Cooking
            case 'A': // Assembly
            case 'I': // Ingredient
            case 'S': // Serving
            case 'W': // Washing
            case 'P': // Plate
            case 'T': // Trash
                return TileType.STATION;
            default:
                throw new IllegalArgumentException("Simbol tile tidak dikenal: '" + ch + "'");
        }
    }
}
