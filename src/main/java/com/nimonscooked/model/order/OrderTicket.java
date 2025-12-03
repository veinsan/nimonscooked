package com.nimonscooked.model.order;

public class OrderTicket {
    private final int id;
    private final String dishName;     
    private final int timeLimit;       
    private int timeRemaining;         
    private OrderStatus status;

    public OrderTicket(int id, String dishName, int timeLimitSeconds) {
        this.id = id;
        this.dishName = dishName;
        this.timeLimit = timeLimitSeconds;
        this.timeRemaining = timeLimitSeconds;
        this.status = OrderStatus.PENDING;
    }

    public int getId() {
        return id;
    }

    public String getDishName() {
        return dishName;
    }

    public int getTimeLimit() {
        return timeLimit;
    }

    public int getTimeRemaining() {
        return timeRemaining;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public boolean isActive() {
        return status == OrderStatus.PENDING;
    }

    public boolean isExpired() {
        return status == OrderStatus.EXPIRED;
    }

    public boolean isServed() {
        return status == OrderStatus.SERVED;
    }

   
    public void tick(int deltaSeconds) {
        if (!isActive()) return; 

        timeRemaining -= deltaSeconds;
        if (timeRemaining <= 0) {
            timeRemaining = 0;
            status = OrderStatus.EXPIRED;
        }
    }

    
    public void markServed() {
        if (status == OrderStatus.PENDING) {
            status = OrderStatus.SERVED;
        }
    }

    @Override
    public String toString() {
        return String.format(
                "#%d %-10s | %3ds/%3ds | %s",
                id, dishName, timeRemaining, timeLimit, status
        );
    }
}
