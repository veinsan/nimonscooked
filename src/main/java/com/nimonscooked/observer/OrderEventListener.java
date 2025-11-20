package com.nimonscooked.observer;

public class OrderEventListener implements GameEventListener {
    
    private int totalOrdersCompleted;
    private int totalOrdersFailed;

    public OrderEventListener() {
        this.totalOrdersCompleted = 0;
        this.totalOrdersFailed = 0;
    }

    @Override
    public void onOrderCompleted(int orderId, int reward) {
        totalOrdersCompleted++;
        System.out.println("📦 [OrderListener] Order #" + orderId + " completed! Total completed: " + totalOrdersCompleted);
    }

    @Override
    public void onOrderFailed(int orderId, int penalty) {
        totalOrdersFailed++;
        System.out.println("📦 [OrderListener] Order #" + orderId + " failed! Total failed: " + totalOrdersFailed);
    }

    @Override
    public void onScoreChanged(int newScore) {
    }

    @Override
    public void onGameOver(String reason) {
        System.out.println("📦 [OrderListener] Game Over! Completed: " + totalOrdersCompleted + ", Failed: " + totalOrdersFailed);
    }

    @Override
    public void onStageCleared(int finalScore, int ordersCompleted) {
        System.out.println("📦 [OrderListener] Stage Cleared! Orders completed: " + ordersCompleted);
    }

    @Override
    public void onNewOrderAdded(int orderId, String recipeName) {
        System.out.println("📦 [OrderListener] New order added: #" + orderId + " - " + recipeName);
    }

    public int getTotalOrdersCompleted() {
        return totalOrdersCompleted;
    }

    public int getTotalOrdersFailed() {
        return totalOrdersFailed;
    }

    public void reset() {
        this.totalOrdersCompleted = 0;
        this.totalOrdersFailed = 0;
    }
}