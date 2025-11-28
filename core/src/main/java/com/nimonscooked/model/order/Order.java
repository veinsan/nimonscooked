package com.nimonscooked.model.order;

public class Order {
    private int position;
    private final String recipeName;
    private final int reward;
    private final int penalty;
    private float remainingTime;

    public Order(int position, String recipeName, int reward, int penalty, int timeLimit) {
        this.position = position;
        this.recipeName = recipeName;
        this.reward = reward;
        this.penalty = penalty;
        this.remainingTime = timeLimit; //
    }

    public int getPosition() {
        return position;
    }

    public String getRecipeName() {
        return recipeName;
    }

    public int getReward() {
        return reward;
    }

    public int getPenalty() {
        return penalty;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    @Override
    public String toString() {
        return "Order #" + position + ": " + recipeName +
               " (Reward: +" + reward + ", Penalty: -" + penalty + ")";
    }

    public void decreaseTimer(float delta) { this.remainingTime -= delta; }
    public float getRemainingTime() { return remainingTime; }
}
