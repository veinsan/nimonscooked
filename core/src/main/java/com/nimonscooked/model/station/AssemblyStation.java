package com.nimonscooked.model.station;

import com.badlogic.gdx.Gdx;
import com.nimonscooked.model.entity.Chef;
import com.nimonscooked.model.item.Item;
import com.nimonscooked.model.dish.Dish;
import com.nimonscooked.model.recipe.Recipe;
import com.nimonscooked.model.utensil.Plate;
import com.nimonscooked.model.item.Ingredient;

import java.util.ArrayList;
import java.util.List;

public class AssemblyStation extends Station {

    private final List<Recipe> availableRecipes;

    public AssemblyStation(String id, List<Recipe> recipes) {
        super(id);
        // Cek null safety agar tidak crash jika factory mengirim null
        this.availableRecipes = recipes != null ? new ArrayList<>(recipes) : new ArrayList<>();
    }

    /**
     * Implementasi method abstract dari Station.
     * Menangani logika interaksi Chef dengan Assembly Station.
     */
    @Override
    public void interact(Chef chef) {
        Item heldItem = chef.getInventory();
        Item stationItem = this.getItem();

        // 1. Taruh Item (Jika Chef bawa barang & Station kosong)
        if (heldItem != null && stationItem == null) {
            this.setItem(heldItem);
            chef.setInventory(null);
            Gdx.app.log("AssemblyStation", "Placed " + heldItem.getDisplayName());
        }
        // 2. Ambil Item (Jika Chef kosong & Station ada barang)
        else if (heldItem == null && stationItem != null) {
            chef.setInventory(stationItem);
            this.setItem(null);
            Gdx.app.log("AssemblyStation", "Took " + stationItem.getDisplayName());
        }
        // 3. Assemble/Combine (Jika Chef bawa Ingredient & Station ada Dish/Plate/Ingredient)
        else if (heldItem instanceof Ingredient && stationItem != null) {
            Item result = combine(stationItem, heldItem);
            if (result != null) {
                this.setItem(result);
                chef.setInventory(null); // Item di tangan chef hilang (masuk ke dish)
                Gdx.app.log("AssemblyStation", "Combined item into: " + result.getDisplayName());
            }
        }
    }

    /**
     * Helper untuk menggabungkan item di meja dengan item yang dipegang.
     */
    private Item combine(Item base, Item added) {
        List<Item> components = new ArrayList<>();

        // Jika di meja sudah ada Dish, ambil komponennya
        if (base instanceof Dish) {
            components.addAll(((Dish) base).getComponents());
        } 
        // Jika di meja ada Ingredient (single), jadikan awal komponen
        else if (base instanceof Ingredient) {
            components.add(base);
        }
        // TODO: Logic untuk Plate (jika base instanceof Plate) bisa ditambahkan di sini

        // Tambahkan item baru
        components.add(added);

        // Cek apakah kombinasi ini membentuk Resep yang valid?
        Dish potentialDish = new Dish("Unfinished Dish", components);
        return tryAssemble(potentialDish);
    }

    public Item tryAssemble(Item input) {
        if (!(input instanceof Dish)) {
            return input;
        }

        Dish inputDish = (Dish) input;

        // Cek apakah dish sementara ini cocok dengan Resep yang ada
        for (Recipe recipe : availableRecipes) {
            if (recipe.matches(inputDish)) {
                // MATCH FOUND! Buat Dish final dengan nama resep asli (misal "Classic Burger")
                Gdx.app.log("AssemblyStation", "RECIPE COMPLETED: " + recipe.getName());
                return new Dish(recipe.getName(), inputDish.getComponents());
            }
        }

        return input; // Kembalikan sebagai "Unfinished Dish" jika belum lengkap
    }

    public List<Recipe> getAvailableRecipes() {
        return new ArrayList<>(availableRecipes);
    }
}