package com.nimonscooked.view.renderer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.nimonscooked.config.GameConfig;
import com.nimonscooked.manager.GameManager;
import com.nimonscooked.manager.ResourceManager;
import com.nimonscooked.model.order.Order;
import java.util.List;

public class HudRenderer {
    private SpriteBatch batch;
    private BitmapFont font;
    private Viewport hudViewport;
    private OrthographicCamera hudCamera;

    public HudRenderer(SpriteBatch batch) {
        this.batch = batch;
        this.font = ResourceManager.getInstance().getCustomFont();

        // Kamera UI terpisah agar tidak ikut zoom in/out kamera dunia
        hudCamera = new OrthographicCamera();
        hudViewport = new FitViewport(GameConfig.SCREEN_WIDTH, GameConfig.SCREEN_HEIGHT, hudCamera);
        hudViewport.apply();
        hudCamera.position.set(GameConfig.SCREEN_WIDTH / 2f, GameConfig.SCREEN_HEIGHT / 2f, 0);
    }

    public void resize(int width, int height) {
        hudViewport.update(width, height, true);
    }

    public void render() {
        hudCamera.update();
        batch.setProjectionMatrix(hudCamera.combined);
        batch.begin();

        // 1. GLOBAL HUD (Top)
        font.getData().setScale(1.2f);
        float timer = GameManager.getInstance().getLevelTimer();
        int minutes = (int) timer / 60;
        int seconds = (int) timer % 60;

        String timeStr = String.format("%02d:%02d", minutes, seconds);
        String scoreStr = "Score: " + GameManager.getInstance().getScore();
        String failsStr = "Fails: " + GameManager.getInstance().getFailedOrders() + "/5";

        font.setColor(1, 1, 1, 1);
        font.draw(batch, timeStr, GameConfig.SCREEN_WIDTH / 2f - 20, GameConfig.SCREEN_HEIGHT - 20);
        font.draw(batch, scoreStr, 20, GameConfig.SCREEN_HEIGHT - 20);

        // Warna merah jika sudah gagal beberapa kali
        if (GameManager.getInstance().getFailedOrders() > 2) font.setColor(1, 0, 0, 1);
        font.draw(batch, failsStr, GameConfig.SCREEN_WIDTH - 150, GameConfig.SCREEN_HEIGHT - 20);
        font.setColor(1, 1, 1, 1); // Reset

        // 2. ORDERS LIST (Left Side)
        List<Order> orders = GameManager.getInstance().orderManager.getActiveOrders();
        float y = GameConfig.SCREEN_HEIGHT - 80;

        font.getData().setScale(0.9f);
        for (Order order : orders) {
            // Tampilkan timer order
            String timerOrder = String.format("(%ds)", (int)order.getRemainingTime());
            String text = order.getRecipeName() + " " + timerOrder;

            // Warna order berubah jadi merah jika waktu < 15 detik
            if (order.getRemainingTime() < 15) font.setColor(1, 0.3f, 0.3f, 1);
            else font.setColor(1, 1, 1, 1);

            font.draw(batch, text, 20, y);
            y -= 35;
        }
        font.setColor(1, 1, 1, 1); // Reset

        // 3. Render Controls Hint (Pojok Kanan Bawah)
        String controls = "WASD: Move | V: Interact | X: Switch Chef";
        GlyphLayout layout = new GlyphLayout(font, controls);
        font.draw(batch, controls, GameConfig.SCREEN_WIDTH - layout.width - 20, 40);

        batch.end();
    }
}
