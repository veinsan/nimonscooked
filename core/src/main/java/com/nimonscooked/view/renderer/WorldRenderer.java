package com.nimonscooked.view.renderer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.MathUtils;
import com.nimonscooked.config.GameConfig;
import com.nimonscooked.manager.MapManager;
import com.nimonscooked.manager.ResourceManager;
import com.nimonscooked.manager.ShaderManager;
import com.nimonscooked.model.entity.Chef;
import com.nimonscooked.model.item.Item;
import com.nimonscooked.model.map.GridMap;
import com.nimonscooked.model.map.Tile;
import com.nimonscooked.model.station.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class WorldRenderer {

    private ResourceManager resourceManager;
    private MapManager mapManager;
    private ShaderManager shaderManager;
    private List<Renderable> renderList;
    
    // Animations
    private Animation<TextureRegion> idleDown, idleUp, idleSide;
    private Animation<TextureRegion> walkDown, walkUp, walkSide;
    private Animation<TextureRegion> chopAnim;
    
    // Textures
    private Texture shadowTexture;
    private Texture snowTexture;
    
    // Snow System
    private static final int SNOW_COUNT = 200;
    private float[] snowX, snowY, snowSpeed, snowSize;
    private float stateTime = 0f;

    private static final float CHEF_SCALE = 1.8f;

    private interface Renderable extends Comparable<Renderable> {
        void render(SpriteBatch batch);
        float getY();
    }

    public WorldRenderer() {
        this.resourceManager = ResourceManager.getInstance();
        this.mapManager = MapManager.getInstance();
        this.shaderManager = ShaderManager.getInstance();
        this.renderList = new ArrayList<>();
        
        initAnimations();
        createShadowTexture();
        createSnowTexture(); 
        initSnow(); 
    }

    // --- INIT SNOW ---
    private void createSnowTexture() {
        Pixmap p = new Pixmap(4, 4, Pixmap.Format.RGBA8888);
        p.setColor(1, 1, 1, 1);
        p.fillCircle(2, 2, 2);
        snowTexture = new Texture(p);
        p.dispose();
    }

    private void initSnow() {
        snowX = new float[SNOW_COUNT];
        snowY = new float[SNOW_COUNT];
        snowSpeed = new float[SNOW_COUNT];
        snowSize = new float[SNOW_COUNT];

        for (int i = 0; i < SNOW_COUNT; i++) {
            snowX[i] = MathUtils.random(-500, 2000); 
            snowY[i] = MathUtils.random(-500, 1500);
            snowSpeed[i] = MathUtils.random(60f, 150f); 
            snowSize[i] = MathUtils.random(0.5f, 1.5f); 
        }
    }

    // --- INIT SHADOW ---
    private void createShadowTexture() {
        int size = 64;
        Pixmap pixmap = new Pixmap(size, size, Pixmap.Format.RGBA8888);
        
        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                float dx = Math.abs(x - size/2f) / (size/2f);
                float dy = Math.abs(y - size/2f) / (size/2f);
                float dist = Math.max(dx, dy);
                
                float alpha = 1f;
                if (dist > 0.85f) {
                    alpha = 1f - ((dist - 0.85f) / 0.15f);
                }
                if (alpha < 0) alpha = 0;

                pixmap.setColor(1f, 1f, 1f, alpha); 
                pixmap.drawPixel(x, y);
            }
        }
        
        shadowTexture = new Texture(pixmap);
        pixmap.dispose();
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

    // --- MAIN RENDER ---
    public void render(SpriteBatch batch) {
        GridMap map = mapManager.currentMap;
        if (map == null) return;

        float delta = Gdx.graphics.getDeltaTime();
        stateTime += delta;

        // 1. SET SHADER - TAVERN (Warm & Bright!) ✅
        ShaderProgram shader = shaderManager.getShader("tavern");
        if (shader != null && shader.isCompiled()) {
            batch.setShader(shader);
            shader.bind();
            shader.setUniformf("u_resolution", Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
            shader.setUniformf("u_time", stateTime);
        }

        // 2. RENDER LAYERS
        renderBackground(batch);
        renderMapShadow(batch, map); // ✅ Keep shadow for background transition
        renderFloor(batch, map);
        
        renderList.clear();
        collectStations(map);
        collectChefs();
        Collections.sort(renderList);
        for (Renderable r : renderList) r.render(batch);

        // 3. RENDER SNOW
        renderSnow(batch, delta);

        // 4. RESET SHADER
        batch.setShader(null); 
    }

    private void renderSnow(SpriteBatch batch, float delta) {
        if (snowTexture == null) return;
        batch.setColor(1f, 1f, 1f, 0.8f); 
        for (int i = 0; i < SNOW_COUNT; i++) {
            snowY[i] -= snowSpeed[i] * delta;
            float wind = (float)Math.sin(stateTime + i) * 0.5f; 
            snowX[i] += wind;
            if (snowY[i] < -500) {
                snowY[i] = 1500; 
                snowX[i] = MathUtils.random(-500, 2000);
            }
            float size = 4 * snowSize[i];
            batch.draw(snowTexture, snowX[i], snowY[i], size, size);
        }
        batch.setColor(Color.WHITE);
    }

    private void renderBackground(SpriteBatch batch) {
        Texture bg = resourceManager.getTexture("ui/bg_winter.jpg");
        if (bg != null) {
            float tileSize = GameConfig.TILE_SIZE;
            float mapWidth = 14 * tileSize;
            float mapHeight = 10 * tileSize;
            float centerX = mapWidth / 2f;
            float centerY = mapHeight / 2f;
            float scaleFactor = 2.3f; 
            float drawWidth = mapWidth * scaleFactor;
            float aspectRatio = (float) bg.getHeight() / bg.getWidth();
            float drawHeight = drawWidth * aspectRatio;
            float x = centerX - (drawWidth / 2f);
            float y = centerY - (drawHeight / 2f);
            batch.draw(bg, x, y, drawWidth, drawHeight);
        } else {
            Texture wall = resourceManager.getTexture("stations/wall.png");
            if (wall != null) {
                batch.setColor(0.4f, 0.35f, 0.3f, 1f); // ✅ Warmer fallback color
                batch.draw(wall, -1000, -1000, 4000, 4000);
                batch.setColor(1, 1, 1, 1);
            }
        }
    }

    // --- Shadow for background transition ---
    private void renderMapShadow(SpriteBatch batch, GridMap map) {
        if (shadowTexture == null) return;

        float tileSize = GameConfig.TILE_SIZE;
        float mapWidth = map.getWidth() * tileSize;
        float mapHeight = map.getHeight() * tileSize;

        float padding = 100f; 

        float x = -padding;
        float y = -padding;
        float w = mapWidth + (padding * 2);
        float h = mapHeight + (padding * 2);

        batch.setColor(0f, 0f, 0f, 0.8f); // ✅ Lighter shadow (was 0.8f) for tavern brightness
        batch.draw(shadowTexture, x, y, w, h);
        
        batch.setColor(Color.WHITE);
    }

    private void renderFloor(SpriteBatch batch, GridMap map) {
        Texture floor1 = resourceManager.getTexture("stations/floor.png");
        Texture floor2 = resourceManager.getTexture("stations/floor2.png");
        int size = GameConfig.TILE_SIZE;
        if (floor1 == null) return;
        if (floor2 == null) floor2 = floor1;
        batch.setColor(1.0f, 1.0f, 1.0f, 1f); // ✅ Full brightness (was 0.8f)
        for (int x = 0; x < map.getWidth(); x++) {
            for (int y = 0; y < map.getHeight(); y++) {
                Texture currentFloor = ((x + y) % 2 == 0) ? floor1 : floor2;
                batch.draw(currentFloor, x * size, y * size, size, size);
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
                    final Station s = tile.getStation();
                    renderList.add(new Renderable() {
                        @Override public float getY() { return row; }
                        @Override public int compareTo(Renderable o) { return Float.compare(o.getY(), this.getY()); }
                        @Override public void render(SpriteBatch batch) {
                            String texName = getTextureForTile(tile.getSymbol(), col, s);
                            if (s instanceof CookingStation && ((CookingStation) s).isActive()) texName = getActiveTextureForStove(col); 
                            if (s instanceof CuttingStation && ((CuttingStation) s).isActive()) texName = "stations/cutting_active.png";
                            if (s instanceof WashingStation && ((WashingStation) s).isActive()) texName = "stations/sink_active.png";
                            
                            Texture tex = resourceManager.getTexture(texName);
                            if (tex == null && texName.contains("crate")) tex = resourceManager.getTexture("stations/crate_meat.png");
                            if (tex == null && texName.contains("cutting_active")) tex = resourceManager.getTexture("stations/cutting.png");
                            if (tex == null && texName.contains("sink_active")) tex = resourceManager.getTexture("stations/sink.png");
                            
                            if (tex != null) batch.draw(tex, col * size, row * size, size, size);
                            else {
                                Texture wall = resourceManager.getTexture("stations/wall.png");
                                if (wall != null) {
                                    batch.setColor(1, 0, 0, 1);
                                    batch.draw(wall, col * size, row * size, size, size);
                                    batch.setColor(1, 1, 1, 1);
                                }
                            }
                            if (s != null) {
                                if (s.hasItem()) {
                                    String itemTexName = s.getItem().getTextureName();
                                    Texture itemTex = resourceManager.getTexture(itemTexName);
                                    if (itemTex != null) {
                                        float bob = (float)Math.sin(System.currentTimeMillis() / 200.0) * 2f;
                                        batch.draw(itemTex, col*size+10, row*size+20 + bob, size*0.6f, size*0.6f);
                                    }
                                }
                                float progress = 0f;
                                boolean showBar = false;
                                if (s instanceof CookingStation) {
                                    Item item = s.getItem();
                                    if (item instanceof com.nimonscooked.model.utensil.CookingDevice) {
                                        var device = (com.nimonscooked.model.utensil.CookingDevice) item;
                                        if (device.isCooking()) { progress = device.getProgress(); showBar = true; }
                                    }
                                } else if (s instanceof CuttingStation) {
                                    CuttingStation cs = (CuttingStation) s;
                                    if (cs.getProgress() > 0) { progress = cs.getProgress(); showBar = true; }
                                } else if (s instanceof WashingStation) {
                                    WashingStation ws = (WashingStation) s;
                                    if (ws.isActive()) { progress = ws.getProgress(); showBar = true; }
                                }
                                if (showBar) drawProgressBar(batch, col * size + 12, row * size + size + 5, progress, false);
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
                    if (frame != null) {
                        boolean flip = (c.direction == Chef.Direction.LEFT);
                        if (frame.isFlipX() != flip) frame.flip(true, false);
                        batch.setColor(0f, 0f, 0f, 0.4f); 
                        float shadowHeight = scaledSize * 0.3f; 
                        batch.draw(frame, drawX, drawY - (shadowHeight * 0.2f), scaledSize, shadowHeight);
                        if (c == mapManager.activeChef) batch.setColor(1, 1, 1, 1);
                        else batch.setColor(0.6f, 0.6f, 0.6f, 1);
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
        if (blank == null) return;
        batch.setColor(0f, 0f, 0f, 1f);
        batch.draw(blank, x - 1, y - 1, width + 2, height + 2);
        batch.setColor(0.2f, 0.2f, 0.2f, 1f);
        batch.draw(blank, x, y, width, height);
        if (isChefAction) batch.setColor(0f, 0.8f, 1f, 1f);
        else {
            if (progress < 0.5f) batch.setColor(1f, 0.8f, 0f, 1f);
            else if (progress < 0.8f) batch.setColor(0f, 1f, 0f, 1f);
            else batch.setColor(1f, 0.3f, 0f, 1f);
        }
        batch.draw(blank, x, y, width * Math.min(progress, 1f), height);
        batch.setColor(1, 1, 1, 1);
    }

    private TextureRegion getChefFrame(Chef c) {
        Animation<TextureRegion> targetAnim = idleDown;
        if (c.isChopping) targetAnim = chopAnim;
        else if (c.isMoving) {
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

    private String getTextureForTile(char symbol, int x, Station station) {
        switch (symbol) {
            case 'X': return "stations/wall.png";
            case 'C': return "stations/cutting.png"; 
            case 'R': if (x < 7) return "stations/stove_right.png"; else return "stations/stove_left.png";
            case 'A': return "stations/assembly.png";
            case 'I': 
                if (station instanceof IngredientStorage) {
                    String name = ((IngredientStorage) station).getIngredientName();
                    if (name != null) {
                        String lower = name.toLowerCase();
                        if (lower.contains("bun") || lower.contains("roti") || lower.contains("bread")) return "stations/crate_bread.png";
                        if (lower.contains("meat") || lower.contains("daging")) return "stations/crate_meat.png";
                        if (lower.contains("cheese") || lower.contains("keju")) return "stations/crate_cheese.png";
                        if (lower.contains("lettuce") || lower.contains("selada")) return "stations/crate_lettuce.png";
                        if (lower.contains("tomato") || lower.contains("tomat")) return "stations/crate_tomato.png";
                    }
                }
                return "stations/crate_meat.png";
            case 'P': return "stations/plate_storage.png";
            case 'S': return "stations/serving.png";
            case 'W': return "stations/sink.png";
            case 'T': return "stations/trash.png";
            default: return "stations/floor.png";
        }
    }

    private String getActiveTextureForStove(int x) {
        if (x < 7) return "stations/stove_active_right.png";
        else return "stations/stove_active_left.png";
    }
}