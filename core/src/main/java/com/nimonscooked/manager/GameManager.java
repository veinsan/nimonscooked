package com.nimonscooked.manager;

import com.nimonscooked.model.recipe.Recipe;
import java.util.List;

public class GameManager {
    private static GameManager instance;

    public OrderManager orderManager;
    private float gameTime;
    private boolean gamePaused;

    public static GameManager getInstance() {
        if (instance == null) {
            instance = new GameManager();
        }
        return instance;
    }

    private GameManager() {
        this.gameTime = 0;
        this.gamePaused = false;

        // Load recipes dari JSON
        List<Recipe> loadedRecipes = RecipeLoader.loadRecipes("data/recipes.json");
        this.orderManager = new OrderManager(loadedRecipes);
    }

    public void update(float delta) {
        if (gamePaused) return;

        gameTime += delta;

        // OrderManager tetap di-update untuk spawn order (timer global, bukan station timer)
        if (orderManager != null) {
            orderManager.update(delta);
        }

        // HAPUS: Loop station update karena M1 tidak ada cooking timer
    }

    public void reset() {
        gameTime = 0;
        gamePaused = false;
        List<Recipe> loadedRecipes = RecipeLoader.loadRecipes("data/recipes.json");
        orderManager = new OrderManager(loadedRecipes);
    }

    // Getter & Setter
    public float getGameTime() { return gameTime; }
    public boolean isPaused() { return gamePaused; }
    public void pauseGame() { gamePaused = true; }
    public void resumeGame() { gamePaused = false; }
}
