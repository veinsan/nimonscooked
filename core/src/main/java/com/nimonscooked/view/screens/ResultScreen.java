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
    private Texture backgroundTexture; 
    
    private float blinkTimer = 0f;
    private boolean showHint = true;
    private static final float BLINK_INTERVAL = 0.5f;

    public ResultScreen(int score, boolean isWin) {
        this.finalScore = score;
        this.isWin = isWin;
        this.font = ResourceManager.getInstance().getCustomFont();
        
        // Ensure "ui/score.png" is loaded in ResourceManager!
        this.backgroundTexture = ResourceManager.getInstance().getTexture("ui/score.png");
        
        if (isWin) {
            AudioManager.getInstance().playSound("sfx/delivery_success.wav");
        } else {
            AudioManager.getInstance().playSound("sfx/delivery_fail.wav");
        }
    }

    @Override
    public void render(float delta) {
        blinkTimer += delta;
        if (blinkTimer >= BLINK_INTERVAL) {
            blinkTimer = 0f;
            showHint = !showHint;
        }

        // Clear screen
        Gdx.gl.glClearColor(0f, 0f, 0f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        NimonscookedGame.instance.batch.begin();

        // 1. Draw the Background
        if (backgroundTexture != null) {
            NimonscookedGame.instance.batch.setColor(Color.WHITE);
            NimonscookedGame.instance.batch.draw(backgroundTexture, 0, 0, GameConfig.SCREEN_WIDTH, GameConfig.SCREEN_HEIGHT);
        }

        float centerX = GameConfig.SCREEN_WIDTH / 2f;
        float centerY = GameConfig.SCREEN_HEIGHT / 2f;

        // 2. Draw Score Number (WHITE with Outline)
        String scoreNum = String.valueOf(finalScore);
        font.getData().setScale(3.5f);
        layout.setText(font, scoreNum);
        
        drawTextWithOutline(scoreNum, 
            centerX - layout.width / 2f, 
            centerY + 60, 
            4f,
            Color.WHITE); // Pass WHITE explicitly

        // 3. Draw Grade (RED if lost, WHITE if win)
        String gradeText = "Grade: " + getGrade(finalScore);
        font.getData().setScale(1.8f);
        layout.setText(font, gradeText);
        
        // Determine the color based on win/loss state
        Color gradeColor = isWin ? Color.WHITE : Color.RED;
        
        drawTextWithOutline(gradeText, 
            centerX - layout.width / 2f, 
            centerY - 100, 
            2f,
            gradeColor); // Pass the calculated color

        // 4. Draw Hint at bottom
        if (showHint) {
            String hint = "Press ENTER to Continue";
            font.getData().setScale(1.0f);
            font.setColor(Color.LIGHT_GRAY); 
            layout.setText(font, hint);
            font.draw(NimonscookedGame.instance.batch, hint, 
                centerX - layout.width / 2f, 
                100);
        }

        NimonscookedGame.instance.batch.end();

        // Input Handling
        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            AudioManager.getInstance().playSound("sfx/catch.mp3");
            GameManager.getInstance().reset();
            NimonscookedGame.instance.setScreen(new MainMenuScreen());
        }
    }

    /**
     * Helper to draw text with a black border (outline).
     * Now accepts a Color parameter for the main text.
     */
    private void drawTextWithOutline(String text, float x, float y, float thickness, Color color) {
        font.setColor(Color.BLACK);
        // Draw offsets for shadow/outline effect
        font.draw(NimonscookedGame.instance.batch, text, x - thickness, y);
        font.draw(NimonscookedGame.instance.batch, text, x + thickness, y);
        font.draw(NimonscookedGame.instance.batch, text, x, y - thickness);
        font.draw(NimonscookedGame.instance.batch, text, x, y + thickness);
        
        // Draw main text on top with the specified color
        font.setColor(color);
        font.draw(NimonscookedGame.instance.batch, text, x, y);
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
        // Texture managed by ResourceManager
    }
}