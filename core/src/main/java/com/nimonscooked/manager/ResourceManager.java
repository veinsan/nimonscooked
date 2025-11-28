package com.nimonscooked.manager;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Disposable;

public class ResourceManager implements Disposable {
    private static final ResourceManager instance = new ResourceManager();
    public static ResourceManager getInstance() { return instance; }

    public final AssetManager assetManager;

    public static final String UI_SKIN_PATH = "ui/freezing-ui.json";
    public static final String CUSTOM_FONT_PATH = "font/pixel.fnt";
    public static final String TEXTURE_ROOT = "textures/";
    public static final String AUDIO_ROOT = "audio/";

    private ResourceManager() {
        assetManager = new AssetManager();
    }

    public void loadAll() {
        assetManager.load(UI_SKIN_PATH, Skin.class);
        assetManager.load(CUSTOM_FONT_PATH, BitmapFont.class);

        loadTexture("ui/title_bg.png");
        loadTexture("ui/warning.png");
        loadTexture("ui/coin.png");
        loadTexture("ui/wasd.png");
        loadTexture("ui/key_1.png");
        loadTexture("ui/key_e.png");
        loadTexture("ui/key_f.png");
        loadTexture("ui/a.png");

        loadTexture("chef/chef_idle_down.png");
        loadTexture("chef/chef_idle_up.png");
        loadTexture("chef/chef_idle_side.png");
        loadTexture("chef/chef_walk_down.png");
        loadTexture("chef/chef_walk_up.png");
        loadTexture("chef/chef_walk_side.png");
        loadTexture("chef/chef_chop.png");

        loadTexture("stations/floor.png");
        loadTexture("stations/wall.png");
        loadTexture("stations/counter.png");
        loadTexture("stations/cutting_board.png");
        loadTexture("stations/stove.png");
        loadTexture("stations/stove_active.png");
        loadTexture("stations/oven.png");
        loadTexture("stations/oven_active.png");
        loadTexture("stations/crate.png");
        loadTexture("stations/delivery.png");
        loadTexture("stations/sink.png");
        loadTexture("stations/trash.png");
        loadTexture("stations/plate_shelf.png");
        loadTexture("stations/plant.png");

        loadTexture("items/plate.png");
        loadTexture("items/plate_dirty.png");
        loadTexture("items/pan.png");

        loadTexture("ingredients/bun.png");
        loadTexture("ingredients/meat_raw.png");
        loadTexture("ingredients/meat_cooked.png");
        loadTexture("ingredients/meat_burnt.png");
        loadTexture("ingredients/cheese.png");
        loadTexture("ingredients/cheese_chopped.png");
        loadTexture("ingredients/tomato.png");
        loadTexture("ingredients/tomato_chopped.png");
        loadTexture("ingredients/lettuce.png");
        loadTexture("ingredients/lettuce_chopped.png");
        loadTexture("ingredients/burger_complete.png");

        loadTexture("vfx/smoke.png");
        loadTexture("vfx/fire.png");
        loadTexture("vfx/sparkle.png");
        loadTexture("vfx/circle.png");

        for (int i = 1; i <= 5; i++) {
            loadTexture("customer/" + i + "_neutral.png");
            loadTexture("customer/" + i + "_angry.png");
        }

        loadMusic("music/bgm_menu.mp3");
        loadMusic("music/bgm_game.mp3");
        loadMusic("music/fire_loop.mp3");

        loadSound("sfx/chop.mp3");
        loadSound("sfx/fry.mp3");
        loadSound("sfx/catch.mp3");

        loadSound("sfx/delivery_success.wav");
        loadSound("sfx/delivery_fail.wav");
        loadSound("sfx/alarm.wav");
        loadSound("sfx/extinguisher.wav");
        loadSound("sfx/trash.wav");

        assetManager.finishLoading();
    }

    private void loadTexture(String path) {
        try {
            assetManager.load(TEXTURE_ROOT + path, Texture.class);
        } catch (Exception e) {
            Gdx.app.error("ResourceManager", "Failed queueing texture: " + path);
        }
    }

    private void loadMusic(String path) {
        assetManager.load(AUDIO_ROOT + path, Music.class);
    }

    private void loadSound(String path) {
        assetManager.load(AUDIO_ROOT + path, Sound.class);
    }

    public Texture getTexture(String fileName) {
        String fullPath = TEXTURE_ROOT + fileName;
        if (assetManager.isLoaded(fullPath)) return assetManager.get(fullPath, Texture.class);
        return null;
    }

    public Music getMusic(String fileName) {
        String fullPath = AUDIO_ROOT + fileName;
        if (assetManager.isLoaded(fullPath)) return assetManager.get(fullPath, Music.class);
        return null;
    }

    public Sound getSound(String fileName) {
        String fullPath = AUDIO_ROOT + fileName;
        if (assetManager.isLoaded(fullPath)) return assetManager.get(fullPath, Sound.class);
        return null;
    }

    public Skin getSkin() { return assetManager.get(UI_SKIN_PATH, Skin.class); }
    public BitmapFont getCustomFont() { return assetManager.get(CUSTOM_FONT_PATH, BitmapFont.class); }

    @Override
    public void dispose() {
        assetManager.dispose();
    }
}