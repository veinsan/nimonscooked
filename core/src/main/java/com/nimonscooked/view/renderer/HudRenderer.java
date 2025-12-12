package com.nimonscooked.view.renderer;

import java.util.List;

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

public class HudRenderer {
    private SpriteBatch batch;
    private BitmapFont font;
    private Viewport hudViewport;
    private OrthographicCamera hudCamera;
    
    // UI Background Textures
    private Texture orderBg;
    private Texture scoreBg;
    private Texture timerBg;
    
    // Pixel texture for progress bars
    private Texture pixelTexture;
    
    // Custom Ink Color for text on paper
    private static final Color INK_COLOR = new Color(0.25f, 0.15f, 0.1f, 1f);

    public HudRenderer(SpriteBatch batch) {
        this.batch = batch;
        this.font = ResourceManager.getInstance().getCustomFont();
        this.pixelTexture = createPixelTexture();

        // Load the UI textures
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

        renderOrders(); // Top Left
        renderScore();  // Bottom Left
        renderTimer();  // Bottom Right
        renderDashCooldown(); // Floating

        batch.end();
    }

    private void renderOrders() {
        if (orderBg == null) return;
        
        List<Order> orders = GameManager.getInstance().orderManager.getActiveOrders();

        // --- CONFIGURATION ---
        float scale = 0.5f;       
        float leftPadding = 10f;  
        float topMargin = 10f;    
        float gap = 5f;           
        // ---------------------

        float scrollWidth = orderBg.getWidth() * scale;
        float scrollHeight = orderBg.getHeight() * scale;

        // Fixed Y position (Top of screen)
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
        GlyphLayout layout = new GlyphLayout();
        
        // --- 0. IMAGE PLACEHOLDER (Top Half) ---
        // This space (approx top 50%) is now empty for your image.
        // Logic to draw your item icon would go here:
        float iconSize = 135 * scale;
        float iconX = bgX + (bgWidth - iconSize) / 2f;
        float iconY = bgY + (bgHeight * 0.40f);
        
        if ( order.getRecipeName().equals("Classic Burger")){
            Texture burgerTexture = ResourceManager.getInstance().getTexture("ingredients/classic_burger.png");
            batch.draw(burgerTexture, iconX, iconY, iconSize, iconSize);
        
        }else if( order.getRecipeName().equals("Cheeseburger")){
            Texture burgerTexture = ResourceManager.getInstance().getTexture("ingredients/cheese_burger.png");
            batch.draw(burgerTexture, iconX, iconY, iconSize, iconSize);

        }else if( order.getRecipeName().equals("Deluxe Burger")){
            Texture burgerTexture = ResourceManager.getInstance().getTexture("ingredients/deluxe_burger.png");
            batch.draw(burgerTexture, iconX, iconY, iconSize, iconSize);
        }else if( order.getRecipeName().equals("BLT Burger")){
            Texture burgerTexture = ResourceManager.getInstance().getTexture("ingredients/blt_burger.png");
            batch.draw(burgerTexture, iconX, iconY, iconSize, iconSize);
        }
       
        // --- 1. RECIPE NAME (Lowered to ~45% height) ---
        font.getData().setScale(0.85f * scale);
        font.setColor(INK_COLOR);
        
        String nameText = order.getRecipeName();
        layout.setText(font, nameText);
        
        // Center X, but move Y down to leave room above
        float nameX = bgX + (bgWidth - layout.width) / 2f;
        float nameY = bgY + (bgHeight * 0.35f); // <--- MOVED DOWN
        
        font.draw(batch, nameText, nameX, nameY);

        // --- 2. TIMER (Below Name) ---
        

        // --- 3. PROGRESS BAR (At the very bottom) ---
        float barWidth = bgWidth * 0.6f; 
        float barHeight = 8 * scale;
        float barX = bgX + (bgWidth - barWidth) / 2f; 
        float barY = bgY + (bgHeight * 0.35f); // <--- MOVED DOWN to 15% from bottom

        batch.setColor(0.6f, 0.5f, 0.4f, 0.3f);
        batch.draw(pixelTexture, barX, barY, barWidth, barHeight);
        
        batch.setColor(order.getProgressColor());
        batch.draw(pixelTexture, barX, barY, barWidth * order.getProgressPercentage(), barHeight);
        
        batch.setColor(1, 1, 1, 1);
    }
    private void renderScore() {
        if (scoreBg == null) return;

        float bgX = 20;
        float bgY = 20;
        batch.setColor(1, 1, 1, 1);
        batch.draw(scoreBg, bgX, bgY);

        float centerX = bgX + scoreBg.getWidth() / 2f;
        float centerY = bgY + scoreBg.getHeight() / 2f;

        // --- BIG SCORE ---
        font.getData().setScale(1.5f); // Scale increased to 2.0 (was 1.1)
        font.setColor(INK_COLOR);

        String scoreStr = "Score: " + GameManager.getInstance().getScore();
        GlyphLayout layout = new GlyphLayout(font, scoreStr);
        
        // Centered vertically and horizontally
        font.draw(batch, scoreStr, 
            centerX - layout.width / 2f, 
            centerY + layout.height / 2f);

        // "Fails" display removed as requested
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

        // --- BIG TIMER ---
        float textX = bgX + 40; 
        float textY = bgY + timerBg.getHeight() / 2f + 15; // Adjusted Y for larger font

        font.getData().setScale(2.2f); // Scale increased to 2.2 (was 1.5)
        
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
}