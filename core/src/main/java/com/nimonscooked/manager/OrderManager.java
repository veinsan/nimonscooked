package com.nimonscooked.manager; // <--- UBAH JADI INI (Sesuai folder)

import com.nimonscooked.model.order.Order;
import com.nimonscooked.model.order.OrderObserver; // Pastikan interface ini ada di model/order
import com.nimonscooked.model.recipe.Recipe;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class OrderManager {
    private List<Recipe> availableRecipes;
    private List<Order> activeOrders;
    private List<OrderObserver> observers;

    private float timeSinceLastOrder = 0;
    private static final float ORDER_INTERVAL = 10f;

    public OrderManager(List<Recipe> menu) {
        this.availableRecipes = menu;
        this.activeOrders = new ArrayList<>();
        this.observers = new ArrayList<>();
    }

    public void addObserver(OrderObserver observer) {
        observers.add(observer);
    }

    public void update(float delta) {
        timeSinceLastOrder += delta;
        if (timeSinceLastOrder >= ORDER_INTERVAL && activeOrders.size() < 5) {
            spawnOrder();
            timeSinceLastOrder = 0;
        }
    }

    private void spawnOrder() {
        if (availableRecipes.isEmpty()) return;
        Recipe r = availableRecipes.get(new Random().nextInt(availableRecipes.size()));
        Order newOrder = new Order(activeOrders.size() + 1, r.getName(), 100, 20);
        activeOrders.add(newOrder);
        notifyOrdersUpdated();
    }

    public int submitOrder(com.nimonscooked.model.dish.Dish servedDish) {
        if (activeOrders.isEmpty()) return 0;

        // Cari order yang cocok (FIFO - First In First Out idealnya, tapi random match juga oke untuk M1)
        for (Order order : activeOrders) {
            // Bandingkan Nama Resep (karena Dish dibuat dari Recipe yang valid, namanya pasti sama)
            if (order.getRecipeName().equalsIgnoreCase(servedDish.getName())) {

                // SUKSES!
                int score = order.getReward();
                activeOrders.remove(order); // Hapus order dari antrian
                notifyOrdersUpdated(); // Update UI

                com.badlogic.gdx.Gdx.app.log("OrderManager", "Order Completed: " + order.getRecipeName() + " (+" + score + ")");
                return score;
            }
        }

        // GAGAL (Pesanan salah)
        com.badlogic.gdx.Gdx.app.log("OrderManager", "Wrong Order Served: " + servedDish.getName());
        return -10; // Penalti default (bisa ambil dari spesifikasi Order kalau ada)
    }

    private void notifyOrdersUpdated() {
        for (OrderObserver obs : observers) {
            obs.onOrdersUpdated(new ArrayList<>(activeOrders));
        }
    }

    public List<Order> getActiveOrders() { return activeOrders; }
}
