package com.nimonscooked.renderer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.nimonscooked.map.GameMap;
import com.nimonscooked.map.Tile;
import com.nimonscooked.map.TileType;

public class TileRenderer {

    private Texture tilesheet;
    private TextureRegion[][] regions;

    private static final int TILE_SIZE = 16;
    private static final int RENDER_SIZE = 48;

    public TileRenderer() {

        // Cek file biar ga silent crash
        if (!Gdx.files.internal("tiles/Interiors_free_16x16.png").exists()) {
            throw new RuntimeException("ERROR: tiles/Interiors_free_16x16.png NOT FOUND!");
        }

        tilesheet = new Texture("tiles/Interiors_free_16x16.png");
        regions = TextureRegion.split(tilesheet, TILE_SIZE, TILE_SIZE);
    }

    public void render(SpriteBatch batch, GameMap map) {
        for (int r = 0; r < map.getRows(); r++) {
            for (int c = 0; c < map.getCols(); c++) {

                Tile tile = map.getTile(r, c);

                TextureRegion region = safeRegion(getRegion(tile));

                batch.draw(
                    region,
                    c * RENDER_SIZE,
                    (map.getRows() - r - 1) * RENDER_SIZE,
                    RENDER_SIZE,
                    RENDER_SIZE
                );
            }
        }
    }

    /** 
     * Kalau region null → fallback, biar GA NULL POINTER
     */
    private TextureRegion safeRegion(TextureRegion region) {
        if (region == null) return regions[0][0];
        return region;
    }

    private TextureRegion getRegion(Tile tile) {
        switch (tile.getType()) {
            case FLOOR:   return getSafe(10, 0);
            case WALL:    return getSafe(5, 8);
            case STATION: return getStation(tile.getSymbol());
            default:      return getSafe(0, 0);
        }
    }

    private TextureRegion getStation(char s) {
        switch (s) {
            case 'C': case 'c': return getSafe(12, 3); // Cutting
            case 'R': case 'r': return getSafe(12, 5); // Stove
            case 'A': case 'a': return getSafe(13, 1); // Assembly
            case 'I': case 'i': return getSafe(11, 7); // Ingredient
            case 'P': case 'p': return getSafe(9, 4);  // Plate storage
            case 'S': case 's': return getSafe(8, 6);  // Serving
            case 'W': case 'w': return getSafe(14, 0); // Washing
            case 'T': case 't': return getSafe(14, 3); // Trash
            default:            return getSafe(0, 0);
        }
    }

    /** 
     * Anti OOB index — kalau asset beda grid tidak crash.
     */
    private TextureRegion getSafe(int row, int col) {
        if (row < 0 || row >= regions.length) return regions[0][0];
        if (col < 0 || col >= regions[row].length) return regions[0][0];
        return regions[row][col];
    }

    public void dispose() {
        tilesheet.dispose();
    }
}
