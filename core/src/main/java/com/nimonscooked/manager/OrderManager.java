package com.nimonscooked.manager;

import com.badlogic.gdx.Gdx;
import com.nimonscooked.model.dish.Dish;
import com.nimonscooked.model.order.Order;
import com.nimonscooked.model.recipe.Recipe;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class OrderManager {
    private List<Recipe> availableRecipes;
    private List<Order> activeOrders;

    private float timeSinceLastOrder = 0;
    private static final float ORDER_INTERVAL = 15f;
    private static final int ORDER_TIME_LIMIT = 60;

    public OrderManager(List<Recipe> menu) {
        this.availableRecipes = menu;
        this.activeOrders = new ArrayList<>();
    }

    public void update(float delta) {
        // Spawn Order
        timeSinceLastOrder += delta;
        if (timeSinceLastOrder >= ORDER_INTERVAL && activeOrders.size() < 5) {
            spawnOrder();
            timeSinceLastOrder = 0;
        }

        // Check Expiration
        Iterator<Order> iterator = activeOrders.iterator();
        while (iterator.hasNext()) {
            Order order = iterator.next();
            order.decreaseTimer(delta); // Pastikan class Order punya method ini

            if (order.getRemainingTime() <= 0) {
                iterator.remove();
                GameManager.getInstance().incrementFailedOrders();
                GameManager.getInstance().addScore(order.getPenalty());
                Gdx.app.log("OrderManager", "Order EXPIRED: " + order.getRecipeName());
            }
        }
    }

    public int submitOrder(Dish servedDish) {
        if (activeOrders.isEmpty()) return 0;

        for (Order order : activeOrders) {
            if (order.getRecipeName().equalsIgnoreCase(servedDish.getName())) {
                int score = order.getReward();
                activeOrders.remove(order);
                GameManager.getInstance().addScore(score);
                spawnOrder(); // Langsung ganti order baru
                return score;
            }
        }

        GameManager.getInstance().addScore(-10); // Wrong order penalty
        return -10;
    }

    private void spawnOrder() {
        if (availableRecipes.isEmpty()) return;
        Recipe r = availableRecipes.get(new Random().nextInt(availableRecipes.size()));
        // Position, Name, Reward, Penalty, TimeLimit
        Order newOrder = new Order(activeOrders.size() + 1, r.getName(), 120, -50, ORDER_TIME_LIMIT);
        activeOrders.add(newOrder);
    }

    public List<Order> getActiveOrders() { return activeOrders; }
}
