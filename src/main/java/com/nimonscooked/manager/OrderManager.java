package com.nimonscooked.manager;

import java.util.LinkedList;
import java.util.Queue;

import com.nimonscooked.model.order.Order;

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
