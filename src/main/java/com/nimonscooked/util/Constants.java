package com.nimonscooked.util;

public final class Constants {
    
    private Constants() {
        throw new AssertionError("Cannot instantiate Constants class");
    }

    public static final class Map {
        public static final int ROWS = 10;
        public static final int COLS = 14;
        public static final String MAP_A_PATH = "maps/map_a.txt";
        public static final String MAP_B_PATH = "maps/map_b.txt";
        public static final String MAP_C_PATH = "maps/map_c.txt";
        public static final String MAP_D_PATH = "maps/map_d.txt";
    }

    public static final class Game {
        public static final int MAX_CONSECUTIVE_FAILURES = 5;
        public static final int DEFAULT_REWARD = 100;
        public static final int DEFAULT_PENALTY = 20;
        public static final int INITIAL_ORDERS = 2;
        public static final int TARGET_SCORE = 1000;
        public static final long STAGE_TIME_LIMIT_MS = 600000;
    }

    public static final class PlateStorage {
        public static final int INITIAL_CLEAN_PLATES = 4;
    }

    public static final class CookingDevice {
        public static final int BOILING_POT_CAPACITY = 2;
        public static final int FRYING_PAN_CAPACITY = 3;
        public static final int OVEN_CAPACITY = 5;
    }

    public static final class Symbols {
        public static final char WALL = 'X';
        public static final char FLOOR = '.';
        public static final char CHEF_SPAWN = 'V';
        public static final char CUTTING_STATION = 'C';
        public static final char COOKING_STATION = 'R';
        public static final char ASSEMBLY_STATION = 'A';
        public static final char SERVING_COUNTER = 'S';
        public static final char WASHING_STATION = 'W';
        public static final char INGREDIENT_STORAGE = 'I';
        public static final char PLATE_STORAGE = 'P';
        public static final char TRASH_STATION = 'T';
    }

    public static final class Messages {
        public static final String GAME_TITLE = "NIMONSCOOKED - BURGER MAP TYPE C";
        public static final String WELCOME = "Welcome to Nimonscooked!";
        public static final String HELP_MESSAGE = "Help Chef Kebin and Chef Stewart prepare burgers for Evil Nimons!";
        public static final String GAME_OVER = "GAME OVER";
        public static final String STAGE_CLEARED = "STAGE CLEARED!";
        public static final String PRESS_START = "Press Enter to start...";
        public static final String CONFIRM_QUIT = "Are you sure you want to quit? (y/n): ";
        public static final String THANKS_PLAYING = "Thank you for playing Nimonscooked!";
    }

    public static final class Controls {
        public static final char MOVE_UP = 'w';
        public static final char MOVE_DOWN = 's';
        public static final char MOVE_LEFT = 'a';
        public static final char MOVE_RIGHT = 'd';
        public static final char INTERACT = 'v';
        public static final char PICKUP_DROP = 'c';
        public static final char SWITCH_CHEF = 'x';
        public static final char QUIT = 'q';
    }

    public static final class Display {
        public static final String SEPARATOR = "=".repeat(60);
        public static final String SUBSEPARATOR = "-".repeat(60);
        public static final int SCREEN_WIDTH = 60;
    }

    public static final class Colors {
        public static final String RESET = "\033[0m";
        public static final String RED = "\033[31m";
        public static final String GREEN = "\033[32m";
        public static final String YELLOW = "\033[33m";
        public static final String BLUE = "\033[34m";
        public static final String MAGENTA = "\033[35m";
        public static final String CYAN = "\033[36m";
        public static final String WHITE = "\033[37m";
        public static final String BOLD = "\033[1m";
    }

    public static final class Timing {
        public static final int PAUSE_SHORT = 500;
        public static final int PAUSE_MEDIUM = 1000;
        public static final int PAUSE_LONG = 2000;
    }
}