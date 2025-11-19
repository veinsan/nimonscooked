package com.nimonscooked.renderer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.nimonscooked.model.Item;
import com.nimonscooked.model.ingredient.Ingredient;

import java.util.HashMap;

public class ItemRenderer {

    private HashMap<String, Texture> textures = new HashMap<>();
    private Texture fallback;

    public ItemRenderer() {
        if (Gdx.files.internal("item/unknown.png").exists()) {
            fallback = new Texture("item/unknown.png");
        } else {
            fallback = new Texture(1, 1, com.badlogic.gdx.graphics.Pixmap.Format.RGBA8888);
        }

        load("bread", "item/bread.png");
        load("meat_raw", "item/meat_raw.png");
        load("meat_chopped", "item/meat_chopped.png");
        load("meat_cooked", "item/meat_cooked.png");

        load("cheese", "item/cheese.png");
        load("cheese_chopped", "item/cheese_chopped.png");

        load("tomato", "item/tomato.png");
        load("tomato_chopped", "item/tomato_chopped.png");

        load("lettuce", "item/lettuce.png");
        load("lettuce_chopped", "item/lettuce_chopped.png");

        load("plate", "item/plate.png");
        load("frying_pan", "item/frying_pan.png");
        load("cutting_board", "item/cutting_board.png");
    }

    private void load(String key, String path) {
        if (Gdx.files.internal(path).exists()) {
            textures.put(key, new Texture(path));
        } else {
            textures.put(key, fallback);
        }
    }

    public void render(SpriteBatch batch, Item item, float x, float y) {
        if (item == null) return;

        String textureKey;
        if (item instanceof Ingredient ingredient) {
            textureKey = ingredient.getTextureKey();
        } else {
            textureKey = item.getName();
        }

        Texture tex = textures.get(textureKey);
        if (tex == null) {
            tex = fallback;
        }

        batch.draw(tex, x, y, 40, 40);
    }

    public void dispose() {
        for (Texture t : textures.values()) {
            if (t != fallback) t.dispose();
        }
        fallback.dispose();
    }
}