package com.nimonscooked.manager;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
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
    private static final float MIN_ORDER_INTERVAL = 10f;
    private static final float MAX_ORDER_INTERVAL = 20f;
    private static final int MAX_ACTIVE_ORDERS = 4;
    private static final int ORDER_TIME_LIMIT = 60;
    
    private float nextOrderInterval;

    public OrderManager(List<Recipe> menu) {
        this.availableRecipes = menu;
        this.activeOrders = new ArrayList<>();
        scheduleNextOrder();
    }

    public void update(float delta) {
        timeSinceLastOrder += delta;
        
        if (timeSinceLastOrder >= nextOrderInterval && activeOrders.size() < MAX_ACTIVE_ORDERS) {
            spawnOrder();
            timeSinceLastOrder = 0;
            scheduleNextOrder();
        }

        Iterator<Order> iterator = activeOrders.iterator();
        while (iterator.hasNext()) {
            Order order = iterator.next();
            order.decreaseTimer(delta);

            if (order.getRemainingTime() <= 0) {
                iterator.remove();
                handleExpiredOrder(order);
            }
        }
        
        updateOrderPositions();
    }

    private void scheduleNextOrder() {
        nextOrderInterval = MathUtils.random(MIN_ORDER_INTERVAL, MAX_ORDER_INTERVAL);
    }

    private void handleExpiredOrder(Order order) {
        GameManager.getInstance().incrementFailedOrders();
        GameManager.getInstance().addScore(order.getPenalty());
        AudioManager.getInstance().playSound("sfx/alarm.wav");
        Gdx.app.log("OrderManager", "Order EXPIRED: " + order.getRecipeName());
    }

    public int submitOrder(Dish servedDish) {
        if (activeOrders.isEmpty()) {
            GameManager.getInstance().addScore(-100);
            AudioManager.getInstance().playSound("sfx/delivery_fail.wav");
            return -100;
        }

        for (Order order : activeOrders) {
            if (order.getRecipeName().equalsIgnoreCase(servedDish.getName())) {
                int score = order.getReward();
                activeOrders.remove(order);
                GameManager.getInstance().addScore(score);
                AudioManager.getInstance().playSound("sfx/delivery_success.wav");
                spawnOrder();
                return score;
            }
        }

        GameManager.getInstance().addScore(-10);
        AudioManager.getInstance().playSound("sfx/delivery_fail.wav");
        return -10;
    }

    private void spawnOrder() {
        if (availableRecipes.isEmpty()) return;
        Recipe r = availableRecipes.get(new Random().nextInt(availableRecipes.size()));
        Order newOrder = new Order(activeOrders.size() + 1, r.getName(), 120, -50, ORDER_TIME_LIMIT);
        activeOrders.add(newOrder);
        AudioManager.getInstance().playSound("sfx/catch.wav");
    }

    private void updateOrderPositions() {
        for (int i = 0; i < activeOrders.size(); i++) {
            activeOrders.get(i).setPosition(i + 1);
        }
    }

    public List<Order> getActiveOrders() {
        return activeOrders;
    }
}