package com.nimonscooked.view.renderer;

import java.util.List;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.nimonscooked.config.GameConfig;
import com.nimonscooked.factory.StationFactory;
import com.nimonscooked.manager.GameManager;
import com.nimonscooked.manager.MapManager;
import com.nimonscooked.manager.ResourceManager;
import com.nimonscooked.model.entity.Chef;
import com.nimonscooked.model.item.Item;
import com.nimonscooked.model.order.Order;
import com.nimonscooked.model.recipe.Recipe;

public class HudRenderer {
    private SpriteBatch batch;
    private BitmapFont font;
    private Viewport hudViewport;
    private OrthographicCamera hudCamera;

    private Texture orderBg;
    private Texture scoreBg;
    private Texture timerBg;

    private Texture pixelTexture;

    private static final Color INK_COLOR = new Color(0.25f, 0.15f, 0.1f, 1f);

    public HudRenderer(SpriteBatch batch) {
        this.batch = batch;
        this.font = ResourceManager.getInstance().getCustomFont();
        this.pixelTexture = createPixelTexture();

        ResourceManager rm = ResourceManager.getInstance();
        this.orderBg = rm.getTexture("ui/order.png");
        this.scoreBg = rm.getTexture("ui/score_current.png");
        this.timerBg = rm.getTexture("ui/time.png");

        hudCamera = new OrthographicCamera();
        hudViewport = new FitViewport(GameConfig.SCREEN_WIDTH, GameConfig.SCREEN_HEIGHT, hudCamera);
        hudViewport.apply();
        hudCamera.position.set(GameConfig.SCREEN_WIDTH / 2f, GameConfig.SCREEN_HEIGHT / 2f, 0);
    }

    private Texture createPixelTexture() {
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
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

        renderOrders();
        renderScore();
        renderTimer();
        renderDashCooldown();

        batch.end();
    }

    private void renderOrders() {
        if (orderBg == null) return;

        List<Order> orders = GameManager.getInstance().orderManager.getActiveOrders();

        float scale = 0.5f;
        float leftPadding = 10f;
        float topMargin = 10f;
        float gap = 5f;

        float scrollWidth = orderBg.getWidth() * scale;
        float scrollHeight = orderBg.getHeight() * scale;

        float y = GameConfig.SCREEN_HEIGHT - topMargin - scrollHeight;

        for (int i = 0; i < orders.size(); i++) {
            Order order = orders.get(i);
            float x = leftPadding + (i * (scrollWidth + gap));

            batch.setColor(1, 1, 1, 1);
            batch.draw(orderBg, x, y, scrollWidth, scrollHeight);

            renderOrderContent(order, x, y, scrollWidth, scrollHeight, scale);
        }
    }

    private void renderOrderContent(Order order, float bgX, float bgY, float bgWidth, float bgHeight, float scale) {
        float iconSize = 160 * scale;
        float iconX = bgX + (bgWidth - iconSize) / 2f;
        float iconY = bgY + (bgHeight * 0.43f);

        String dishTexturePath = "ingredients/burger_random.png";

        if (order.getRecipeName().contains("Classic")) dishTexturePath = "ingredients/classic_burger.png";
        else if (order.getRecipeName().contains("Cheese")) dishTexturePath = "ingredients/cheese_burger.png";
        else if (order.getRecipeName().contains("Deluxe")) dishTexturePath = "ingredients/deluxe_burger.png";
        else if (order.getRecipeName().contains("BLT")) dishTexturePath = "ingredients/blt_burger.png";
        else if (order.getRecipeName().contains("Salad")) dishTexturePath = "ingredients/salad.png";

        Texture dishTex = ResourceManager.getInstance().getTexture(dishTexturePath);
        if (dishTex != null) {
            batch.draw(dishTex, iconX, iconY, iconSize, iconSize);
        }

        font.getData().setScale(0.8f * scale);
        font.setColor(INK_COLOR);

        GlyphLayout layout = new GlyphLayout(font, order.getRecipeName());
        float nameX = bgX + (bgWidth - layout.width) / 2f;
        float nameY = iconY - (2 * scale);

        font.draw(batch, order.getRecipeName(), nameX, nameY);

        Recipe recipe = null;
        for (Recipe r : StationFactory.getCachedRecipes()) {
            if (r.getName().equals(order.getRecipeName())) {
                recipe = r;
                break;
            }
        }

        if (recipe != null) {
            List<Item> ingredients = recipe.getRequiredItems();

            float ingSize = 55 * scale;
            float spacing = 3 * scale;

            float totalIngWidth = (ingredients.size() * ingSize) + ((ingredients.size() - 1) * spacing);
            float startIngX = bgX + (bgWidth - totalIngWidth) / 2f;
            float startIngY = bgY + (bgHeight * 0.28f);

            for (int i = 0; i < ingredients.size(); i++) {
                Item item = ingredients.get(i);
                Texture ingTex = ResourceManager.getInstance().getTexture(item.getTextureName());

                float currentX = startIngX + (i * (ingSize + spacing));

                if (ingTex != null) {
                    batch.setColor(1, 1, 1, 1);
                    batch.draw(ingTex, currentX, startIngY, ingSize, ingSize);
                }
            }
        }

        float barWidth = bgWidth * 0.65f;
        float barHeight = 8 * scale;
        float barX = bgX + (bgWidth - barWidth) / 2f;
        float barY = bgY + (bgHeight * 0.25f);

        batch.setColor(0.6f, 0.5f, 0.4f, 0.3f);
        batch.draw(pixelTexture, barX, barY, barWidth, barHeight);

        batch.setColor(order.getProgressColor());
        batch.draw(pixelTexture, barX, barY, barWidth * order.getProgressPercentage(), barHeight);

        batch.setColor(Color.WHITE);
    }

    private void renderScore() {
        if (scoreBg == null) return;

        float bgX = 20;
        float bgY = 20;
        batch.setColor(1, 1, 1, 1);
        batch.draw(scoreBg, bgX, bgY);

        float centerX = bgX + scoreBg.getWidth() / 2f;
        float centerY = bgY + scoreBg.getHeight() / 2f;

        font.getData().setScale(1.5f);
        font.setColor(INK_COLOR);

        String scoreStr = "Score: " + GameManager.getInstance().getScore();
        GlyphLayout layout = new GlyphLayout(font, scoreStr);

        font.draw(
            batch,
            scoreStr,
            centerX - layout.width / 2f,
            centerY + layout.height / 2f
        );
    }

    private void renderTimer() {
        if (timerBg == null) return;

        float bgX = GameConfig.SCREEN_WIDTH - timerBg.getWidth() - 20;
        float bgY = 20;
        batch.setColor(1, 1, 1, 1);
        batch.draw(timerBg, bgX, bgY);

        float timer = GameManager.getInstance().getLevelTimer();
        int minutes = (int) timer / 60;
        int seconds = (int) timer % 60;
        String timeStr = String.format("%02d:%02d", minutes, seconds);

        float textX = bgX + 40;
        float textY = bgY + timerBg.getHeight() / 2f + 15;

        font.getData().setScale(2.2f);

        if (timer < 30) font.setColor(Color.RED);
        else font.setColor(INK_COLOR);

        font.draw(batch, timeStr, textX, textY);
    }

    private void renderDashCooldown() {
        Chef activeChef = MapManager.getInstance().activeChef;
        if (activeChef == null) return;

        float cooldown = activeChef.getDashCooldown();
        if (cooldown > 0) {
            font.getData().setScale(0.8f);
            font.setColor(1, 0.5f, 0, 1);
            String dashText = String.format("Dash: %.1fs", cooldown);
            font.draw(batch, dashText, 40, 160);
        }
    }

    public void dispose() {
        if (pixelTexture != null) {
            pixelTexture.dispose();
        }
    }
}