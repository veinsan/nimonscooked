package com.nimonscooked.manager;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Disposable;

public class ResourceManager implements Disposable {
    private static final ResourceManager instance = new ResourceManager();
    public static ResourceManager getInstance() { return instance; }

    public final AssetManager assetManager;

    // Sesuaikan nama file UI & Font kamu di sini
    public static final String UI_SKIN_PATH = "ui/freezing-ui.json";
    public static final String CUSTOM_FONT_PATH = "font/pixel.fnt";
    public static final String TEXTURE_ROOT = "textures/";

    private ResourceManager() {
        assetManager = new AssetManager();
    }

    public void loadAll() {
        assetManager.load(UI_SKIN_PATH, Skin.class);
        assetManager.load(CUSTOM_FONT_PATH, BitmapFont.class);

        // Textures
        loadTexture("chef/chef_idle.png");
        loadTexture("chef/chef_walk.png");
        loadTexture("chef/chef_chop.png");

        loadTexture("stations/floor.png");
        loadTexture("stations/wall.png");
        loadTexture("stations/counter.png");
        loadTexture("stations/cutting_board.png");
        loadTexture("stations/stove.png");
        loadTexture("stations/crate.png");
        loadTexture("stations/delivery.png");
        loadTexture("stations/sink.png");
        loadTexture("stations/trash.png");

        loadTexture("items/plate.png");
        loadTexture("items/plate_dirty.png");
        loadTexture("items/pan.png");
        loadTexture("items/pan_meat.png");
        loadTexture("items/pan_meat_cooked.png");

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

        assetManager.finishLoading();
    }

    private void loadTexture(String path) {
        assetManager.load(TEXTURE_ROOT + path, Texture.class);
    }

    public Texture getTexture(String fileName) {
        String fullPath = TEXTURE_ROOT + fileName;
        if (assetManager.isLoaded(fullPath)) {
            return assetManager.get(fullPath, Texture.class);
        }
        return null;
    }

    public Skin getSkin() { return assetManager.get(UI_SKIN_PATH, Skin.class); }
    public BitmapFont getCustomFont() { return assetManager.get(CUSTOM_FONT_PATH, BitmapFont.class); }

    @Override
    public void dispose() { assetManager.dispose(); }
}
