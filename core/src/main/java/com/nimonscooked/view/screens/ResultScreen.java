package com.nimonscooked.view.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.nimonscooked.NimonscookedGame;
import com.nimonscooked.config.GameConfig;
import com.nimonscooked.manager.GameManager;
import com.nimonscooked.manager.ResourceManager;

public class ResultScreen extends ScreenAdapter {
    private int finalScore;
    private boolean isWin;
    private BitmapFont font;
    private GlyphLayout layout = new GlyphLayout();

    public ResultScreen(int score, boolean isWin) {
        this.finalScore = score;
        this.isWin = isWin;
        this.font = ResourceManager.getInstance().getCustomFont();
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.1f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        NimonscookedGame.instance.batch.begin();

        String title = isWin ? "STAGE CLEARED!" : "STAGE FAILED";
        String scoreText = "Final Score: " + finalScore;
        String hint = "Press ENTER to Return to Menu";

        font.getData().setScale(2.0f);
        if (isWin) {
            font.setColor(0, 1, 0, 1); // Hijau
        } else {
            font.setColor(1, 0, 0, 1); // Merah
        }
        layout.setText(font, title);
        font.draw(NimonscookedGame.instance.batch, title,
            (GameConfig.SCREEN_WIDTH - layout.width) / 2,
            GameConfig.SCREEN_HEIGHT / 2 + 100);

        font.getData().setScale(1.5f);
        font.setColor(1, 1, 1, 1);
        layout.setText(font, scoreText);
        font.draw(NimonscookedGame.instance.batch, scoreText,
            (GameConfig.SCREEN_WIDTH - layout.width) / 2,
            GameConfig.SCREEN_HEIGHT / 2);

        font.getData().setScale(1.0f);
        layout.setText(font, hint);
        font.draw(NimonscookedGame.instance.batch, hint,
            (GameConfig.SCREEN_WIDTH - layout.width) / 2,
            GameConfig.SCREEN_HEIGHT / 2 - 100);

        NimonscookedGame.instance.batch.end();

        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            // Reset game state dan kembali ke menu
            GameManager.getInstance().reset();
            NimonscookedGame.instance.setScreen(new MainMenuScreen());
        }
    }
}
