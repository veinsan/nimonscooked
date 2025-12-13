package com.nimonscooked.model.station;

import com.badlogic.gdx.Gdx;
import com.nimonscooked.model.entity.Chef;
import com.nimonscooked.model.ingredient.Preparable;
import com.nimonscooked.model.item.Item;
import com.nimonscooked.model.utensil.CookingDevice;
import com.nimonscooked.model.utensil.FryingPan;
import com.nimonscooked.model.utensil.Plate;
import com.nimonscooked.model.dish.Dish;
import java.util.Collections;

public class CookingStation extends Station {

    public enum StoveType { LEFT, RIGHT }
    private final StoveType stoveType;

    public CookingStation(String id, StoveType type, float x, float y) {
        super(id, x, y, 64, 64); // ← FIX: Add x, y, 64, 64
        this.stoveType = type;
        this.item = new FryingPan(); 
    }

    @Override
    public void interact(Chef chef) {
        if (!(this.item instanceof FryingPan)) return;
        
        FryingPan pan = (FryingPan) this.item;
        Item heldItem = chef.getInventory();

        // 1. GATEKEEPER (Strict Locking)
        if (pan.isCooking() && !pan.isFoodReady()) {
            return;
        }

        // 2. INPUT BAHAN (Hanya Chopped Meat)
        if (heldItem instanceof Preparable && pan.getContents().isEmpty()) {
            Preparable ingredient = (Preparable) heldItem;

            if (pan.canAccept(ingredient) && ingredient.canBeCooked()) {
                pan.addIngredient(ingredient);
                chef.setInventory(null);
                Gdx.app.log("CookingStation", "Ingredient placed on pan");
            } else {
                Gdx.app.log("CookingStation", "REJECTED: Must be CHOPPED and COOKABLE (Meat only).");
            }
        }
        
        // 3. PLATING (Output - Pakai Piring)
        else if (heldItem instanceof Plate && !pan.getContents().isEmpty()) {
            if (pan.isFoodReady()) {
                Plate plate = (Plate) heldItem;
                Preparable result = pan.getContents().get(0); 
                
                if (result instanceof Item) {
                    Dish newDish = new Dish(
                        ((Item) result).getName(), 
                        Collections.singletonList((Item) result)
                    );
                    plate.setContainedDish(newDish);
                    pan.clear(); 
                    Gdx.app.log("CookingStation", "SUCCESS: Food plated!");
                }
            }
        }

        // 4. AMBIL HASIL (Output - Tangan Kosong)
        else if (heldItem == null && !pan.getContents().isEmpty()) {
            if (pan.isFoodReady()) {
                Preparable result = pan.getContents().get(0);
                
                if (result instanceof Item) {
                    chef.setInventory((Item) result);
                    pan.clear(); 
                    Gdx.app.log("CookingStation", "Picked up with hands!");
                }
            }
        }
    }

    @Override
    public void processHold(Chef chef, float delta) {
        if (!(this.item instanceof FryingPan)) return;
        
        FryingPan pan = (FryingPan) this.item;
        
        // ONE-TIME TRIGGER: Start cooking sekali hold
        if (!pan.isCooking() && !pan.getContents().isEmpty()) {
            pan.startCooking();
            Gdx.app.log("CookingStation", "Started cooking!");
        }
        
        // NOTE: Chef TIDAK di-lock (tetap bisa gerak)
        // chef.setBusy() TIDAK dipanggil!
    }

    public boolean isActive() {
        return item instanceof CookingDevice && ((CookingDevice) item).isCooking();
    }
    
    public float getProgress() {
        Item item = this.getItem();
        if (item instanceof CookingDevice) {
            return ((CookingDevice) item).getProgress();
        }
        return 0f;
    }
    
    public StoveType getStoveType() { 
        return stoveType; 
    }
}