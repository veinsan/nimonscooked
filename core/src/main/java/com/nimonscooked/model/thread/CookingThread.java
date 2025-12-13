package com.nimonscooked.model.thread;

import com.badlogic.gdx.Gdx;
import com.nimonscooked.config.GameConfig;
import com.nimonscooked.manager.AudioManager; // <--- FIX: Import yang hilang
import com.nimonscooked.model.ingredient.Preparable;
import com.nimonscooked.model.item.Ingredient;
import com.nimonscooked.model.utensil.CookingDevice;
import com.nimonscooked.model.utensil.FryingPan;
import java.util.List;

public class CookingThread extends Thread {

    private final List<Preparable> ingredients;
    private final CookingDevice device;
    private volatile boolean running = true;

    private static final long TIME_TO_COOK = (long) (GameConfig.COOK_TIME * 1000);
    private static final long TIME_TO_BURN = (long) (GameConfig.BURN_TIME * 1000);

    private float progress = 0f;

    public CookingThread(CookingDevice device, List<Preparable> ingredients) {
        this.device = device;
        this.ingredients = ingredients;
        setName("CookingThread-" + System.currentTimeMillis());
        setDaemon(true);
    }

    @Override
    public void run() {
        // FIX: contents.get(0).getName() sekarang valid
        if (ingredients.isEmpty() || !ingredients.get(0).getName().equalsIgnoreCase("Meat")) {
            Gdx.app.error("CookingThread", "Thread started with non-meat item, shutting down.");
            running = false;
            return;
        }
        
        try {
            setIngredientsState(Ingredient.State.COOKING);
            
            cookingPhase();
            if (!running) return;

            // FIX: playDoneSound() sekarang valid
            if (device instanceof FryingPan) {
                ((FryingPan) device).playDoneSound();
            }
            
            burningPhase();
            
        } catch (InterruptedException e) {
            running = false;
        } finally {
            // FIX: stopSound() sekarang valid
            if (device instanceof FryingPan && !running) {
                ((FryingPan) device).stopSound();
            }
        }
    }

    private void setIngredientsState(Ingredient.State state) {
        if (!running) return;
        synchronized (ingredients) {
            for (Preparable p : ingredients) {
                // FIX: p.getName() sekarang valid
                if (p.getName().equalsIgnoreCase("Meat") && p instanceof Ingredient) {
                     ((Ingredient) p).setState(state);
                }
            }
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
                // FIX: p.getName() sekarang valid
                if (p.getName().equalsIgnoreCase("Meat")) {
                    p.cook(); 
                }
            }
        }
        progress = 0f;
    }

    private void burningPhase() throws InterruptedException {
        long startTime = System.currentTimeMillis();
        while (running && (System.currentTimeMillis() - startTime < TIME_TO_BURN)) {
            long elapsed = System.currentTimeMillis() - startTime;
            progress = (float) elapsed / TIME_TO_BURN;
            Thread.sleep(100);
        }

        if (!running) return;

        setIngredientsState(Ingredient.State.BURNT);
        
        if (device instanceof FryingPan) {
            // FIX: stopSound() sekarang valid
            ((FryingPan) device).stopSound();
            // FIX: AudioManager.getInstance() sekarang valid
            Gdx.app.postRunnable(() -> AudioManager.getInstance().playSound("sfx/trash.wav")); 
        }
    }

    public void stopCooking() {
        running = false;
        interrupt();
    }

    public float getProgress() { return progress; }
    public boolean isRunning() { return running && isAlive(); }
}