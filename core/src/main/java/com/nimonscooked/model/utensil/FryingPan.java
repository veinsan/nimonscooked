package com.nimonscooked.model.utensil;

import com.badlogic.gdx.Gdx;
import com.nimonscooked.manager.AudioManager;
import com.nimonscooked.model.ingredient.Preparable;
import com.nimonscooked.model.item.Ingredient;
import com.nimonscooked.model.thread.CookingThread;
import java.util.ArrayList;
import java.util.List;

public class FryingPan extends KitchenUtensil implements CookingDevice {

    private static final int MAX_CAPACITY = 1;
    private final List<Preparable> contents;
    
    // Audio Tracking
    private long currentSoundId = -1;
    private String currentSoundFile = null;

    public FryingPan() {
        super("Frying Pan", "EMPTY_PAN");
        this.contents = new ArrayList<>();
    }

    @Override
    public boolean isPortable() { return false; }
    @Override
    public int capacity() { return MAX_CAPACITY; }

    @Override
    public boolean canAccept(Preparable ingredient) {
        if (contents.size() >= MAX_CAPACITY) return false;
        if (!(ingredient instanceof Ingredient)) return false;
        
        Ingredient ing = (Ingredient) ingredient;
        return ing.getState() == Ingredient.State.CHOPPED;
    }

    @Override
    public void addIngredient(Preparable ingredient) {
        if (contents.size() < MAX_CAPACITY) {
            stopCooking();
            contents.add(ingredient);
            updateTexture();
        }
    }

    private void updateTexture() {
        if (contents.isEmpty()) {
            this.textureName = "EMPTY_PAN"; 
            return;
        } 
        
        Preparable p = contents.get(0);
        // FIX: p.getName() sekarang VALID karena Preparable sudah diupdate.
        if (p.getName().equalsIgnoreCase("Meat")) { 
            Ingredient.State state = ((Ingredient) p).getState();
            if (state == Ingredient.State.BURNT) {
                this.textureName = "ingredients/meat_burnt.png";
            } else if (state == Ingredient.State.COOKED) {
                this.textureName = "ingredients/meat_cooked.png";
            } else {
                this.textureName = "ingredients/meat_raw.png";
            }
        } else {
            this.textureName = "EMPTY_PAN"; 
            Gdx.app.error("FryingPan", "Non-meat item detected in pan, visual suppressed.");
        }
    }

    @Override
    public String getTextureName() {
        updateTexture();
        return super.getTextureName();
    }

    public boolean hasCookedFood() {
        if (contents.isEmpty()) return false;
        // FIX: contents.get(0).getName() sekarang VALID
        Preparable p = contents.get(0); 
        if (!p.getName().equalsIgnoreCase("Meat")) return false; 
        return ((Ingredient) p).getState() == Ingredient.State.COOKED;
    }

    public boolean isFoodReady() {
        if (contents.isEmpty()) return false;
        // FIX: contents.get(0).getName() sekarang VALID
        if (!contents.get(0).getName().equalsIgnoreCase("Meat")) return false; 
        
        Ingredient.State s = ((Ingredient) contents.get(0)).getState();
        return s == Ingredient.State.COOKED || s == Ingredient.State.BURNT;
    }

    // --- METHOD AUDIO BARU (Dibutuhkan oleh CookingThread) ---
    public void playFrySound() {
        stopSound();
        currentSoundFile = "sfx/fry.mp3";
        Gdx.app.postRunnable(() -> {
            currentSoundId = AudioManager.getInstance().playLoopingSound(currentSoundFile);
        });
    }

    public void playDoneSound() {
        stopSound(); 
        currentSoundFile = "sfx/done.mp3";
        Gdx.app.postRunnable(() -> {
            currentSoundId = AudioManager.getInstance().playLoopingSound(currentSoundFile);
        });
    }

    public void stopSound() {
        if (currentSoundId != -1 && currentSoundFile != null) {
            String fileToStop = currentSoundFile;
            long idToStop = currentSoundId;
            Gdx.app.postRunnable(() -> {
                AudioManager.getInstance().stopLoopingSound(fileToStop, idToStop);
            });
            currentSoundId = -1;
            currentSoundFile = null;
        }
    }
    // -----------------------------------------------------------

    @Override
    public void startCooking() {
        if (contents.isEmpty()) return;
        if (isCooking()) return; 

        // Double check lagi: Hanya start kalau isinya Meat
        if (!contents.get(0).getName().equalsIgnoreCase("Meat")) {
            Gdx.app.error("FryingPan", "CRITICAL: Attempted to cook non-meat item.");
            return;
        }

        cookingThread = new CookingThread(this, contents);
        cookingThread.start();
        playFrySound();
    }
    
    @Override
    public void stopCooking() {
        if (cookingThread != null) {
            cookingThread.stopCooking();
            cookingThread = null; 
        }
        stopSound();
    }

    @Override
    public boolean isCooking() {
        return cookingThread != null && cookingThread.isRunning();
    }

    @Override
    public float getProgress() {
        return cookingThread != null ? cookingThread.getProgress() : 0f;
    }

    public List<Preparable> getContents() {
        return new ArrayList<>(contents);
    }

    @Override
    public void clear() {
        stopCooking();
        contents.clear();
        updateTexture();
        super.clear();
    }
}