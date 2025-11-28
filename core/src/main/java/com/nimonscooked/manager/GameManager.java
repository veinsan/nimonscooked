package com.nimonscooked.manager;

import com.badlogic.gdx.Gdx;
import com.nimonscooked.NimonscookedGame;
import com.nimonscooked.model.recipe.Recipe;
import com.nimonscooked.view.screens.ResultScreen;
import com.nimonscooked.view.screens.MainMenuScreen;
import java.util.List;

public class GameManager {
    private static GameManager instance;

    public OrderManager orderManager;

    // GAME LOOP VARIABLES (M2)
    private float levelTimer;
    private final float LEVEL_DURATION = 180f; // 3 Menit
    private int score;
    private int failedOrdersCount;
    private final int MAX_FAILED_ORDERS = 5;
    private final int MIN_PASS_SCORE = 100;

    private boolean gamePaused;
    private boolean isGameOver;

    public static GameManager getInstance() {
        if (instance == null) instance = new GameManager();
        return instance;
    }

    private GameManager() {
        reset();
    }

    public void update(float delta) {
        if (gamePaused || isGameOver) return;

        // 1. Level Timer Countdown
        levelTimer -= delta;
        if (levelTimer <= 0) {
            levelTimer = 0;
            triggerGameOver(); // Time's Up
        }

        // 2. Update Order Logic (Spawn/Expire)
        if (orderManager != null) {
            orderManager.update(delta);
        }

        // 3. Update Station Timers (Jika ada logic non-thread di masa depan)
        // Saat ini station pakai Thread terpisah, jadi aman.
    }

    public void addScore(int value) {
        score += value;
    }

    public void incrementFailedOrders() {
        failedOrdersCount++;
        if (failedOrdersCount >= MAX_FAILED_ORDERS) {
            triggerGameOver(); // Too Many Failed Orders
        }
    }

    private void triggerGameOver() {
        if (isGameOver) return;
        isGameOver = true;

        boolean isWin = (score >= MIN_PASS_SCORE) && (failedOrdersCount < MAX_FAILED_ORDERS);

        Gdx.app.postRunnable(() -> {
            NimonscookedGame.instance.setScreen(new ResultScreen(score, isWin));
        });
    }

    public void reset() {
        levelTimer = LEVEL_DURATION;
        score = 0;
        failedOrdersCount = 0;
        gamePaused = false;
        isGameOver = false;

        List<Recipe> loadedRecipes = RecipeLoader.loadRecipes("data/recipes.json");
        orderManager = new OrderManager(loadedRecipes);
    }

    public float getLevelTimer() { return levelTimer; }
    public int getScore() { return score; }
    public int getFailedOrders() { return failedOrdersCount; }
    public boolean isPaused() { return gamePaused; }
}
