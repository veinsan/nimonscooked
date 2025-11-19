package com.nimonscooked.model.order;

import com.nimonscooked.model.Item;
import com.nimonscooked.model.dish.Dish;
import com.nimonscooked.model.recipe.Recipe;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Random;

public class OrderManager {
    
    public static final int MAX_CONSECUTIVE_FAILURES = 5;
    private static final int DEFAULT_REWARD = 100;
    private static final int DEFAULT_PENALTY = 20;

    private final Queue<Order> activeOrders;
    private final List<Recipe> menu;
    private final Random random;

    private int score;
    private int nextOrderPosition;
    private int consecutiveFailures;
    private int ordersCompleted;
    private int ordersFailed;
    private boolean isGameOver;

    public OrderManager(List<Recipe> menu) {
        this.menu = menu;
        this.activeOrders = new LinkedList<>();
        this.random = new Random();
        this.score = 0;
        this.nextOrderPosition = 1;
        this.consecutiveFailures = 0;
        this.ordersCompleted = 0;
        this.ordersFailed = 0;
        this.isGameOver = false;
    }

    public void addOrder(String recipeName, int reward, int penalty) {
        Order newOrder = new Order(nextOrderPosition++, recipeName, reward, penalty);
        activeOrders.add(newOrder);
        System.out.println("📋 New Order #" + newOrder.getPosition() + ": " + recipeName);
    }

    public void addRandomOrder() {
        if (menu.isEmpty()) {
            System.err.println("❌ Cannot generate order: menu is empty!");
            return;
        }
        Recipe randomRecipe = menu.get(random.nextInt(menu.size()));
        addOrder(randomRecipe.getName(), DEFAULT_REWARD, DEFAULT_PENALTY);
    }

    public Order getNextOrder() {
        return activeOrders.peek();
    }

    private Order completeOrder() {
        return activeOrders.poll();
    }

    public int getActiveOrderCount() {
        return activeOrders.size();
    }

    public boolean tryServe(Item servedItem) {
        Order currentOrder = activeOrders.peek();
        if (currentOrder == null) {
            System.out.println("❌ No active orders to serve!");
            return false;
        }

        if (!(servedItem instanceof Dish)) {
            System.out.println("❌ Must serve a dish!");
            applyPenalty(currentOrder);
            return false;
        }

        Dish servedDish = (Dish) servedItem;
        Recipe targetRecipe = findRecipeByName(currentOrder.getRecipeName());
        if (targetRecipe == null) {
            System.err.println("❌ Recipe not found: " + currentOrder.getRecipeName());
            return false;
        }

        if (targetRecipe.matches(servedDish)) {
            score += currentOrder.getReward();
            consecutiveFailures = 0;
            ordersCompleted++;
            
            System.out.println("✅ Order #" + currentOrder.getPosition() + " completed!");
            System.out.println("💰 Earned: +" + currentOrder.getReward() + " points");
            System.out.println("📊 Total Score: " + score);
            
            completeOrder();
            addRandomOrder();
            return true;
        } else {
            applyPenalty(currentOrder);
            return false;
        }
    }

    private void applyPenalty(Order order) {
        score -= order.getPenalty();
        if (score < 0) score = 0;
        
        consecutiveFailures++;
        ordersFailed++;

        System.out.println("❌ Wrong dish! -" + order.getPenalty() + " points");
        System.out.println("⚠️ Consecutive failures: " + consecutiveFailures + "/" + MAX_CONSECUTIVE_FAILURES);
        System.out.println("📊 Current Score: " + score);

        if (consecutiveFailures >= MAX_CONSECUTIVE_FAILURES) {
            isGameOver = true;
            System.out.println("\n💀 GAME OVER: Too many consecutive failures!");
        }
    }

    private Recipe findRecipeByName(String recipeName) {
        for (Recipe recipe : menu) {
            if (recipe.getName().equalsIgnoreCase(recipeName)) {
                return recipe;
            }
        }
        return null;
    }

    public int getScore() {
        return score;
    }

    public int getConsecutiveFailures() {
        return consecutiveFailures;
    }

    public int getOrdersCompleted() {
        return ordersCompleted;
    }

    public int getOrdersFailed() {
        return ordersFailed;
    }

    public boolean isGameOver() {
        return isGameOver;
    }

    public List<Recipe> getMenu() {
        return new LinkedList<>(menu);
    }
}