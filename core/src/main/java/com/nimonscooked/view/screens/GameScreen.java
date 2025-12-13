package com.nimonscooked.view.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.nimonscooked.NimonscookedGame;
import com.nimonscooked.config.GameConfig;
import com.nimonscooked.controller.InputHandler;
import com.nimonscooked.controller.PlayerController;
import com.nimonscooked.manager.AudioManager;
import com.nimonscooked.manager.GameManager;
import com.nimonscooked.manager.MapManager;
import com.nimonscooked.model.entity.Chef;
import com.nimonscooked.view.renderer.WorldRenderer;
import com.nimonscooked.view.renderer.HudRenderer;

public class GameScreen extends ScreenAdapter {
    private WorldRenderer worldRenderer;
    private HudRenderer hudRenderer;

    private InputHandler inputHandler;
    private PlayerController playerController;

    private OrthographicCamera camera;
    private Viewport viewport;

    public GameScreen() {
        camera = new OrthographicCamera();
        camera.setToOrtho(false, GameConfig.SCREEN_WIDTH, GameConfig.SCREEN_HEIGHT);
        
        viewport = new ExtendViewport(GameConfig.SCREEN_WIDTH, GameConfig.SCREEN_HEIGHT, camera);
        viewport.apply();
        
        camera.zoom = 0.6f;

        worldRenderer = new WorldRenderer();
        hudRenderer = new HudRenderer(NimonscookedGame.instance.batch);

        inputHandler = new InputHandler();
        playerController = new PlayerController(inputHandler);

        Gdx.input.setInputProcessor(inputHandler);

        centerCameraOnActiveChef();

        AudioManager.getInstance().playMusic("music/bgm_game.mp3");
    }

    private void centerCameraOnActiveChef() {
        Chef activeChef = MapManager.getInstance().activeChef;
        if (activeChef != null) {
            camera.position.set(
                activeChef.visualPos.x * GameConfig.TILE_SIZE, 
                activeChef.visualPos.y * GameConfig.TILE_SIZE, 
                0
            );
        } else {
            int mapWidth = MapManager.getInstance().currentMap.getWidth() * GameConfig.TILE_SIZE;
            int mapHeight = MapManager.getInstance().currentMap.getHeight() * GameConfig.TILE_SIZE;
            camera.position.set(mapWidth / 2f, mapHeight / 2f, 0);
        }
        camera.update();
    }

    private void updateCamera(float delta) {
        Chef activeChef = MapManager.getInstance().activeChef;
        if (activeChef == null) return;

        float targetX = activeChef.visualPos.x * GameConfig.TILE_SIZE;
        float targetY = activeChef.visualPos.y * GameConfig.TILE_SIZE;

        float lerpSpeed = GameConfig.CAMERA_LERP_SPEED * delta;
        camera.position.x += (targetX - camera.position.x) * lerpSpeed;
        camera.position.y += (targetY - camera.position.y) * lerpSpeed;

        camera.update();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
        hudRenderer.resize(width, height);
        centerCameraOnActiveChef();
    }

    @Override
    public void render(float delta) {
        playerController.update(delta);
        GameManager.getInstance().update(delta);

        updateCamera(delta);

        Gdx.gl.glClearColor(0.05f, 0.05f, 0.1f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        NimonscookedGame.instance.batch.setProjectionMatrix(camera.combined);
        
        NimonscookedGame.instance.batch.begin();
        worldRenderer.render(NimonscookedGame.instance.batch);
        NimonscookedGame.instance.batch.end();

        hudRenderer.render();

        com.nimonscooked.util.CachePools.freeAll();
    }

    @Override
    public void dispose() {
        if (worldRenderer != null) worldRenderer.dispose();
        if (hudRenderer != null) hudRenderer.dispose();
    }
}