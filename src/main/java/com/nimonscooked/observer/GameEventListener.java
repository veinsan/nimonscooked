package com.nimonscooked.observer;

public interface GameEventListener {
    void onOrderCompleted(int orderId, int reward);
    void onOrderFailed(int orderId, int penalty);
    void onScoreChanged(int newScore);
    void onGameOver(String reason);
    void onStageCleared(int finalScore, int ordersCompleted);
    void onNewOrderAdded(int orderId, String recipeName);
}