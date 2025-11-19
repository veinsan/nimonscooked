package com.nimonscooked.model.order;

import java.util.LinkedList;
import java.util.Queue;

public class OrderManager {
    private Queue<Order> orders = new LinkedList<>();

    public void addOrder(Order order) {
        orders.add(order);
    }

    public Order getNextOrder() {
        return orders.peek();
    }

    public Order completeOrder() {
        return orders.poll();
    }

    public int size() {
        return orders.size();
    }
}
