package com.nimonscooked.manager;

import com.badlogic.gdx.Gdx;
import com.nimonscooked.NimonscookedGame;
import com.nimonscooked.exception.GameLoadException;
import com.nimonscooked.model.recipe.Recipe;
import com.nimonscooked.view.screens.ResultScreen;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class GameManager {
    private static GameManager instance;
    private static final ExecutorService threadPool = Executors.newFixedThreadPool(4);

    public OrderManager orderManager;

    private float levelTimer;
    private final float LEVEL_DURATION = 180f;
    private int score;
    private int failedOrdersCount;
    private final int MAX_FAILED_ORDERS = 5;
    private final int MIN_PASS_SCORE = 100;

    private boolean gamePaused;
    private boolean isGameOver;
    private float timeScale = 1.0f;

    public static GameManager getInstance() {
        if (instance == null) instance = new GameManager();
        return instance;
    }

    private GameManager() {
        reset();
    }

    public static ExecutorService getThreadPool() {
        return threadPool;
    }

    public void update(float delta) {
        if (gamePaused || isGameOver) return;

        float scaledDelta = delta * timeScale;

        levelTimer -= scaledDelta;
        if (levelTimer <= 0) {
            levelTimer = 0;
            triggerGameOver();
        }

        if (orderManager != null) {
            orderManager.update(scaledDelta);
        }
    }

    public void addScore(int value) {
        score += value;
    }

    public void incrementFailedOrders() {
        failedOrdersCount++;
        if (failedOrdersCount >= MAX_FAILED_ORDERS) {
            triggerGameOver();
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
        timeScale = 1.0f;

        try {
            List<Recipe> loadedRecipes = RecipeLoader.loadRecipes("data/recipes.json");
            this.orderManager = new OrderManager(loadedRecipes);
        } catch (GameLoadException e) {
            Gdx.app.error("GameManager", "CRITICAL ERROR: Failed to load recipes!", e);
            this.orderManager = new OrderManager(new ArrayList<>());
        }
    }

    public void setTimeScale(float scale) {
        this.timeScale = Math.max(0.1f, Math.min(2.0f, scale));
    }

    public float getTimeScale() {
        return timeScale;
    }

    public float getLevelTimer() { return levelTimer; }
    public int getScore() { return score; }
    public int getFailedOrders() { return failedOrdersCount; }
    public boolean isPaused() { return gamePaused; }
    public void pauseGame() { gamePaused = true; }
    public void resumeGame() { gamePaused = false; }

    public void dispose() {
        shutdown();
    }

    public static void shutdown() {
        threadPool.shutdown();
        try {
            if (!threadPool.awaitTermination(5, TimeUnit.SECONDS)) {
                threadPool.shutdownNow();
            }
        } catch (InterruptedException e) {
            threadPool.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}