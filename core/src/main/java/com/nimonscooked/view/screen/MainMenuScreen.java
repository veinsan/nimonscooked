package com.nimonscooked.view.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.nimonscooked.NimonscookedGame;
import com.nimonscooked.config.GameConfig;
import com.nimonscooked.manager.ResourceManager;

public class MainMenuScreen extends ScreenAdapter {
    private BitmapFont font;
    private GlyphLayout layout = new GlyphLayout();

    @Override
    public void show() {
        font = ResourceManager.getInstance().getCustomFont();
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.2f, 0.2f, 0.5f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        NimonscookedGame.instance.batch.begin();
        String text = "PRESS ENTER TO START";
        layout.setText(font, text);
        font.draw(NimonscookedGame.instance.batch, text,
            (GameConfig.SCREEN_WIDTH - layout.width) / 2,
            (GameConfig.SCREEN_HEIGHT + layout.height) / 2);
        NimonscookedGame.instance.batch.end();

        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            NimonscookedGame.instance.setScreen(new GameScreen());
        }
    }
}
