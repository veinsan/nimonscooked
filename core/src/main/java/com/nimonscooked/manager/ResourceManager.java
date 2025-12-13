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

        loadTexture("chef/chef_idle_down.png");
        loadTexture("chef/chef_idle_side.png");
        loadTexture("chef/chef_idle_up.png");
        loadTexture("chef/chef_walk_down.png");
        loadTexture("chef/chef_walk_side.png");
        loadTexture("chef/chef_walk_up.png");

        loadTexture("ingredients/blt_burger.png");
        loadTexture("ingredients/bun.png");
        loadTexture("ingredients/burger_random.png");
        loadTexture("ingredients/cheese.png");
        loadTexture("ingredients/cheese_burger.png");
        loadTexture("ingredients/cheese_chopped.png");
        loadTexture("ingredients/classic_burger.png");
        loadTexture("ingredients/deluxe_burger.png");
        loadTexture("ingredients/lettuce.png");
        loadTexture("ingredients/lettuce_chopped.png");
        loadTexture("ingredients/meat_burnt.png");
        loadTexture("ingredients/meat_chopped.png");
        loadTexture("ingredients/meat_cooked.png");
        loadTexture("ingredients/meat_raw.png");
        loadTexture("ingredients/tomato.png");
        loadTexture("ingredients/tomato_chopped.png");

        loadTexture("items/plate.png");
        loadTexture("items/plate_dirty.png");

        loadTexture("stations/assembly.png");
        loadTexture("stations/crate.png");
        loadTexture("stations/crate_bread.png");
        loadTexture("stations/crate_cheese.png");
        loadTexture("stations/crate_lettuce.png");
        loadTexture("stations/crate_meat.png");
        loadTexture("stations/crate_tomato.png");
        loadTexture("stations/cutting.png");
        loadTexture("stations/cutting_active.png");
        loadTexture("stations/floor.png");
        loadTexture("stations/floor2.png");
        loadTexture("stations/plate_storage.png");
        loadTexture("stations/serving.png");
        loadTexture("stations/sink.png");
        loadTexture("stations/sink_active.png");

        loadTexture("stations/stove_active_left.png");
        loadTexture("stations/stove_active_right.png");
        loadTexture("stations/stove_left.png");
        loadTexture("stations/stove_right.png");
        loadTexture("stations/stove_cooked_left.png");
        loadTexture("stations/stove_cooked_right.png");

        loadTexture("stations/trash.png");
        loadTexture("stations/wall.png");

        loadTexture("ui/title_bg.png");
        loadTexture("ui/bg_winter.jpg");
        loadTexture("ui/score.png");
        loadTexture("ui/time.png");
        loadTexture("ui/order.png");
        loadTexture("ui/score_current.png");

        loadMusic("music/bgm_game.mp3");
        loadMusic("music/bgm_menu.mp3");
        loadMusic("music/bgm_gameover.mp3");

        loadSound("sfx/alarm.wav");
        loadSound("sfx/catch.mp3");
        loadSound("sfx/chop.mp3");
        loadSound("sfx/delivery_fail.wav");
        loadSound("sfx/delivery_success.wav");
        loadSound("sfx/done.mp3");
        loadSound("sfx/fry.mp3");
        loadSound("sfx/trash.wav");
        loadSound("sfx/click.mp3");
        loadSound("sfx/hover.mp3");

        loadTexture("effects/smoke.png");
        loadTexture("effects/chop.png");


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
        if (assetManager.isLoaded(fullPath)) {
            return assetManager.get(fullPath, Texture.class);
        }
        return null;
    }

    public Music getMusic(String fileName) {
        String fullPath = AUDIO_ROOT + fileName;
        if (assetManager.isLoaded(fullPath)) {
            return assetManager.get(fullPath, Music.class);
        }
        return null;
    }

    public Sound getSound(String fileName) {
        String fullPath = AUDIO_ROOT + fileName;
        if (assetManager.isLoaded(fullPath)) {
            return assetManager.get(fullPath, Sound.class);
        }
        return null;
    }

    public Skin getSkin() {
        return assetManager.get(UI_SKIN_PATH, Skin.class);
    }

    public BitmapFont getCustomFont() {
        return assetManager.get(CUSTOM_FONT_PATH, BitmapFont.class);
    }

    @Override
    public void dispose() {
        assetManager.dispose();
    }
}