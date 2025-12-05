package com.nimonscooked.view.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.nimonscooked.NimonscookedGame;
import com.nimonscooked.config.GameConfig;
import com.nimonscooked.manager.AudioManager;
import com.nimonscooked.manager.GameManager;
import com.nimonscooked.manager.ResourceManager;

public class ResultScreen extends ScreenAdapter {
    private int finalScore;
    private boolean isWin;
    private BitmapFont font;
    private GlyphLayout layout = new GlyphLayout();
    private Texture pixelTexture;
    
    private float blinkTimer = 0f;
    private boolean showHint = true;
    private static final float BLINK_INTERVAL = 0.5f;

    public ResultScreen(int score, boolean isWin) {
        this.finalScore = score;
        this.isWin = isWin;
        this.font = ResourceManager.getInstance().getCustomFont();
        this.pixelTexture = createPixelTexture();
        
        if (isWin) {
            AudioManager.getInstance().playSound("sfx/delivery_success.wav");
        } else {
            AudioManager.getInstance().playSound("sfx/delivery_fail.wav");
        }
    }

    private Texture createPixelTexture() {
        com.badlogic.gdx.graphics.Pixmap pixmap = 
            new com.badlogic.gdx.graphics.Pixmap(1, 1, 
                com.badlogic.gdx.graphics.Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.WHITE);
        pixmap.fill();
        Texture tex = new Texture(pixmap);
        pixmap.dispose();
        return tex;
    }

    @Override
    public void render(float delta) {
        blinkTimer += delta;
        if (blinkTimer >= BLINK_INTERVAL) {
            blinkTimer = 0f;
            showHint = !showHint;
        }

        Gdx.gl.glClearColor(0.1f, 0.1f, 0.1f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        NimonscookedGame.instance.batch.begin();

        drawBackground();

        String title = isWin ? "STAGE CLEARED!" : "STAGE FAILED";
        String scoreText = "Final Score: " + finalScore;
        String gradeText = getGrade(finalScore);
        String hint = "Press ENTER to Return to Menu";

        font.getData().setScale(2.5f);
        font.setColor(isWin ? Color.GREEN : Color.RED);
        layout.setText(font, title);
        font.draw(NimonscookedGame.instance.batch, title,
                (GameConfig.SCREEN_WIDTH - layout.width) / 2,
                GameConfig.SCREEN_HEIGHT / 2 + 150);

        font.getData().setScale(2.0f);
        font.setColor(Color.YELLOW);
        layout.setText(font, gradeText);
        font.draw(NimonscookedGame.instance.batch, gradeText,
                (GameConfig.SCREEN_WIDTH - layout.width) / 2,
                GameConfig.SCREEN_HEIGHT / 2 + 80);

        font.getData().setScale(1.5f);
        font.setColor(Color.WHITE);
        layout.setText(font, scoreText);
        font.draw(NimonscookedGame.instance.batch, scoreText,
                (GameConfig.SCREEN_WIDTH - layout.width) / 2,
                GameConfig.SCREEN_HEIGHT / 2);

        if (showHint) {
            font.getData().setScale(1.0f);
            font.setColor(Color.LIGHT_GRAY);
            layout.setText(font, hint);
            font.draw(NimonscookedGame.instance.batch, hint,
                    (GameConfig.SCREEN_WIDTH - layout.width) / 2,
                    100);
        }

        NimonscookedGame.instance.batch.end();

        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            AudioManager.getInstance().playSound("sfx/catch.mp3");
            GameManager.getInstance().reset();
            NimonscookedGame.instance.setScreen(new MainMenuScreen());
        }
    }

    private void drawBackground() {
        NimonscookedGame.instance.batch.setColor(0, 0, 0, 0.8f);
        NimonscookedGame.instance.batch.draw(pixelTexture, 
            0, 0, 
            GameConfig.SCREEN_WIDTH, 
            GameConfig.SCREEN_HEIGHT);
        NimonscookedGame.instance.batch.setColor(Color.WHITE);
    }

    private String getGrade(int score) {
        if (score >= 500) return "S";
        if (score >= 400) return "A";
        if (score >= 300) return "B";
        if (score >= 200) return "C";
        if (score >= 100) return "D";
        return "F";
    }

    @Override
    public void dispose() {
        pixelTexture.dispose();
    }
}