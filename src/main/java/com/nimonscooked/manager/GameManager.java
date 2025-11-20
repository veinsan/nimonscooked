package com.nimonscooked.manager;

import com.nimonscooked.core.GameState;

public class GameManager {
    private static GameManager instance;
    
    private int totalScore;
    private int currentTurn;
    private boolean isRunning;
    private GameState gameState;
    private long gameStartTime;
    private long gameDuration;

    private GameManager() {
        this.totalScore = 0;
        this.currentTurn = 0;
        this.isRunning = false;
        this.gameState = GameState.MENU;
        this.gameStartTime = 0;
        this.gameDuration = 0;
    }

    public static GameManager getInstance() {
        if (instance == null) {
            synchronized (GameManager.class) {
                if (instance == null) {
                    instance = new GameManager();
                }
            }
        }
        return instance;
    }

    public void startGame() {
        this.isRunning = true;
        this.gameState = GameState.PLAYING;
        this.totalScore = 0;
        this.currentTurn = 0;
        this.gameStartTime = System.currentTimeMillis();
        this.gameDuration = 0;
    }

    public void pauseGame() {
        if (gameState == GameState.PLAYING) {
            this.gameState = GameState.PAUSED;
            updateGameDuration();
        }
    }

    public void resumeGame() {
        if (gameState == GameState.PAUSED) {
            this.gameState = GameState.PLAYING;
            this.gameStartTime = System.currentTimeMillis();
        }
    }

    public void stageCleared() {
        updateGameDuration();
        this.gameState = GameState.STAGE_CLEARED;
        this.isRunning = false;
    }

    public void endGame() {
        updateGameDuration();
        this.isRunning = false;
        this.gameState = GameState.GAME_OVER;
    }

    private void updateGameDuration() {
        if (gameStartTime > 0) {
            gameDuration += System.currentTimeMillis() - gameStartTime;
        }
    }

    public void addScore(int points) {
        this.totalScore += points;
        if (this.totalScore < 0) {
            this.totalScore = 0;
        }
    }

    public void nextTurn() {
        this.currentTurn++;
    }

    public int getTotalScore() {
        return totalScore;
    }

    public void setTotalScore(int score) {
        this.totalScore = score;
    }

    public int getCurrentTurn() {
        return currentTurn;
    }

    public boolean isRunning() {
        return isRunning;
    }

    public GameState getGameState() {
        return gameState;
    }

    public void setGameState(GameState state) {
        this.gameState = state;
    }

    public long getGameDuration() {
        if (gameState == GameState.PLAYING && gameStartTime > 0) {
            return gameDuration + (System.currentTimeMillis() - gameStartTime);
        }
        return gameDuration;
    }

    public String getFormattedDuration() {
        long duration = getGameDuration();
        long seconds = (duration / 1000) % 60;
        long minutes = (duration / (1000 * 60)) % 60;
        long hours = duration / (1000 * 60 * 60);
        
        if (hours > 0) {
            return String.format("%02d:%02d:%02d", hours, minutes, seconds);
        }
        return String.format("%02d:%02d", minutes, seconds);
    }

    public void reset() {
        this.totalScore = 0;
        this.currentTurn = 0;
        this.isRunning = false;
        this.gameState = GameState.MENU;
        this.gameStartTime = 0;
        this.gameDuration = 0;
    }

    public boolean isGameActive() {
        return gameState == GameState.PLAYING || gameState == GameState.PAUSED;
    }

    public boolean isGameEnded() {
        return gameState == GameState.GAME_OVER || gameState == GameState.STAGE_CLEARED;
    }

    @Override
    public String toString() {
        return "GameManager[State=" + gameState + 
               ", Score=" + totalScore + 
               ", Turn=" + currentTurn + 
               ", Duration=" + getFormattedDuration() + "]";
    }
}