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
import com.nimonscooked.manager.AudioManager; // Import AudioManager
import com.nimonscooked.manager.GameManager;
import com.nimonscooked.manager.MapManager;
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
        viewport = new ExtendViewport(GameConfig.SCREEN_WIDTH, GameConfig.SCREEN_HEIGHT, camera);

        viewport.apply();
        // Zoom kamera
        camera.zoom = 0.5f;

        // Init Renderers
        this.worldRenderer = new WorldRenderer();
        this.hudRenderer = new HudRenderer(NimonscookedGame.instance.batch);

        this.inputHandler = new InputHandler();
        this.playerController = new PlayerController(inputHandler);

        Gdx.input.setInputProcessor(inputHandler);
        centerCameraOnMap();

        // --- FIX: PANGGIL MUSIK DI SINI (DI DALAM KURUNG KURAWAL CONSTRUCTOR) ---
        AudioManager.getInstance().playMusic("music/bgm_game.mp3");
    }

    private void centerCameraOnMap() {
        int mapWidth = MapManager.getInstance().currentMap.getWidth() * GameConfig.TILE_SIZE;
        int mapHeight = MapManager.getInstance().currentMap.getHeight() * GameConfig.TILE_SIZE;
        camera.position.set(mapWidth / 2f, mapHeight / 2f, 0);
        camera.update();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
        hudRenderer.resize(width, height);
        centerCameraOnMap();
    }

    @Override
    public void render(float delta) {
        // 1. Update Logic
        playerController.update(delta);
        GameManager.getInstance().update(delta);

        // 2. Render World
        camera.update();
        NimonscookedGame.instance.batch.setProjectionMatrix(camera.combined);

        Gdx.gl.glClearColor(0.1f, 0.1f, 0.1f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        NimonscookedGame.instance.batch.begin();
        worldRenderer.render(NimonscookedGame.instance.batch);
        NimonscookedGame.instance.batch.end();

        // 3. Render UI
        hudRenderer.render();
    }

    @Override
    public void dispose() {
        // Cleanup resources if needed
    }
}
