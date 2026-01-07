package com.nimonscooked.view.renderer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.nimonscooked.config.GameConfig;
import com.nimonscooked.manager.MapManager;
import com.nimonscooked.manager.ResourceManager;
import com.nimonscooked.manager.ShaderManager;
import com.nimonscooked.model.entity.Chef;
import com.nimonscooked.model.ingredient.Preparable;
import com.nimonscooked.model.item.Ingredient;
import com.nimonscooked.model.item.Item;
import com.nimonscooked.model.map.GridMap;
import com.nimonscooked.model.map.Tile;
import com.nimonscooked.model.station.*;
import com.nimonscooked.model.utensil.FryingPan;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class WorldRenderer {

    private ResourceManager resourceManager;
    private MapManager mapManager;
    private ShaderManager shaderManager;
    private List<Renderable> renderList;
    private BitmapFont font;
    
    private Animation<TextureRegion> idleDown, idleUp, idleSide;
    private Animation<TextureRegion> walkDown, walkUp, walkSide;
    
    private Animation<TextureRegion> smokeAnim;
    private Animation<TextureRegion> chopAnim;
    private Animation<TextureRegion> washAnim;
    
    private Texture shadowTexture;
    private Texture snowTexture;
    private Texture pixelTexture;
    
    private static final int SNOW_COUNT = 200;
    private float[] snowX, snowY, snowSpeed, snowSize;
    private float stateTime = 0f;

    private static final float CHEF_SCALE = 1.8f;

    private Vector3 mouseWorldPos = new Vector3();

    private interface Renderable extends Comparable<Renderable> {
        void render(SpriteBatch batch);
        float getY();
    }

    public WorldRenderer() {
        this.resourceManager = ResourceManager.getInstance();
        this.mapManager = MapManager.getInstance();
        this.shaderManager = ShaderManager.getInstance();
        this.renderList = new ArrayList<>();
        this.font = resourceManager.getCustomFont();
        
        initAnimations();
        initEffectAnimations(); 
        createShadowTexture();
        createSnowTexture(); 
        createPixelTexture();
        initSnow(); 
    }

    private void initEffectAnimations() {
        Texture smokeSheet = resourceManager.getTexture("effects/smoke.png");
        if (smokeSheet != null) {
            int FRAME_COLS = 8; 
            TextureRegion[][] tmp = TextureRegion.split(smokeSheet, smokeSheet.getWidth() / FRAME_COLS, smokeSheet.getHeight());
            smokeAnim = new Animation<>(0.1f, tmp[0]);
            smokeAnim.setPlayMode(Animation.PlayMode.LOOP);
        }

        Texture chopSheet = resourceManager.getTexture("effects/chop.png");
        if (chopSheet != null) {
            int FRAME_COLS = 3;
            TextureRegion[][] tmp = TextureRegion.split(chopSheet, chopSheet.getWidth() / FRAME_COLS, chopSheet.getHeight());
            chopAnim = new Animation<>(0.15f, tmp[0]);
            chopAnim.setPlayMode(Animation.PlayMode.LOOP);
        }
        Texture washSheet = resourceManager.getTexture("effects/wash.png");
        if (washSheet != null) {
            washAnim = new Animation<>(0.2f, new TextureRegion(washSheet));
            washAnim.setPlayMode(Animation.PlayMode.LOOP);
        }
    }

    private void createPixelTexture() {
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.WHITE);
        pixmap.fill();
        pixelTexture = new Texture(pixmap);
        pixmap.dispose();
    }

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
    }

    private Animation<TextureRegion> createAnimation(Texture sheet, int cols, float duration) {
        if (sheet == null) return null;
        TextureRegion[][] tmp = TextureRegion.split(sheet, sheet.getWidth() / cols, sheet.getHeight());
        TextureRegion[] frames = new TextureRegion[cols];
        System.arraycopy(tmp[0], 0, frames, 0, cols);
        return new Animation<>(duration, frames);
    }

    private Station getFacingStation() {
        Chef activeChef = mapManager.activeChef;
        if (activeChef == null) return null;

        int targetCol = activeChef.position.col;
        int targetRow = activeChef.position.row;

        switch (activeChef.direction) {
            case UP: targetRow++; break;
            case DOWN: targetRow--; break;
            case LEFT: targetCol--; break;
            case RIGHT: targetCol++; break;
        }

        if (!mapManager.currentMap.isValid(targetCol, targetRow)) {
            return null;
        }

        return mapManager.getStationAt(targetCol, targetRow);
    }

    public void render(SpriteBatch batch) {
        GridMap map = mapManager.currentMap;
        if (map == null) return;

        float delta = Gdx.graphics.getDeltaTime();
        stateTime += delta;

        ShaderProgram shader = shaderManager.getShader("tavern");
        if (shader != null && shader.isCompiled()) {
            batch.setShader(shader);
            shader.bind();
            shader.setUniformf("u_resolution", Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
            shader.setUniformf("u_time", stateTime);
        }

        renderBackground(batch);
        renderMapShadow(batch, map);
        renderFloor(batch, map);
        renderSelectionHighlight(batch);
        renderDroppedItems(batch, map);
        
        renderList.clear();
        collectStations(map);
        Collections.sort(renderList);
        for (Renderable r : renderList) r.render(batch);

        renderChefs(batch);
        renderSnow(batch, delta);
        renderTooltip(batch, map);

        batch.setShader(null); 
    }

    private void renderSelectionHighlight(SpriteBatch batch) {
        Chef activeChef = mapManager.activeChef;
        if (activeChef == null) return;

        int targetCol = activeChef.position.col;
        int targetRow = activeChef.position.row;

        switch (activeChef.direction) {
            case UP: targetRow++; break;
            case DOWN: targetRow--; break;
            case LEFT: targetCol--; break;
            case RIGHT: targetCol++; break;
        }

        if (mapManager.currentMap.isValid(targetCol, targetRow)) {
            float x = targetCol * GameConfig.TILE_SIZE;
            float y = targetRow * GameConfig.TILE_SIZE;
            
            Station station = mapManager.getStationAt(targetCol, targetRow);
            
            if (station != null) {
                batch.setColor(0f, 1f, 0f, 0.3f);
            } else {
                batch.setColor(1f, 1f, 1f, 0.2f);
            }
            
            batch.draw(pixelTexture, x, y, GameConfig.TILE_SIZE, GameConfig.TILE_SIZE);
            
            batch.setColor(1f, 1f, 0f, 0.5f);
            float t = 2f;
            batch.draw(pixelTexture, x, y, GameConfig.TILE_SIZE, t);
            batch.draw(pixelTexture, x, y + GameConfig.TILE_SIZE - t, GameConfig.TILE_SIZE, t);
            batch.draw(pixelTexture, x, y, t, GameConfig.TILE_SIZE);
            batch.draw(pixelTexture, x + GameConfig.TILE_SIZE - t, y, t, GameConfig.TILE_SIZE);
            
            batch.setColor(Color.WHITE);
        }
    }

    private void collectStations(GridMap map) {
        int size = GameConfig.TILE_SIZE;
        Station facingStation = getFacingStation();
        
        for (int x = 0; x < map.getWidth(); x++) {
            for (int y = 0; y < map.getHeight(); y++) {
                Tile tile = map.getTile(x, y);
                if (tile != null && tile.getSymbol() != '.' && tile.getSymbol() != 'V') {
                    final int col = x;
                    final int row = y;
                    final Station s = tile.getStation();
                    final boolean isHighlighted = (s == facingStation);
                    
                    renderList.add(new Renderable() {
                        @Override public float getY() { return row; }
                        @Override public int compareTo(Renderable o) { return Float.compare(o.getY(), this.getY()); }
                        
                        @Override public void render(SpriteBatch batch) {
                            String texName = getTextureForTile(tile.getSymbol(), col, s);
                            boolean stationIsBeingHeld = false;
                            
                            if (s instanceof CookingStation) {
                                CookingStation cs = (CookingStation) s;
                                boolean isCooked = false;
                                Item item = cs.getItem();
                                if (item instanceof FryingPan) {
                                    FryingPan pan = (FryingPan) item;
                                    if (pan.hasCookedFood()) {
                                        if (cs.getStoveType() == CookingStation.StoveType.LEFT) texName = "stations/stove_cooked_left.png";
                                        else texName = "stations/stove_cooked_right.png";
                                        stationIsBeingHeld = true; 
                                        isCooked = true;
                                    }
                                }
                                if (!isCooked && cs.isActive()) {
                                    stationIsBeingHeld = true; 
                                    if (cs.getStoveType() == CookingStation.StoveType.LEFT) texName = "stations/stove_active_left.png";
                                    else texName = "stations/stove_active_right.png";
                                }
                            }
                            
                            if (s instanceof CuttingStation && ((CuttingStation)s).isActive()) {
                                stationIsBeingHeld = true;
                                texName = "stations/cutting_active.png";
                            }
                            
                            if (s instanceof WashingStation && ((WashingStation)s).isActive()) {
                                stationIsBeingHeld = true;
                                texName = "stations/sink_active.png";
                            }
                            
                            Texture tex = resourceManager.getTexture(texName);
                            if (tex == null && texName.contains("crate")) tex = resourceManager.getTexture("stations/crate_meat.png");
                            if (tex == null && texName.contains("cutting")) tex = resourceManager.getTexture("stations/cutting.png");
                            if (tex == null && texName.contains("sink")) tex = resourceManager.getTexture("stations/sink.png");
                            
                            if (isHighlighted) batch.setColor(1.3f, 1.3f, 1.3f, 1f);
                            else batch.setColor(1f, 1f, 1f, 1f);
                            
                            if (tex != null) batch.draw(tex, col * size, row * size, size, size);
                            else {
                                Texture wall = resourceManager.getTexture("stations/wall.png");
                                if(wall!=null) { batch.setColor(1,0,0,1); batch.draw(wall, col*size, row*size, size, size); }
                            }
                            
                            batch.setColor(1f, 1f, 1f, 1f);
                            
                            if (s != null) {
                                if (s.hasItem() && !stationIsBeingHeld) {
                                    Item item = s.getItem();
                                    String itemTexName = item.getTextureName();
                                    
                                    if (!itemTexName.equals("EMPTY_PAN")) {
                                        float bob = (float)Math.sin(System.currentTimeMillis() / 200.0) * 2f;
                                        
                                        if (item instanceof com.nimonscooked.model.utensil.Plate) {
                                            com.nimonscooked.model.utensil.Plate plate = (com.nimonscooked.model.utensil.Plate) item;
                                            
                                            Texture plateTex = resourceManager.getTexture(plate.getTextureName());
                                            if (plateTex != null) {
                                                batch.draw(plateTex, col*size+10, row*size+20 + bob, size*0.6f, size*0.6f);
                                            }
                                            
                                            if (plate.getContainedDish() != null) {
                                                com.nimonscooked.model.dish.Dish dish = plate.getContainedDish();
                                                String dishTexName = dish.getTextureName();
                                                
                                                Texture dishTex = resourceManager.getTexture(dishTexName);
                                                if (dishTex != null) {
                                                    batch.draw(dishTex, col*size+12, row*size+25 + bob, size*0.55f, size*0.55f);
                                                }
                                            }
                                            
                                            renderIngredientPopup(batch, col*size, row*size + bob, item);
                                        } else {
                                            Texture itemTex = resourceManager.getTexture(itemTexName);
                                            if (itemTex != null) {
                                                batch.draw(itemTex, col*size+10, row*size+20 + bob, size*0.6f, size*0.6f);
                                                renderIngredientPopup(batch, col*size, row*size + bob, item);
                                            }
                                        }
                                    }
                                }
                                else if (s instanceof PlateStorage) {
                                    PlateStorage ps = (PlateStorage) s;
                                    
                                    if (ps.hasDirtyPlates()) {
                                        Texture dirtyPlateTex = resourceManager.getTexture("items/plate_dirty.png");
                                        if (dirtyPlateTex != null) {
                                            float bob = (float)Math.sin(System.currentTimeMillis() / 500.0) * 2f;
                                            batch.draw(dirtyPlateTex, col * size + 10, row * size + 20 + bob, size * 0.7f, size * 0.7f);
                                        }
                                    }
                                }

                                float progress = 0f;
                                boolean showBar = false;

                                if (s instanceof CookingStation) {
                                    Item item = s.getItem();
                                    if (item instanceof FryingPan) {
                                        FryingPan pan = (FryingPan) item;
                                        if (pan.isCooking()) { 
                                            progress = pan.getProgress(); 
                                            showBar = true; 
                                        }
                                        
                                        boolean showSmoke = pan.isCooking();
                                        for(Preparable p : pan.getContents()) {
                                            if(p instanceof Ingredient && ((Ingredient)p).getState() == Ingredient.State.BURNT) {
                                                showSmoke = false;
                                            }
                                        }
                                        if (showSmoke && smokeAnim != null) {
                                            TextureRegion frame = smokeAnim.getKeyFrame(stateTime, true);
                                            batch.setColor(1f, 1f, 1f, 0.7f);
                                            batch.draw(frame, col * size + (size/2f) - 16, row * size + size - 10, 32, 64);
                                            batch.setColor(1f, 1f, 1f, 1f);
                                        }
                                    }
                                } 
                                else if (s instanceof CuttingStation) {
                                    CuttingStation cs = (CuttingStation) s;
                                    float prog = cs.getProgress();
                                    if (prog > 0 && prog < 1.0f) { 
                                        progress = prog; 
                                        showBar = true; 
                                    }
                                    
                                    if (cs.isActive() && chopAnim != null) {
                                        TextureRegion frame = chopAnim.getKeyFrame(stateTime, true);
                                        float effectSize = 64f; 
                                        float effectX = col * size + (size - effectSize)/2f;
                                        float effectY = row * size + (size - effectSize)/2f + 10;
                                        
                                        float shakeAmount = 3f;
                                        float shakeSpeed = 18f;
                                        float shakeX = (float)Math.sin(stateTime * shakeSpeed) * shakeAmount;
                                        float shakeY = (float)Math.sin(stateTime * shakeSpeed * 1.3f) * (shakeAmount * 0.5f);
                                        
                                        effectX += shakeX;
                                        effectY += shakeY;
                                        
                                        batch.draw(frame, effectX, effectY, effectSize, effectSize);
                                    }
                                } 
                                else if (s instanceof WashingStation) {
                                    WashingStation ws = (WashingStation) s;
                                    float prog = ws.getProgress();
                                    if (prog > 0 && prog < 1.0f) { 
                                        progress = prog; 
                                        showBar = true; 
                                    }
                                    
                                    if (ws.isActive() && washAnim != null) {
                                        TextureRegion frame = washAnim.getKeyFrame(stateTime, true);
                                        float effectSize = 64f;
                                        float effectX = col * size + (size - effectSize) / 2f;
                                        float effectY = row * size + size - 20;
                                        
                                        float bobAmount = 2f;
                                        float bobSpeed = 3f;
                                        float bobOffset = (float)Math.sin(stateTime * bobSpeed) * bobAmount;
                                        
                                        effectY += bobOffset;
                                        
                                        batch.setColor(1f, 1f, 1f, 0.8f);
                                        batch.draw(frame, effectX, effectY, effectSize, effectSize);
                                        batch.setColor(1f, 1f, 1f, 1f);
                                    }
                                }
                                if (showBar) {
                                    drawProgressBar(batch, col * size + 12, row * size + size + 5, progress, false);
                                }
                            }
                        }
                    });
                }
            }
        }
    }

    private void renderDroppedItems(SpriteBatch batch, GridMap map) {
        int size = GameConfig.TILE_SIZE;
        for (int x = 0; x < map.getWidth(); x++) {
            for (int y = 0; y < map.getHeight(); y++) {
                Tile tile = map.getTile(x, y);
                if (tile != null && tile.hasDroppedItem()) {
                    Item item = tile.getDroppedItem();
                    Texture itemTex = resourceManager.getTexture(item.getTextureName());
                    if (itemTex != null) {
                        float bob = (float)Math.sin(stateTime * 3f) * 2f;
                        batch.draw(itemTex, x * size + 16, y * size + 10 + bob, size * 0.5f, size * 0.5f);
                    }
                }
            }
        }
    }

    private void renderTooltip(SpriteBatch batch, GridMap map) {
        mouseWorldPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
        int hoveredCol = (int)(mouseWorldPos.x / GameConfig.TILE_SIZE);
        int hoveredRow = (int)(mouseWorldPos.y / GameConfig.TILE_SIZE);
        if (!map.isValid(hoveredCol, hoveredRow)) return;
        Station station = mapManager.getStationAt(hoveredCol, hoveredRow);
        if (station instanceof AssemblyStation) {
            AssemblyStation as = (AssemblyStation) station;
            List<Item> ingredients = as.getCurrentIngredients();
            if (!ingredients.isEmpty()) {
                float tooltipX = hoveredCol * GameConfig.TILE_SIZE;
                float tooltipY = (hoveredRow + 1) * GameConfig.TILE_SIZE + 10;
                float bgWidth = 200;
                float bgHeight = 30 + (ingredients.size() * 20);
                batch.setColor(0.1f, 0.1f, 0.15f, 0.95f);
                batch.draw(pixelTexture, tooltipX, tooltipY, bgWidth, bgHeight);
                batch.setColor(1f, 0.9f, 0.4f, 1f);
                batch.draw(pixelTexture, tooltipX, tooltipY, bgWidth, 2);
                batch.draw(pixelTexture, tooltipX, tooltipY + bgHeight - 2, bgWidth, 2);
                batch.draw(pixelTexture, tooltipX, tooltipY, 2, bgHeight);
                batch.draw(pixelTexture, tooltipX + bgWidth - 2, tooltipY, 2, bgHeight);
                batch.setColor(Color.WHITE);
                font.getData().setScale(0.4f);
                font.setColor(1f, 0.9f, 0.5f, 1f);
                font.draw(batch, "Ingredients:", tooltipX + 10, tooltipY + bgHeight - 10);
                font.setColor(Color.WHITE);
                for (int i = 0; i < ingredients.size(); i++) {
                    String name = ingredients.get(i).getDisplayName();
                    font.draw(batch, "- " + name, tooltipX + 10, tooltipY + bgHeight - 30 - (i * 20));
                }
                font.getData().setScale(1f);
            }
        }
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
                batch.setColor(0.4f, 0.35f, 0.3f, 1f);
                batch.draw(wall, -1000, -1000, 4000, 4000);
                batch.setColor(1, 1, 1, 1);
            }
        }
    }

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
        batch.setColor(0f, 0f, 0f, 0.8f); 
        batch.draw(shadowTexture, x, y, w, h);
        batch.setColor(Color.WHITE);
    }

    private void renderFloor(SpriteBatch batch, GridMap map) {
        Texture floor1 = resourceManager.getTexture("stations/floor.png");
        Texture floor2 = resourceManager.getTexture("stations/floor2.png");
        int size = GameConfig.TILE_SIZE;
        if (floor1 == null) return;
        if (floor2 == null) floor2 = floor1;
        batch.setColor(1.0f, 1.0f, 1.0f, 1f); 
        for (int x = 0; x < map.getWidth(); x++) {
            for (int y = 0; y < map.getHeight(); y++) {
                Texture currentFloor = ((x + y) % 2 == 0) ? floor1 : floor2;
                batch.draw(currentFloor, x * size, y * size, size, size);
            }
        }
        batch.setColor(Color.WHITE);
    }

    private void renderChefs(SpriteBatch batch) {
        int size = GameConfig.TILE_SIZE;
        for (Chef c : mapManager.chefs) {
            TextureRegion frame = getChefFrame(c);
            float scaledSize = size * CHEF_SCALE;
            float offsetXY = (scaledSize - size) / 2f;
            float drawX = (c.visualPos.x * size) - offsetXY;
            float drawY = (c.visualPos.y * size) - offsetXY;
            
            if (frame != null) {
                boolean flip = (c.direction == Chef.Direction.LEFT);
                if (frame.isFlipX() != flip) frame.flip(true, false);
                
                batch.setColor(0f, 0f, 0f, 0.3f); 
                float shadowY = drawY - 5f;
                batch.draw(frame, drawX, shadowY, scaledSize, scaledSize * 0.2f);
                
                if (c.getType() == Chef.Type.CHEF_A) {
                    batch.setColor(1f, 1f, 1f, 1f); 
                } else {
                    batch.setColor(0.7f, 0.8f, 1f, 1f); 
                }
                
                if (c != mapManager.activeChef) {
                    Color old = batch.getColor();
                    batch.setColor(old.r * 0.6f, old.g * 0.6f, old.b * 0.6f, 1f);
                }
                
                batch.draw(frame, drawX, drawY, scaledSize, scaledSize);
            }
            
            batch.setColor(1, 1, 1, 1);
                        
            if (c.getInventory() != null) {
                Item item = c.getInventory();
                float bob = (float)Math.sin(c.stateTime * 5f) * 3f;
                
                if (item instanceof com.nimonscooked.model.utensil.Plate) {
                    com.nimonscooked.model.utensil.Plate plate = (com.nimonscooked.model.utensil.Plate) item;
                    
                    Texture plateTex = resourceManager.getTexture(plate.getTextureName());
                    if (plateTex != null) {
                        batch.draw(plateTex, drawX + size/2, drawY + scaledSize - 10 + bob, size*0.5f, size*0.5f);
                    }
                    
                    if (plate.getContainedDish() != null) {
                        com.nimonscooked.model.dish.Dish dish = plate.getContainedDish();
                        String dishTexName = dish.getTextureName();
                        
                        Texture dishTex = resourceManager.getTexture(dishTexName);
                        if (dishTex != null) {
                            batch.draw(dishTex, drawX + size/2 + 2, drawY + scaledSize - 5 + bob, size*0.45f, size*0.45f);
                        }
                    }
                    
                    renderIngredientPopup(batch, drawX, drawY + bob + 10, item);
                } else {
                    Texture itemTex = resourceManager.getTexture(item.getTextureName());
                    if (itemTex != null) {
                        batch.draw(itemTex, drawX + size/2, drawY + scaledSize - 10 + bob, size*0.5f, size*0.5f);
                        renderIngredientPopup(batch, drawX, drawY + bob + 10, item);
                    }
                }
            }
            
            if (c.isBusy() && c.getCurrentInteraction() != null) {
                drawProgressBar(batch, drawX + size/4, drawY + scaledSize + 5, c.getCurrentInteraction().getProgress(), true);
            }
        }
    }

    private void renderIngredientPopup(SpriteBatch batch, float x, float y, Item item) {
        if (!(item instanceof com.nimonscooked.model.dish.Dish)) return;
        com.nimonscooked.model.dish.Dish dish = (com.nimonscooked.model.dish.Dish) item;
        if (!item.getTextureName().contains("random")) return;
        java.util.List<Item> components = dish.getComponents();
        if (components.isEmpty()) return;
        float iconSize = 20f; 
        float padding = 2f;
        float totalWidth = (components.size() * iconSize) + ((components.size() - 1) * padding);
        float startX = x + (GameConfig.TILE_SIZE - totalWidth) / 2f;
        float startY = y + GameConfig.TILE_SIZE + 15f; 
        float bgPadding = 4f;
        batch.setColor(0f, 0f, 0f, 0.6f); 
        batch.draw(pixelTexture, startX - bgPadding, startY - bgPadding, totalWidth + bgPadding*2, iconSize + bgPadding*2);
        batch.setColor(Color.WHITE); 
        for (int i = 0; i < components.size(); i++) {
            Item ing = components.get(i);
            Texture tex = resourceManager.getTexture(ing.getTextureName());
            if (tex != null) {
                batch.draw(tex, startX + (i * (iconSize + padding)), startY, iconSize, iconSize);
            }
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
        if (isChefAction) {
            batch.setColor(0f, 0.8f, 1f, 1f);
        } else {
            if (progress < 0.5f) {
                batch.setColor(1f, 0.8f, 0f, 1f);
            } else if (progress < 0.8f) {
                batch.setColor(0f, 1f, 0f, 1f);
            } else {
                batch.setColor(1f, 0.3f, 0f, 1f);
            }
        }
        batch.draw(blank, x, y, width * Math.min(progress, 1f), height);
        batch.setColor(1, 1, 1, 1);
    }

    private TextureRegion getChefFrame(Chef c) {
        Animation<TextureRegion> targetAnim = idleDown;
        if (c.isMoving) {
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
            case 'R': 
                if (station instanceof CookingStation) {
                    CookingStation cs = (CookingStation) station;
                    if (cs.getStoveType() == CookingStation.StoveType.LEFT) {
                        return "stations/stove_left.png";
                    } else {
                        return "stations/stove_right.png";
                    }
                }
                return "stations/stove_right.png";
            case 'A': return "stations/assembly.png";
            case 'I': 
                if (station instanceof IngredientStorage) {
                    String name = ((IngredientStorage) station).getIngredientName();
                    if (name != null) {
                        String lower = name.toLowerCase();
                        if (lower.contains("bun")) return "stations/crate_bread.png";
                        if (lower.contains("meat")) return "stations/crate_meat.png";
                        if (lower.contains("cheese")) return "stations/crate_cheese.png";
                        if (lower.contains("lettuce")) return "stations/crate_lettuce.png";
                        if (lower.contains("tomato")) return "stations/crate_tomato.png";
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
    
    public void dispose() {
        if (shadowTexture != null) shadowTexture.dispose();
        if (snowTexture != null) snowTexture.dispose();
        if (pixelTexture != null) pixelTexture.dispose();
    }
}