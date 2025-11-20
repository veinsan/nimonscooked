package com.nimonscooked;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.nimonscooked.manager.GameManager;
import com.nimonscooked.manager.ResourceManager;
import com.nimonscooked.view.screens.MainMenuScreen;

public class NimonscookedGame extends Game {
    public static NimonscookedGame instance;
    public SpriteBatch batch;

    @Override
    public void create() {
        instance = this;
        batch = new SpriteBatch();

        Gdx.app.log("Nimonscooked", "Engine Starting...");

        try {
            // 1. Load Assets
            ResourceManager.getInstance().loadAll();
            Gdx.app.log("Nimonscooked", "Assets Loaded Successfully.");

            // 2. Load Map
            // Pastikan file map_c.txt ada di assets/data/
            com.nimonscooked.manager.MapManager.getInstance().loadMap("data/map_c.txt");

            // 3. Init Logic
            GameManager.getInstance();

        } catch (Exception e) {
            Gdx.app.error("Nimonscooked", "CRITICAL ERROR Loading Assets", e);
            Gdx.app.exit();
        }

        setScreen(new MainMenuScreen());
    }

    @Override
    public void render() {
        super.render();
    }

    @Override
    public void dispose() {
        if (batch != null) batch.dispose();
        ResourceManager.getInstance().dispose();
        Gdx.app.log("Nimonscooked", "Game Disposed.");
    }
}
