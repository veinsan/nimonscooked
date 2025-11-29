package com.nimonscooked.model.utensil;

import com.nimonscooked.manager.AudioManager;
import com.nimonscooked.model.ingredient.Preparable;
import com.nimonscooked.model.item.Ingredient;
import com.nimonscooked.model.thread.CookingThread;
import java.util.ArrayList;
import java.util.List;

public class FryingPan extends KitchenUtensil implements CookingDevice {

    private static final int MAX_CAPACITY = 3;
    private final List<Preparable> contents;

    public FryingPan() {
        super("Frying Pan", "items/pan.png");
        this.contents = new ArrayList<>();
    }

    @Override
    public boolean isPortable() {
        return true;
    }

    @Override
    public int capacity() {
        return MAX_CAPACITY;
    }

    @Override
    public boolean canAccept(Preparable ingredient) {
        if (contents.size() >= MAX_CAPACITY) return false;
        if (!(ingredient instanceof Ingredient)) return false;
        Ingredient ing = (Ingredient) ingredient;
        return ing.getState() == Ingredient.State.CHOPPED || ing.getState() == Ingredient.State.RAW;
    }

    @Override
    public void addIngredient(Preparable ingredient) {
        if (canAccept(ingredient)) {
            contents.add(ingredient);
            updateTexture();
        }
    }

    private void updateTexture() {
        if (contents.isEmpty()) {
            this.textureName = "items/pan.png";
        } else {
            boolean hasCooked = false;
            boolean hasBurnt = false;
            
            for (Preparable p : contents) {
                if (p instanceof Ingredient) {
                    Ingredient ing = (Ingredient) p;
                    if (ing.getState() == Ingredient.State.BURNT) {
                        hasBurnt = true;
                        break;
                    }
                    if (ing.getState() == Ingredient.State.COOKED) {
                        hasCooked = true;
                    }
                }
            }
            
            if (hasBurnt) {
                this.textureName = "ingredients/meat_burnt.png";
            } else if (hasCooked) {
                this.textureName = "ingredients/meat_cooked.png";
            } else {
                this.textureName = "ingredients/meat_raw.png";
            }
        }
    }

    @Override
    public void startCooking() {
        if (contents.isEmpty()) return;
        if (cookingThread == null || !cookingThread.isAlive()) {
            cookingThread = new CookingThread(this, contents);
            cookingThread.start();
            AudioManager.getInstance().playSound("sfx/fry.mp3");
        }
    }

    @Override
    public void stopCooking() {
        if (cookingThread != null) {
            cookingThread.stopCooking();
        }
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
        super.clear();
        contents.clear();
        updateTexture();
    }

    @Override
    public String getDisplayName() {
        return contents.isEmpty()
                ? "Frying Pan (Empty)"
                : "Frying Pan (" + contents.size() + " items)";
    }
}