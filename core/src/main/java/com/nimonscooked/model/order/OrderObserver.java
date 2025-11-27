package com.nimonscooked.model.order;
import java.util.List;

public interface OrderObserver {
    void onOrdersUpdated(List<Order> activeOrders);
    void onOrderCompleted(Order order, int score);
    void onOrderExpired(Order order);
}