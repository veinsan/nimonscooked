package com.nimonscooked.manager;

import com.badlogic.gdx.Gdx;
import com.nimonscooked.NimonscookedGame;
import com.nimonscooked.exception.GameLoadException;
import com.nimonscooked.model.recipe.Recipe;
import com.nimonscooked.view.screens.ResultScreen;

import java.util.ArrayList;
import java.util.List;

public class GameManager {
    private static GameManager instance;

    public OrderManager orderManager;

    private float levelTimer;
    private final float LEVEL_DURATION = 180f;
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

        levelTimer -= delta;
        if (levelTimer <= 0) {
            levelTimer = 0;
            triggerGameOver();
        }

        if (orderManager != null) {
            orderManager.update(delta);
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

        try {
            List<Recipe> loadedRecipes = RecipeLoader.loadRecipes("data/recipes.json");
            this.orderManager = new OrderManager(loadedRecipes);
        } catch (GameLoadException e) {
            Gdx.app.error("GameManager", "CRITICAL ERROR: Failed to load recipes!", e);
            this.orderManager = new OrderManager(new ArrayList<>());
        }
    }

    public float getLevelTimer() { return levelTimer; }
    public int getScore() { return score; }
    public int getFailedOrders() { return failedOrdersCount; }
    public boolean isPaused() { return gamePaused; }
    public void pauseGame() { gamePaused = true; }
    public void resumeGame() { gamePaused = false; }
}