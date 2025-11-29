package com.nimonscooked.model.thread;

import com.badlogic.gdx.Gdx;
import com.nimonscooked.manager.AudioManager;
import com.nimonscooked.model.ingredient.Preparable;
import com.nimonscooked.model.item.Ingredient;
import com.nimonscooked.model.utensil.CookingDevice;
import java.util.List;

public class CookingThread extends Thread {

    private final List<Preparable> ingredients;
    private volatile boolean running = true;

    private static final long TIME_TO_COOK = 10000;
    private static final long TIME_TO_BURN = 8000;
    private static final long BURN_WARNING_TIME = 6000;

    private float progress = 0f;
    private boolean hasWarned = false;

    public CookingThread(CookingDevice device, List<Preparable> ingredients) {
        this.ingredients = ingredients;
        setName("CookingThread-" + System.currentTimeMillis());
        setDaemon(true);
    }

    @Override
    public void run() {
        try {
            cookingPhase();
            if (!running) return;
            burningPhase();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            Gdx.app.log("CookingThread", "Thread interrupted");
        }
    }

    private void cookingPhase() throws InterruptedException {
        long startTime = System.currentTimeMillis();

        while (running && (System.currentTimeMillis() - startTime < TIME_TO_COOK)) {
            progress = (float) (System.currentTimeMillis() - startTime) / TIME_TO_COOK;
            Thread.sleep(100);
        }

        if (!running) return;

        synchronized (ingredients) {
            for (Preparable p : ingredients) {
                p.cook();
            }
        }

        Gdx.app.postRunnable(() -> {
            AudioManager.getInstance().playSound("sfx/fry.mp3");
            Gdx.app.log("CookingThread", "Food is COOKED!");
        });

        progress = 0f;
        hasWarned = false;
    }

    private void burningPhase() throws InterruptedException {
        long startTime = System.currentTimeMillis();

        while (running && (System.currentTimeMillis() - startTime < TIME_TO_BURN)) {
            long elapsed = System.currentTimeMillis() - startTime;
            progress = (float) elapsed / TIME_TO_BURN;

            if (!hasWarned && elapsed >= BURN_WARNING_TIME) {
                hasWarned = true;
                Gdx.app.postRunnable(() -> {
                    AudioManager.getInstance().playSound("sfx/alarm.wav");
                    Gdx.app.log("CookingThread", "WARNING: Food is about to BURN!");
                });
            }

            Thread.sleep(100);
        }

        if (!running) return;

        synchronized (ingredients) {
            for (Preparable p : ingredients) {
                if (p instanceof Ingredient) {
                    ((Ingredient) p).setState(Ingredient.State.BURNT);
                }
            }
        }

        Gdx.app.postRunnable(() -> {
            AudioManager.getInstance().playSound("sfx/trash.wav");
            Gdx.app.log("CookingThread", "Food is BURNT!");
        });
    }

    public void stopCooking() {
        running = false;
        interrupt();
    }

    public float getProgress() {
        return progress;
    }

    public boolean isRunning() {
        return running && isAlive();
    }
}