package com.nimonscooked.model.order;

public class Order {
    private String recipeName;
    private int reward;
    private int penalty;

    public Order(String recipeName, int reward, int penalty) {
        this.recipeName = recipeName;
        this.reward = reward;
        this.penalty = penalty;
    }

    public String getRecipeName() { return recipeName; }
    public int getReward() { return reward; }
    public int getPenalty() { return penalty; }
}
