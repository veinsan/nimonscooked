package com.nimonscooked.model.order;

public class Order {
    private int position;
    private String recipeName;
    private int reward;
    private int penalty;

    public Order(int position, String recipeName, int reward, int penalty) {
        this.position = position;
        this.recipeName = recipeName;
        this.reward = reward;
        this.penalty = penalty;
    }

    public int getPosition() { return position; }
    public String getRecipeName() { return recipeName; }
    public int getReward() { return reward; }
    public int getPenalty() { return penalty; }
    
    public void setPosition(int position) {
        this.position = position;
    }
}