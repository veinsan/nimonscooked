package com.nimonscooked;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.nimonscooked.config.GameConfig;
import com.nimonscooked.manager.AudioManager;
import com.nimonscooked.manager.GameManager;
import com.nimonscooked.manager.MapManager;
import com.nimonscooked.manager.ResourceManager;
import com.nimonscooked.view.screens.MainMenuScreen;

public class NimonscookedGame extends Game {
    public static NimonscookedGame instance;
    public SpriteBatch batch;

    private boolean initialized = false;

    @Override
    public void create() {
        instance = this;
        
        Gdx.app.log("Nimonscooked", "=================================");
        Gdx.app.log("Nimonscooked", "Engine Starting...");
        Gdx.app.log("Nimonscooked", "LibGDX Version: " + com.badlogic.gdx.Version.VERSION);
        Gdx.app.log("Nimonscooked", "=================================");

        try {
            initializeEngine();
            initialized = true;
            Gdx.app.log("Nimonscooked", "Engine Initialized Successfully");
        } catch (Exception e) {
            Gdx.app.error("Nimonscooked", "CRITICAL ERROR During Initialization", e);
            Gdx.app.exit();
        }
    }

    private void initializeEngine() {
        batch = new SpriteBatch();
        Gdx.app.log("Nimonscooked", "SpriteBatch created");

        Gdx.app.log("Nimonscooked", "Loading assets...");
        ResourceManager.getInstance().loadAll();
        Gdx.app.log("Nimonscooked", "Assets loaded successfully");

        Gdx.app.log("Nimonscooked", "Initializing audio...");
        AudioManager audioManager = AudioManager.getInstance();
        audioManager.setMasterVolume(GameConfig.MASTER_VOLUME);
        audioManager.setMusicVolume(GameConfig.MUSIC_VOLUME);
        audioManager.setSfxVolume(GameConfig.SFX_VOLUME);
        Gdx.app.log("Nimonscooked", "Audio initialized");

        Gdx.app.log("Nimonscooked", "Loading recipes...");
        com.nimonscooked.factory.StationFactory.initializeRecipes(GameConfig.RECIPES_PATH);
        Gdx.app.log("Nimonscooked", "Recipes loaded");

        Gdx.app.log("Nimonscooked", "Loading map...");
        MapManager.getInstance().loadMap(GameConfig.DEFAULT_MAP_PATH);
        Gdx.app.log("Nimonscooked", "Map loaded: " + 
            MapManager.getInstance().currentMap.getWidth() + "x" + 
            MapManager.getInstance().currentMap.getHeight());

        Gdx.app.log("Nimonscooked", "Initializing game manager...");
        GameManager.getInstance();
        Gdx.app.log("Nimonscooked", "Game manager initialized");

        Gdx.app.log("Nimonscooked", "Setting main menu screen...");
        setScreen(new MainMenuScreen());
    }

    @Override
    public void render() {
        try {
            super.render();
        } catch (Exception e) {
            Gdx.app.error("Nimonscooked", "Error during render", e);
        }
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
        Gdx.app.log("Nimonscooked", "Window resized to: " + width + "x" + height);
    }

    @Override
    public void pause() {
        super.pause();
        Gdx.app.log("Nimonscooked", "Game paused");
    }

    @Override
    public void resume() {
        super.resume();
        Gdx.app.log("Nimonscooked", "Game resumed");
    }

    @Override
    public void dispose() {
        Gdx.app.log("Nimonscooked", "=================================");
        Gdx.app.log("Nimonscooked", "Disposing game resources...");

        if (batch != null) {
            batch.dispose();
            Gdx.app.log("Nimonscooked", "SpriteBatch disposed");
        }

        try {
            GameManager.getInstance().dispose();
            Gdx.app.log("Nimonscooked", "GameManager disposed");
        } catch (Exception e) {
            Gdx.app.error("Nimonscooked", "Error disposing GameManager", e);
        }

        try {
            MapManager.getInstance().dispose();
            Gdx.app.log("Nimonscooked", "MapManager disposed");
        } catch (Exception e) {
            Gdx.app.error("Nimonscooked", "Error disposing MapManager", e);
        }

        try {
            ResourceManager.getInstance().dispose();
            Gdx.app.log("Nimonscooked", "ResourceManager disposed");
        } catch (Exception e) {
            Gdx.app.error("Nimonscooked", "Error disposing ResourceManager", e);
        }

        try {
            com.nimonscooked.util.CachePools.reset();
            Gdx.app.log("Nimonscooked", "CachePools reset");
        } catch (Exception e) {
            Gdx.app.error("Nimonscooked", "Error resetting CachePools", e);
        }

        Gdx.app.log("Nimonscooked", "Game Disposed Successfully");
        Gdx.app.log("Nimonscooked", "=================================");
    }

    public boolean isInitialized() {
        return initialized;
    }
}