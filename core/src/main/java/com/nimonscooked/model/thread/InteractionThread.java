package com.nimonscooked.model.thread;

import com.nimonscooked.model.entity.Chef;

public abstract class InteractionThread extends Thread {
    protected Chef chef;
    protected long durationMs;
    protected volatile boolean running = true;
    protected float progress = 0f;

    public InteractionThread(Chef chef, float durationSeconds) {
        this.chef = chef;
        this.durationMs = (long)(durationSeconds * 1000);
        setName("InteractionThread-" + chef.hashCode());
    }

    @Override
    public void run() {
        chef.setBusy(true);
        long startTime = System.currentTimeMillis();
        try {
            while (running && (System.currentTimeMillis() - startTime < durationMs)) {
                progress = Math.min(1f, (float)(System.currentTimeMillis() - startTime) / durationMs);
                Thread.sleep(50);
            }
            if (running) {
                progress = 1.0f;
                onComplete();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            chef.setBusy(false);
            chef.setCurrentInteraction(null);
            chef.isChopping = false;
        }
    }

    public abstract void onComplete();

    public float getProgress() {
        return progress;
    }

    public void stopInteraction() {
        running = false;
    }
}