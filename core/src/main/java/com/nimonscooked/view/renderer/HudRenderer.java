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

        // 1. Render Judul/Score
        font.getData().setScale(1.5f);
        font.draw(batch, "ORDERS", 20, GameConfig.SCREEN_HEIGHT - 20);

        // 2. Render Daftar Order Aktif
        List<Order> orders = GameManager.getInstance().orderManager.getActiveOrders();
        float y = GameConfig.SCREEN_HEIGHT - 60;

        font.getData().setScale(1.0f);
        if (orders.isEmpty()) {
            font.draw(batch, "No Active Orders", 20, y);
        } else {
            for (Order order : orders) {
                String text = order.getRecipeName() + " (" + order.getReward() + " pts)";
                font.draw(batch, text, 20, y);
                y -= 30; // Spasi ke bawah
            }
        }

        // 3. Render Controls Hint (Pojok Kanan Bawah)
        String controls = "WASD: Move | V: Interact | X: Switch Chef";
        GlyphLayout layout = new GlyphLayout(font, controls);
        font.draw(batch, controls, GameConfig.SCREEN_WIDTH - layout.width - 20, 40);

        batch.end();
    }
}
