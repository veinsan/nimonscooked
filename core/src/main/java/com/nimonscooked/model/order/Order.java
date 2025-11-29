package com.nimonscooked.model.order;

import com.badlogic.gdx.graphics.Color;

public class Order {
    private int position;
    private final String recipeName;
    private final int reward;
    private final int penalty;
    private float remainingTime;
    private final float initialTime;

    public Order(int position, String recipeName, int reward, int penalty, int timeLimit) {
        this.position = position;
        this.recipeName = recipeName;
        this.reward = reward;
        this.penalty = penalty;
        this.remainingTime = timeLimit;
        this.initialTime = timeLimit;
    }

    public Color getProgressColor() {
        float progress = remainingTime / initialTime;
        
        if (progress < 0.2f) {
            return Color.RED;
        } else if (progress < 0.5f) {
            return Color.ORANGE;
        } else {
            return Color.GREEN;
        }
    }

    public float getProgressPercentage() {
        return Math.max(0f, Math.min(1f, remainingTime / initialTime));
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

    public void decreaseTimer(float delta) {
        this.remainingTime -= delta;
    }

    public float getRemainingTime() {
        return remainingTime;
    }

    @Override
    public String toString() {
        return "Order #" + position + ": " + recipeName +
               " (Reward: +" + reward + ", Penalty: -" + penalty + ")";
    }
}