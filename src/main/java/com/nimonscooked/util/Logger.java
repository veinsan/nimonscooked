package com.nimonscooked.util;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Logger {
    
    private static Logger instance;
    private PrintWriter fileWriter;
    private boolean enableFileLogging;
    private boolean enableConsoleLogging;
    private LogLevel currentLogLevel;

    public enum LogLevel {
        DEBUG(0),
        INFO(1),
        WARNING(2),
        ERROR(3),
        CRITICAL(4);

        private final int level;

        LogLevel(int level) {
            this.level = level;
        }

        public int getLevel() {
            return level;
        }
    }

    private Logger() {
        this.enableFileLogging = false;
        this.enableConsoleLogging = true;
        this.currentLogLevel = LogLevel.INFO;
    }

    public static Logger getInstance() {
        if (instance == null) {
            synchronized (Logger.class) {
                if (instance == null) {
                    instance = new Logger();
                }
            }
        }
        return instance;
    }

    public void enableFileLogging(String filePath) {
        try {
            this.fileWriter = new PrintWriter(new FileWriter(filePath, true), true);
            this.enableFileLogging = true;
            info("File logging enabled: " + filePath);
        } catch (IOException e) {
            error("Failed to enable file logging: " + e.getMessage());
        }
    }

    public void disableFileLogging() {
        if (fileWriter != null) {
            fileWriter.close();
            fileWriter = null;
        }
        enableFileLogging = false;
    }

    public void setConsoleLogging(boolean enabled) {
        this.enableConsoleLogging = enabled;
    }

    public void setLogLevel(LogLevel level) {
        this.currentLogLevel = level;
    }

    private void log(LogLevel level, String message) {
        if (level.getLevel() < currentLogLevel.getLevel()) {
            return;
        }

        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        String logMessage = String.format("[%s] [%s] %s", timestamp, level.name(), message);

        if (enableConsoleLogging) {
            switch (level) {
                case ERROR:
                case CRITICAL:
                    System.err.println(logMessage);
                    break;
                default:
                    System.out.println(logMessage);
                    break;
            }
        }

        if (enableFileLogging && fileWriter != null) {
            fileWriter.println(logMessage);
        }
    }

    public void debug(String message) {
        log(LogLevel.DEBUG, message);
    }

    public void info(String message) {
        log(LogLevel.INFO, message);
    }

    public void warning(String message) {
        log(LogLevel.WARNING, message);
    }

    public void error(String message) {
        log(LogLevel.ERROR, message);
    }

    public void critical(String message) {
        log(LogLevel.CRITICAL, message);
    }

    public void exception(String message, Exception e) {
        error(message + " | Exception: " + e.getClass().getSimpleName() + " - " + e.getMessage());
        if (enableFileLogging && fileWriter != null) {
            e.printStackTrace(fileWriter);
        }
    }

    public void gameEvent(String event) {
        info("[GAME EVENT] " + event);
    }

    public void playerAction(String action) {
        debug("[PLAYER ACTION] " + action);
    }

    public void systemEvent(String event) {
        info("[SYSTEM] " + event);
    }

    public void close() {
        disableFileLogging();
    }
}