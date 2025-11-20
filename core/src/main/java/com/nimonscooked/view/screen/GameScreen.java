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
import com.nimonscooked.manager.MapManager;
import com.nimonscooked.view.renderer.WorldRenderer;

public class GameScreen extends ScreenAdapter {
    private WorldRenderer worldRenderer;
    private InputHandler inputHandler;
    private PlayerController playerController;

    // Kamera & Viewport
    private OrthographicCamera camera;
    private Viewport viewport;

    public GameScreen() {
        // 1. Setup Kamera
        camera = new OrthographicCamera();

        // FitViewport: Menjaga aspek rasio, ada black bar kalau window ditarik aneh
        // Kita set ukuran dunia game sesuai ukuran layar config
        viewport = new ExtendViewport(GameConfig.SCREEN_WIDTH, GameConfig.SCREEN_HEIGHT, camera);

        // Posisikan kamera di tengah layar
        viewport.apply();
        camera.position.set(GameConfig.SCREEN_WIDTH / 2f, GameConfig.SCREEN_HEIGHT / 2f, 0);

        this.worldRenderer = new WorldRenderer();
        this.inputHandler = new InputHandler();
        this.playerController = new PlayerController(inputHandler);

        Gdx.input.setInputProcessor(inputHandler);

        // Center Map Logic (Biar peta otomatis di tengah, berapapun ukuran layarnya)
        centerCameraOnMap();
    }

    private void centerCameraOnMap() {
        int mapWidth = MapManager.getInstance().currentMap.getWidth() * GameConfig.TILE_SIZE;
        int mapHeight = MapManager.getInstance().currentMap.getHeight() * GameConfig.TILE_SIZE;

        // Geser kamera supaya titik (0,0) peta ada di tengah viewport,
        // atau biarkan kamera default tapi renderer yang geser.
        // Cara paling gampang: Kita geser kamera ke tengah-tengah MAP.

        camera.position.set(mapWidth / 2f, mapHeight / 2f, 0);
        camera.update();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
        centerCameraOnMap(); // Recenter kalau window di-resize
    }

    @Override
    public void render(float delta) {
        playerController.update(delta);

        // Update Kamera
        camera.update();
        // Set Batch agar menggunakan matriks kamera kita
        NimonscookedGame.instance.batch.setProjectionMatrix(camera.combined);

        Gdx.gl.glClearColor(0.1f, 0.1f, 0.1f, 1); // Warna background abu-abu tua biar enak
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        NimonscookedGame.instance.batch.begin();
        worldRenderer.render(NimonscookedGame.instance.batch);
        NimonscookedGame.instance.batch.end();
    }
}
