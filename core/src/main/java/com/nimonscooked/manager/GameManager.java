package com.nimonscooked.manager;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import com.badlogic.gdx.Gdx;
import com.nimonscooked.NimonscookedGame;
import com.nimonscooked.config.GameConfig;
import com.nimonscooked.exception.GameLoadException;
import com.nimonscooked.model.recipe.Recipe;
import com.nimonscooked.view.screens.ResultScreen;

public class GameManager {
    private static GameManager instance;
    private static ExecutorService threadPool;

    public OrderManager orderManager;

    private float levelTimer;
    private int score;
    private int failedOrdersCount;

    private boolean gamePaused;
    private boolean isGameOver;
    private float timeScale = 1.0f;

    public static GameManager getInstance() {
        if (instance == null) {
            instance = new GameManager();
        }
        return instance;
    }

    private GameManager() {
        if (threadPool == null) {
            threadPool = Executors.newFixedThreadPool(4);
        }
        reset();
    }

    public static ExecutorService getThreadPool() {
        if (threadPool == null) {
            threadPool = Executors.newFixedThreadPool(4);
        }
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
        if (failedOrdersCount >= GameConfig.MAX_FAILED_ORDERS) {
            triggerGameOver();
        }
    }

    private void triggerGameOver() {
        if (isGameOver) return;
        isGameOver = true;

        boolean isWin = (score >= GameConfig.MIN_PASS_SCORE) && 
                       (failedOrdersCount < GameConfig.MAX_FAILED_ORDERS);

        Gdx.app.postRunnable(() -> {
            NimonscookedGame.instance.setScreen(new ResultScreen(score, isWin));
        });
    }

    public void reset() {
        levelTimer = GameConfig.LEVEL_DURATION;
        score = 0;
        failedOrdersCount = 0;
        gamePaused = false;
        isGameOver = false;
        timeScale = 1.0f;

        if (orderManager != null) {
            orderManager = null;
        }

        try {
            List<Recipe> loadedRecipes = RecipeLoader.loadRecipes(GameConfig.RECIPES_PATH);
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

    public float getLevelTimer() { 
        return levelTimer; 
    }
    
    public int getScore() { 
        return score; 
    }
    
    public int getFailedOrders() { 
        return failedOrdersCount; 
    }
    
    public boolean isPaused() { 
        return gamePaused; 
    }
    
    public void pauseGame() { 
        gamePaused = true; 
    }
    
    public void resumeGame() { 
        gamePaused = false; 
    }

    public void dispose() {
        shutdown();
    }

    public static void shutdown() {
        if (threadPool != null) {
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
}