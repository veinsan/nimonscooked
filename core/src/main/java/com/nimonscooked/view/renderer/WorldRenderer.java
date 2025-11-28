package com.nimonscooked.view.renderer;

import com.badlogic.gdx.graphics.Color;
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

    private Animation<TextureRegion> idleDown, idleUp, idleSide;
    private Animation<TextureRegion> walkDown, walkUp, walkSide;
    private Animation<TextureRegion> chopAnim;

    private static final float CHEF_SCALE = 1.8f;
    private Texture shadowTexture;

    private interface Renderable extends Comparable<Renderable> {
        void render(SpriteBatch batch);
        float getY();
    }

    public WorldRenderer() {
        this.resourceManager = ResourceManager.getInstance();
        this.mapManager = MapManager.getInstance();
        this.renderList = new ArrayList<>();
        this.shadowTexture = resourceManager.getTexture("stations/wall.png");
        initAnimations();
    }

    private void initAnimations() {
        idleDown = createAnimation(resourceManager.getTexture("chef/chef_idle_down.png"), 4, 0.15f);
        idleUp   = createAnimation(resourceManager.getTexture("chef/chef_idle_up.png"), 4, 0.15f);
        idleSide = createAnimation(resourceManager.getTexture("chef/chef_idle_side.png"), 4, 0.15f);

        walkDown = createAnimation(resourceManager.getTexture("chef/chef_walk_down.png"), 6, 0.1f);
        walkUp   = createAnimation(resourceManager.getTexture("chef/chef_walk_up.png"), 6, 0.1f);
        walkSide = createAnimation(resourceManager.getTexture("chef/chef_walk_side.png"), 6, 0.1f);

        Texture chopTex = resourceManager.getTexture("chef/chef_chop.png");
        if (chopTex == null) chopTex = resourceManager.getTexture("chef/chef_walk_side.png");
        chopAnim = createAnimation(chopTex, 6, 0.08f);
    }

    private Animation<TextureRegion> createAnimation(Texture sheet, int cols, float duration) {
        if (sheet == null) return null;
        TextureRegion[][] tmp = TextureRegion.split(sheet, sheet.getWidth() / cols, sheet.getHeight());
        TextureRegion[] frames = new TextureRegion[cols];
        System.arraycopy(tmp[0], 0, frames, 0, cols);
        return new Animation<>(duration, frames);
    }

    public void render(SpriteBatch batch) {
        GridMap map = mapManager.currentMap;
        if (map == null) return;

        renderFloor(batch, map);
        renderList.clear();
        collectStations(map);
        collectChefs();
        Collections.sort(renderList);
        for (Renderable r : renderList) r.render(batch);
    }

    private void renderFloor(SpriteBatch batch, GridMap map) {
        Texture floorTex = resourceManager.getTexture("stations/floor.png");
        int size = GameConfig.TILE_SIZE;
        if (floorTex == null) return;

        batch.setColor(0.8f, 0.8f, 0.8f, 1f);
        for (int x = 0; x < map.getWidth(); x++) {
            for (int y = 0; y < map.getHeight(); y++) {
                batch.draw(floorTex, x * size, y * size, size, size);
            }
        }
        batch.setColor(Color.WHITE);
    }

    private void collectStations(GridMap map) {
        int size = GameConfig.TILE_SIZE;
        for (int x = 0; x < map.getWidth(); x++) {
            for (int y = 0; y < map.getHeight(); y++) {
                Tile tile = map.getTile(x, y);
                if (tile != null && tile.getSymbol() != '.' && tile.getSymbol() != 'V') {
                    final int col = x;
                    final int row = y;
                    renderList.add(new Renderable() {
                        @Override public float getY() { return row; }
                        @Override public int compareTo(Renderable o) { return Float.compare(o.getY(), this.getY()); }
                        @Override public void render(SpriteBatch batch) {
                            String texName = getTextureForTile(tile.getSymbol());
                            Texture tex = resourceManager.getTexture(texName);
                            if (tex != null) batch.draw(tex, col * size, row * size, size, size);

                            Station s = tile.getStation();
                            if (s != null) {
                                if (s.hasItem()) {
                                    Texture itemTex = resourceManager.getTexture(s.getItem().getTextureName());
                                    if (itemTex != null) {
                                        float bob = (float)Math.sin(System.currentTimeMillis() / 200.0) * 2f;
                                        batch.draw(itemTex, col*size+10, row*size+20 + bob, size*0.6f, size*0.6f);
                                    }
                                }
                                if (s.getItem() instanceof com.nimonscooked.model.utensil.CookingDevice) {
                                    com.nimonscooked.model.utensil.CookingDevice device = (com.nimonscooked.model.utensil.CookingDevice) s.getItem();
                                    if (device.isCooking() || device.getProgress() > 0) {
                                        drawProgressBar(batch, col * size + 12, row * size + size + 15, device.getProgress(), false);
                                    }
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
                @Override public float getY() { return c.visualPos.y; }
                @Override public int compareTo(Renderable o) { return Float.compare(o.getY(), this.getY()); }
                @Override public void render(SpriteBatch batch) {
                    TextureRegion frame = getChefFrame(c);
                    float scaledSize = size * CHEF_SCALE;
                    float offsetXY = (scaledSize - size) / 2f;
                    float drawX = (c.visualPos.x * size) - offsetXY;
                    float drawY = (c.visualPos.y * size) - offsetXY + (size*0.4f);

                    if (shadowTexture != null) {
                        batch.setColor(0f, 0f, 0f, 0.4f);
                        batch.draw(shadowTexture, drawX + 20, c.visualPos.y * size + 5, scaledSize - 40, 15);
                    }

                    if (c == mapManager.activeChef) batch.setColor(1, 1, 1, 1);
                    else batch.setColor(0.6f, 0.6f, 0.6f, 1);

                    if (frame != null) {
                        boolean flip = (c.direction == Chef.Direction.LEFT);
                        if (frame.isFlipX() != flip) {
                            frame.flip(true, false);
                        }
                        batch.draw(frame, drawX, drawY, scaledSize, scaledSize);
                    }
                    batch.setColor(1, 1, 1, 1);

                    if (c.getInventory() != null) {
                        Texture itemTex = resourceManager.getTexture(c.getInventory().getTextureName());
                        if (itemTex != null) {
                            float bob = (float)Math.sin(c.stateTime * 5f) * 3f;
                            batch.draw(itemTex, drawX + size/2, drawY + size + bob, size*0.5f, size*0.5f);
                        }
                    }

                    if (c.isBusy() && c.getCurrentInteraction() != null) {
                        drawProgressBar(batch, drawX + size/2, drawY + scaledSize, c.getCurrentInteraction().getProgress(), true);
                    }
                }
            });
        }
    }

    private void drawProgressBar(SpriteBatch batch, float x, float y, float progress, boolean isChefAction) {
        float width = 40f;
        float height = 6f;
        Texture blank = resourceManager.getTexture("stations/wall.png");
        batch.setColor(0f, 0f, 0f, 1f);
        batch.draw(blank, x - 1, y - 1, width + 2, height + 2);
        batch.setColor(0.2f, 0.2f, 0.2f, 1f);
        batch.draw(blank, x, y, width, height);
        if (isChefAction) batch.setColor(0f, 0.8f, 1f, 1f);
        else batch.setColor(0f, 1f, 0f, 1f);
        batch.draw(blank, x, y, width * progress, height);
        batch.setColor(1, 1, 1, 1);
    }

    private TextureRegion getChefFrame(Chef c) {
        Animation<TextureRegion> targetAnim = idleDown;

        if (c.isChopping) {
            targetAnim = chopAnim;
        } else if (c.isMoving) {
            switch (c.direction) {
                case UP: targetAnim = walkUp; break;
                case DOWN: targetAnim = walkDown; break;
                case RIGHT: targetAnim = walkSide; break;
                case LEFT: targetAnim = walkSide; break;
            }
        } else {
            switch (c.direction) {
                case UP: targetAnim = idleUp; break;
                case DOWN: targetAnim = idleDown; break;
                case RIGHT: targetAnim = idleSide; break;
                case LEFT: targetAnim = idleSide; break;
            }
        }

        if (targetAnim == null) return null;
        return targetAnim.getKeyFrame(c.stateTime, true);
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