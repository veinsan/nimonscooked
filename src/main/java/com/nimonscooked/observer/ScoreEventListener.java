package com.nimonscooked.observer;

public class ScoreEventListener implements GameEventListener {
    
    private int currentScore;
    private int highestScore;
    private int totalRewardsEarned;
    private int totalPenaltiesIncurred;

    public ScoreEventListener() {
        this.currentScore = 0;
        this.highestScore = 0;
        this.totalRewardsEarned = 0;
        this.totalPenaltiesIncurred = 0;
    }

    @Override
    public void onOrderCompleted(int orderId, int reward) {
        totalRewardsEarned += reward;
        System.out.println("💰 [ScoreListener] Earned +" + reward + " points! Total rewards: " + totalRewardsEarned);
    }

    @Override
    public void onOrderFailed(int orderId, int penalty) {
        totalPenaltiesIncurred += penalty;
        System.out.println("💰 [ScoreListener] Lost -" + penalty + " points! Total penalties: " + totalPenaltiesIncurred);
    }

    @Override
    public void onScoreChanged(int newScore) {
        int scoreDiff = newScore - currentScore;
        currentScore = newScore;
        
        if (currentScore > highestScore) {
            highestScore = currentScore;
            System.out.println("💰 [ScoreListener] New high score: " + highestScore + "!");
        }
        
        String direction = scoreDiff > 0 ? "increased" : scoreDiff < 0 ? "decreased" : "unchanged";
        System.out.println("💰 [ScoreListener] Score " + direction + " to: " + currentScore);
    }

    @Override
    public void onGameOver(String reason) {
        System.out.println("💰 [ScoreListener] Final Score: " + currentScore);
        System.out.println("💰 [ScoreListener] Highest Score: " + highestScore);
    }

    @Override
    public void onStageCleared(int finalScore, int ordersCompleted) {
        System.out.println("💰 [ScoreListener] Stage Cleared! Final Score: " + finalScore);
    }

    @Override
    public void onNewOrderAdded(int orderId, String recipeName) {
    }

    public int getCurrentScore() {
        return currentScore;
    }

    public int getHighestScore() {
        return highestScore;
    }

    public int getTotalRewardsEarned() {
        return totalRewardsEarned;
    }

    public int getTotalPenaltiesIncurred() {
        return totalPenaltiesIncurred;
    }

    public int getNetScore() {
        return totalRewardsEarned - totalPenaltiesIncurred;
    }

    public void reset() {
        this.currentScore = 0;
        this.totalRewardsEarned = 0;
        this.totalPenaltiesIncurred = 0;
    }
}