package com.nimonscooked.manager;

import com.badlogic.gdx.graphics.Texture;

import java.util.HashMap;

public class TextureManager {

    private static TextureManager instance;

    // Map penyimpanan
    private HashMap<String, Texture> textures = new HashMap<>();

    private TextureManager() {}

    public static TextureManager get() {
        if (instance == null) instance = new TextureManager();
        return instance;
    }

    // ========= LOAD =========
    public void load(String key, String path) {
        if (!textures.containsKey(key)) {
            textures.put(key, new Texture(path));
        }
    }

    // ========= GET =========
    public Texture getTexture(String key) {
        return textures.get(key);
    }

    // ========= DISPOSE =========
    public void dispose() {
        for (Texture t : textures.values()) {
            t.dispose();
        }
        textures.clear();
    }
}
