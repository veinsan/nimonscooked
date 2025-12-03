package com.nimonscooked.model.order;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class OrderManager {

    private final List<Order> orders = new ArrayList<>();

    public Order createOrder(int position, String recipeName, int reward, int penalty, int timeLimitSeconds) {
        Order order = new Order(position, recipeName, reward, penalty, timeLimitSeconds);
        orders.add(order);
        return order;
    }

    public void tickAll(int deltaSeconds) {
        for (Order order : orders) {
            order.tick(deltaSeconds);
        }
    }

    public boolean serveByRecipeName(String recipeName) {
        for (Order order : orders) {
            if (order.isActive() && order.getRecipeName().equalsIgnoreCase(recipeName)) {
                order.markServed();
                return true;
            }
        }
        return false;
    }

    public List<Order> getAllOrders() {
        return Collections.unmodifiableList(orders);
    }

    public List<Order> getActiveOrders() {
        List<Order> active = new ArrayList<>();
        for (Order o : orders) {
            if (o.isActive()) active.add(o);
        }
        return active;
    }
}
