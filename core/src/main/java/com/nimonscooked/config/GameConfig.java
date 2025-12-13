package com.nimonscooked.config;

public class GameConfig {
    public static final int SCREEN_WIDTH = 1920;
    public static final int SCREEN_HEIGHT = 1080;
    public static final int TILE_SIZE = 64;

    public static final float LEVEL_DURATION = 180f;
    public static final int MIN_PASS_SCORE = 100;
    public static final int MAX_FAILED_ORDERS = 5;

    public static final float COOK_TIME = 12f;
    public static final float BURN_TIME = 12f;
    public static final float BURN_WARNING_TIME = 4f;
    public static final float CHOP_TIME = 3f;
    public static final float WASH_TIME = 3f;

    public static final float MIN_ORDER_INTERVAL = 10f;
    public static final float MAX_ORDER_INTERVAL = 20f;
    public static final int MAX_ACTIVE_ORDERS = 4;
    public static final int DEFAULT_ORDER_TIME_LIMIT = 60;

    public static final float SPEED_BONUS_THRESHOLD_HIGH = 0.8f;
    public static final float SPEED_BONUS_THRESHOLD_MED = 0.5f;
    public static final float SPEED_BONUS_MULTIPLIER_HIGH = 1.5f;
    public static final float SPEED_BONUS_MULTIPLIER_MED = 1.0f;
    public static final float SPEED_BONUS_MULTIPLIER_LOW = 0.7f;

    public static final int REWARD_CORRECT_ORDER = 120;
    public static final int PENALTY_WRONG_ORDER = -50;
    public static final int PENALTY_EXPIRED_ORDER = -50;
    public static final int PENALTY_NO_ORDER = -100;

    public static final float DASH_COOLDOWN = 2.0f;
    public static final int DASH_DISTANCE = 3;
    public static final float THROW_DISTANCE = 4f;

    public static final float MOVE_INPUT_DELAY = 0.15f;
    public static final float ACTION_INPUT_DELAY = 0.2f;
    public static final float VISUAL_LERP_SPEED = 15f;
    public static final float CAMERA_LERP_SPEED = 8f;

    public static final int PLATE_RETURN_TIME = 10;
    public static final int MAX_DIRTY_PLATES = 5;

    public static final float MASTER_VOLUME = 1.0f;
    public static final float MUSIC_VOLUME = 0.5f;
    public static final float SFX_VOLUME = 1.0f;

    public static final String RECIPES_PATH = "data/recipes.json";
    public static final String DEFAULT_MAP_PATH = "data/map_c.txt";

    public static final boolean DEBUG_MODE = false;
    public static final boolean SHOW_FPS = false;
}