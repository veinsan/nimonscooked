package com.nimonscooked.model.thread;

import com.nimonscooked.model.ingredient.Preparable;
import com.nimonscooked.model.item.Ingredient;
import com.nimonscooked.model.utensil.CookingDevice;
import java.util.List;

public class CookingThread extends Thread {
    private final CookingDevice device;
    private final List<Preparable> ingredients;
    private volatile boolean running = true;
    private static final long TIME_TO_COOK = 12000;
    private static final long TIME_TO_BURN = 12000;
    private float progress = 0f;

    public CookingThread(CookingDevice device, List<Preparable> ingredients) {
        this.device = device;
        this.ingredients = ingredients;
    }

    @Override
    public void run() {
        long startTime = System.currentTimeMillis();
        try {
            // Phase 1: Cook
            while (running && (System.currentTimeMillis() - startTime < TIME_TO_COOK)) {
                progress = (float)(System.currentTimeMillis() - startTime) / TIME_TO_COOK;
                Thread.sleep(100);
            }
            if(!running) return;

            for(Preparable p : ingredients) p.cook(); // Become Cooked

            // Phase 2: Burn
            startTime = System.currentTimeMillis();
            progress = 0f;
            while (running && (System.currentTimeMillis() - startTime < TIME_TO_BURN)) {
                progress = (float)(System.currentTimeMillis() - startTime) / TIME_TO_BURN;
                Thread.sleep(100);
            }
            if(!running) return;

            // Become Burnt (Implementasi state Burnt di Ingredient)
            for(Preparable p : ingredients) {
                if(p instanceof Ingredient) ((Ingredient)p).setState(Ingredient.State.BURNT);
            }

        } catch (InterruptedException e) {}
    }

    public void stopCooking() { running = false; }
    public float getProgress() { return progress; }
    public boolean isRunning() { return running && isAlive(); }
}
