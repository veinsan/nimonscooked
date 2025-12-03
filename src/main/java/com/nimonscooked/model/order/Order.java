package com.nimonscooked.model.order;

public class Order {
    private int position;
    private String recipeName;
    private int reward;
    private int penalty;

    private final int timeLimit;   
    private int timeRemaining;     
    private OrderStatus status;    

    public Order(int position, String recipeName, int reward, int penalty) {
        this(position, recipeName, reward, penalty, 60); 
    }

    public Order(int position, String recipeName, int reward, int penalty, int timeLimit) {
        this.position = position;
        this.recipeName = recipeName;
        this.reward = reward;
        this.penalty = penalty;
        this.timeLimit = timeLimit;
        this.timeRemaining = timeLimit;
        this.status = OrderStatus.PENDING;
    }

    public int getPosition() { return position; }
    public String getRecipeName() { return recipeName; }
    public int getReward() { return reward; }
    public int getPenalty() { return penalty; }

    public void setPosition(int position) {
        this.position = position;
    }

    public int getTimeLimit() { return timeLimit; }
    public int getTimeRemaining() { return timeRemaining; }
    public OrderStatus getStatus() { return status; }

    public boolean isActive() { return status == OrderStatus.PENDING; }
    public boolean isExpired() { return status == OrderStatus.EXPIRED; }
    public boolean isServed() { return status == OrderStatus.SERVED; }

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
                "[pos=%d] %-10s | %3ds/%3ds | %s | +%d / -%d",
                position, recipeName, timeRemaining, timeLimit, status, reward, penalty
        );
    }
}
