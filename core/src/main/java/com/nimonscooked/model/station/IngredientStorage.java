package com.nimonscooked.model.station;

import com.badlogic.gdx.Gdx;
import com.nimonscooked.model.entity.Chef;
import com.nimonscooked.model.item.Ingredient;

public class IngredientStorage extends Station {
    private String ingredientName;

    public IngredientStorage(String id, String ingredientName) {
        super(id);
        this.ingredientName = ingredientName;
    }

    // --- INI METHOD PENTING YANG SEBELUMNYA HILANG ---
    // WorldRenderer butuh ini buat nentuin gambar crate (Meat/Cheese/dll)
    public String getIngredientName() {
        return ingredientName;
    }

    @Override
    public void interact(Chef chef) {
        // Cek jika tangan kosong, kasih bahan
        if (chef.getInventory() == null) {
            Ingredient newIngredient = createIngredient(ingredientName);
            chef.setInventory(newIngredient);
            Gdx.app.log("Storage", "Chef took: " + newIngredient.getName());
        }
    }

    private Ingredient createIngredient(String name) {
        // Pastikan path texture ini sesuai dengan aset kamu
        // Misalnya: "ingredients/meat_raw.png"
        String basePath = "ingredients/" + name.toLowerCase() + "_raw.png";
        return new Ingredient(name, basePath);
    }
}