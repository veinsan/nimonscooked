package com.nimonscooked.view.renderer;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.nimonscooked.config.GameConfig;
import com.nimonscooked.manager.MapManager;
import com.nimonscooked.manager.ResourceManager;
import com.nimonscooked.model.entity.Chef;
import com.nimonscooked.model.map.GridMap;
import com.nimonscooked.model.map.Tile;
import com.nimonscooked.model.station.Station;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class WorldRenderer {

    private ResourceManager resourceManager;
    private MapManager mapManager;
    private List<Renderable> renderList;

    private Animation<TextureRegion> chefWalkAnim, chefIdleAnim, chefChopAnim;
    private static final float CHEF_SCALE = 1.8f;

    // Interface untuk sorting objek berdasarkan Y
    private interface Renderable extends Comparable<Renderable> {
        void render(SpriteBatch batch);
        float getY();
    }

    public WorldRenderer() {
        this.resourceManager = ResourceManager.getInstance();
        this.mapManager = MapManager.getInstance();
        this.renderList = new ArrayList<>();
        initAnimations();
    }

    private void initAnimations() {
        Texture walk = resourceManager.getTexture("chef/chef_walk.png");
        Texture idle = resourceManager.getTexture("chef/chef_idle.png");
        Texture chop = resourceManager.getTexture("chef/chef_chop.png");

        if(walk != null) chefWalkAnim = createAnimation(walk, 8, 0.1f);
        if(idle != null) chefIdleAnim = createAnimation(idle, 8, 0.15f);
        if(chop != null) chefChopAnim = createAnimation(chop, 8, 0.08f);
    }

    private Animation<TextureRegion> createAnimation(Texture sheet, int cols, float duration) {
        TextureRegion[][] tmp = TextureRegion.split(sheet, sheet.getWidth() / cols, sheet.getHeight());
        TextureRegion[] frames = new TextureRegion[cols];
        System.arraycopy(tmp[0], 0, frames, 0, cols);
        return new Animation<>(duration, frames);
    }

    public void render(SpriteBatch batch) {
        GridMap map = mapManager.currentMap;
        if (map == null) return;

        // 1. Render Floor (Lantai selalu di bawah)
        renderFloor(batch, map);

        // 2. Collect semua objek yang berdiri (Chefs & Stations)
        renderList.clear();
        collectStations(map);
        collectChefs();

        // 3. Sort berdasarkan Y (Objek "jauh" digambar duluan)
        Collections.sort(renderList);

        // 4. Render Sorted Objects
        for (Renderable r : renderList) {
            r.render(batch);
        }
    }

    private void renderFloor(SpriteBatch batch, GridMap map) {
        Texture floorTex = resourceManager.getTexture("stations/floor.png");
        int size = GameConfig.TILE_SIZE;
        if (floorTex == null) return;

        for (int x = 0; x < map.getWidth(); x++) {
            for (int y = 0; y < map.getHeight(); y++) {
                batch.draw(floorTex, x * size, y * size, size, size);
            }
        }
    }

    private void collectStations(GridMap map) {
        int size = GameConfig.TILE_SIZE;
        for (int x = 0; x < map.getWidth(); x++) {
            for (int y = 0; y < map.getHeight(); y++) {
                Tile tile = map.getTile(x, y);
                // Skip lantai kosong dan spawn point, hanya gambar station/tembok
                if (tile != null && tile.getSymbol() != '.' && tile.getSymbol() != 'V') {
                    final int col = x;
                    final int row = y;

                    renderList.add(new Renderable() {
                        @Override
                        public float getY() { return row; }

                        @Override
                        public int compareTo(Renderable o) {
                            return Float.compare(o.getY(), this.getY());
                        }

                        @Override
                        public void render(SpriteBatch batch) {
                            String texName = getTextureForTile(tile.getSymbol());
                            Texture tex = resourceManager.getTexture(texName);
                            if (tex != null) {
                                batch.draw(tex, col * size, row * size, size, size);
                            }

                            // Render Item di atas Station (jika ada)
                            Station s = tile.getStation();
                            if (s != null && s.hasItem()) {
                                Texture itemTex = resourceManager.getTexture(s.getItem().getTextureName());
                                if (itemTex != null) {
                                    float itemScale = 0.6f;
                                    float offset = (size - (size * itemScale)) / 2;
                                    // Gambar item sedikit di atas meja (+10 pixel)
                                    batch.draw(itemTex, col * size + offset, row * size + offset + 10, size * itemScale, size * itemScale);
                                }
                            }
                        }
                    });
                }
            }
        }
    }

    private void collectChefs() {
        int size = GameConfig.TILE_SIZE;
        for (Chef c : mapManager.chefs) {
            renderList.add(new Renderable() {
                @Override
                public float getY() { return c.visualPos.y; }

                @Override
                public int compareTo(Renderable o) {
                    return Float.compare(o.getY(), this.getY());
                }

                @Override
                public void render(SpriteBatch batch) {
                    TextureRegion frame = getChefFrame(c);
                    float scaledSize = size * CHEF_SCALE;
                    float offsetXY = (scaledSize - size) / 2f;
                    float offsetYCorrection = size * 0.4f;

                    float drawX = (c.visualPos.x * size) - offsetXY;
                    float drawY = (c.visualPos.y * size) - offsetXY + offsetYCorrection;

                    // Highlight active chef
                    if (c == mapManager.activeChef) batch.setColor(1, 1, 1, 1);
                    else batch.setColor(0.6f, 0.6f, 0.6f, 1);

                    if (frame != null) batch.draw(frame, drawX, drawY, scaledSize, scaledSize);

                    batch.setColor(1, 1, 1, 1);

                    // Render Item Held (Di atas kepala chef)
                    if (c.getInventory() != null) {
                        Texture itemTex = resourceManager.getTexture(c.getInventory().getTextureName());
                        if (itemTex != null) {
                            float itemSize = size * 0.5f;
                            batch.draw(itemTex, drawX + size/2, drawY + size, itemSize, itemSize);
                        }
                    }
                }
            });
        }
    }

    private TextureRegion getChefFrame(Chef c) {
        if (c.isChopping) return chefChopAnim.getKeyFrame(c.stateTime, true);
        if (c.isMoving) return chefWalkAnim.getKeyFrame(c.stateTime, true);
        return chefIdleAnim.getKeyFrame(0);
    }

    private String getTextureForTile(char symbol) {
        switch (symbol) {
            case 'X': return "stations/wall.png";
            case 'C': return "stations/cutting_board.png";
            case 'R': return "stations/stove.png";
            case 'A': return "stations/counter.png";
            case 'I': return "stations/crate.png";
            case 'P': return "stations/counter.png";
            case 'S': return "stations/delivery.png";
            case 'W': return "stations/sink.png";
            case 'T': return "stations/trash.png";
            default: return "stations/floor.png";
        }
    }
}
