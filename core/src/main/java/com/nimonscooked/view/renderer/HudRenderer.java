package com.nimonscooked.view.renderer;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.nimonscooked.config.GameConfig;
import com.nimonscooked.manager.GameManager;
import com.nimonscooked.manager.MapManager;
import com.nimonscooked.manager.ResourceManager;
import com.nimonscooked.model.entity.Chef;
import com.nimonscooked.model.order.Order;

import java.util.List;

public class HudRenderer {
    private SpriteBatch batch;
    private BitmapFont font;
    private Viewport hudViewport;
    private OrthographicCamera hudCamera;
    private Texture pixelTexture;

    public HudRenderer(SpriteBatch batch) {
        this.batch = batch;
        this.font = ResourceManager.getInstance().getCustomFont();
        this.pixelTexture = createPixelTexture();

        hudCamera = new OrthographicCamera();
        hudViewport = new FitViewport(GameConfig.SCREEN_WIDTH, GameConfig.SCREEN_HEIGHT, hudCamera);
        hudViewport.apply();
        hudCamera.position.set(GameConfig.SCREEN_WIDTH / 2f, GameConfig.SCREEN_HEIGHT / 2f, 0);
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

    public void resize(int width, int height) {
        hudViewport.update(width, height, true);
    }

    public void render() {
        hudCamera.update();
        batch.setProjectionMatrix(hudCamera.combined);
        batch.begin();

        renderTimer();
        renderScore();
        renderOrders();
        renderDashCooldown();
        renderControls();

        batch.end();
    }

    private void renderTimer() {
        font.getData().setScale(1.2f);
        float timer = GameManager.getInstance().getLevelTimer();
        int minutes = (int) timer / 60;
        int seconds = (int) timer % 60;

        String timeStr = String.format("%02d:%02d", minutes, seconds);

        if (timer < 30) {
            font.setColor(1, 0.3f, 0.3f, 1);
        } else if (timer < 60) {
            font.setColor(1, 0.8f, 0.3f, 1);
        } else {
            font.setColor(1, 1, 1, 1);
        }

        GlyphLayout layout = new GlyphLayout(font, timeStr);
        font.draw(batch, timeStr, 
            GameConfig.SCREEN_WIDTH / 2f - layout.width / 2, 
            GameConfig.SCREEN_HEIGHT - 20);
        font.setColor(1, 1, 1, 1);
    }

    private void renderScore() {
        font.getData().setScale(1.2f);
        String scoreStr = "Score: " + GameManager.getInstance().getScore();
        String failsStr = "Fails: " + GameManager.getInstance().getFailedOrders() + "/5";

        font.setColor(1, 1, 1, 1);
        font.draw(batch, scoreStr, 20, GameConfig.SCREEN_HEIGHT - 20);

        if (GameManager.getInstance().getFailedOrders() > 2) {
            font.setColor(1, 0, 0, 1);
        }
        font.draw(batch, failsStr, GameConfig.SCREEN_WIDTH - 150, GameConfig.SCREEN_HEIGHT - 20);
        font.setColor(1, 1, 1, 1);
    }

    private void renderOrders() {
        List<Order> orders = GameManager.getInstance().orderManager.getActiveOrders();
        float startY = GameConfig.SCREEN_HEIGHT - 80;
        float orderHeight = 50;

        font.getData().setScale(0.9f);

        for (int i = 0; i < orders.size(); i++) {
            Order order = orders.get(i);
            float y = startY - (i * orderHeight);

            renderOrderCard(order, 20, y);
        }

        font.setColor(1, 1, 1, 1);
    }

    private void renderOrderCard(Order order, float x, float y) {
        float cardWidth = 250;
        float cardHeight = 40;

        batch.setColor(0, 0, 0, 0.7f);
        batch.draw(pixelTexture, x, y - cardHeight, cardWidth, cardHeight);

        batch.setColor(0.2f, 0.2f, 0.2f, 1f);
        batch.draw(pixelTexture, x + 2, y - cardHeight + 2, cardWidth - 4, cardHeight - 4);

        String recipeName = order.getRecipeName();
        int timeRemaining = (int) order.getRemainingTime();
        String timerStr = String.format("(%ds)", timeRemaining);

        Color textColor = order.getProgressColor();
        font.setColor(textColor);
        font.draw(batch, recipeName, x + 8, y - 10);

        font.getData().setScale(0.7f);
        font.draw(batch, timerStr, x + 8, y - 25);
        font.getData().setScale(0.9f);

        float progressBarWidth = cardWidth - 16;
        float progressBarHeight = 4;
        float progressBarX = x + 8;
        float progressBarY = y - cardHeight + 8;

        batch.setColor(0.3f, 0.3f, 0.3f, 1f);
        batch.draw(pixelTexture, progressBarX, progressBarY, progressBarWidth, progressBarHeight);

        batch.setColor(order.getProgressColor());
        float progress = order.getProgressPercentage();
        batch.draw(pixelTexture, progressBarX, progressBarY, 
            progressBarWidth * progress, progressBarHeight);

        batch.setColor(1, 1, 1, 1);
    }

    private void renderDashCooldown() {
        Chef activeChef = MapManager.getInstance().activeChef;
        if (activeChef == null) return;

        float cooldown = activeChef.getDashCooldown();
        if (cooldown > 0) {
            font.getData().setScale(0.8f);
            font.setColor(1, 0.5f, 0, 1);
            String dashText = String.format("Dash: %.1fs", cooldown);
            font.draw(batch, dashText, 20, 80);
            font.setColor(1, 1, 1, 1);
        }
    }

    private void renderControls() {
        font.getData().setScale(0.8f);
        String controls = "WASD: Move | V: Interact | X: Switch Chef | SHIFT+Dir: Dash | F: Throw";
        GlyphLayout layout = new GlyphLayout(font, controls);
        font.draw(batch, controls, GameConfig.SCREEN_WIDTH - layout.width - 20, 40);
    }
}