package com.nimonscooked.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class GameConfig {
    private static GameConfig instance;
    private final Properties properties;

    private GameConfig() {
        properties = new Properties();
        loadDefaultConfig();
    }

    public static GameConfig getInstance() {
        if (instance == null) {
            synchronized (GameConfig.class) {
                if (instance == null) {
                    instance = new GameConfig();
                }
            }
        }
        return instance;
    }

    private void loadDefaultConfig() {
        properties.setProperty("game.max_consecutive_failures", "5");
        properties.setProperty("game.default_reward", "100");
        properties.setProperty("game.default_penalty", "20");
        properties.setProperty("game.initial_orders", "2");
        properties.setProperty("map.rows", "10");
        properties.setProperty("map.cols", "14");
        properties.setProperty("plate.initial_count", "4");
        properties.setProperty("cooking.boiling_pot_capacity", "2");
        properties.setProperty("cooking.frying_pan_capacity", "3");
        properties.setProperty("cooking.oven_capacity", "5");
    }

    public void loadFromFile(String configPath) {
        try (InputStream input = getClass().getClassLoader().getResourceAsStream(configPath)) {
            if (input != null) {
                properties.load(input);
                System.out.println("✓ Configuration loaded from: " + configPath);
            }
        } catch (IOException e) {
            System.err.println("⚠ Could not load config file, using defaults");
        }
    }

    public int getInt(String key, int defaultValue) {
        String value = properties.getProperty(key);
        if (value != null) {
            try {
                return Integer.parseInt(value);
            } catch (NumberFormatException e) {
                return defaultValue;
            }
        }
        return defaultValue;
    }

    public String getString(String key, String defaultValue) {
        return properties.getProperty(key, defaultValue);
    }

    public boolean getBoolean(String key, boolean defaultValue) {
        String value = properties.getProperty(key);
        if (value != null) {
            return Boolean.parseBoolean(value);
        }
        return defaultValue;
    }

    public void setProperty(String key, String value) {
        properties.setProperty(key, value);
    }

    public int getMaxConsecutiveFailures() {
        return getInt("game.max_consecutive_failures", 5);
    }

    public int getDefaultReward() {
        return getInt("game.default_reward", 100);
    }

    public int getDefaultPenalty() {
        return getInt("game.default_penalty", 20);
    }

    public int getInitialOrders() {
        return getInt("game.initial_orders", 2);
    }

    public int getMapRows() {
        return getInt("map.rows", 10);
    }

    public int getMapCols() {
        return getInt("map.cols", 14);
    }

    public int getInitialPlateCount() {
        return getInt("plate.initial_count", 4);
    }
}