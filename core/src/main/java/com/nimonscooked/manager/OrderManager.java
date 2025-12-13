package com.nimonscooked.manager;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import com.nimonscooked.config.GameConfig;
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
    private float nextOrderInterval;

    public OrderManager(List<Recipe> menu) {
        this.availableRecipes = menu;
        this.activeOrders = new ArrayList<>();
        scheduleNextOrder();
    }

    public void update(float delta) {
        timeSinceLastOrder += delta;

        if (timeSinceLastOrder >= nextOrderInterval && activeOrders.size() < GameConfig.MAX_ACTIVE_ORDERS) {
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
        nextOrderInterval = MathUtils.random(GameConfig.MIN_ORDER_INTERVAL, GameConfig.MAX_ORDER_INTERVAL);
    }

    private void handleExpiredOrder(Order order) {
        GameManager.getInstance().incrementFailedOrders();
        GameManager.getInstance().addScore(order.getPenalty());
        AudioManager.getInstance().playSound("sfx/alarm.wav");
        Gdx.app.log("OrderManager", "Order EXPIRED: " + order.getRecipeName());
    }

    public int submitOrder(Dish servedDish) {
        if (activeOrders.isEmpty()) {
            GameManager.getInstance().addScore(GameConfig.PENALTY_NO_ORDER);
            AudioManager.getInstance().playSound("sfx/delivery_fail.wav");
            return GameConfig.PENALTY_NO_ORDER;
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

        GameManager.getInstance().addScore(GameConfig.PENALTY_WRONG_ORDER);
        AudioManager.getInstance().playSound("sfx/delivery_fail.wav");
        return GameConfig.PENALTY_WRONG_ORDER;
    }

    private void spawnOrder() {
        if (availableRecipes.isEmpty()) return;

        Recipe r = availableRecipes.get(new Random().nextInt(availableRecipes.size()));

        Order newOrder = new Order(
            activeOrders.size() + 1,
            r.getName(),
            GameConfig.REWARD_CORRECT_ORDER,
            GameConfig.PENALTY_EXPIRED_ORDER,
            GameConfig.DEFAULT_ORDER_TIME_LIMIT
        );

        activeOrders.add(newOrder);
        AudioManager.getInstance().playSound("sfx/catch.mp3");
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