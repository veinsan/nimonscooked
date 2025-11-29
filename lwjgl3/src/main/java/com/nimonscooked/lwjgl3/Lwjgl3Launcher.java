package com.nimonscooked.lwjgl3;

import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3WindowAdapter;
import com.nimonscooked.NimonscookedGame;
import com.nimonscooked.config.GameConfig;

public class Lwjgl3Launcher {
    
    private static int windowedWidth = GameConfig.SCREEN_WIDTH;
    private static int windowedHeight = GameConfig.SCREEN_HEIGHT;
    
    public static void main(String[] args) {
        if (StartupHelper.startNewJvmIfRequired()) return;
        createApplication();
    }

    private static Lwjgl3Application createApplication() {
        return new Lwjgl3Application(new NimonscookedGame(), getDefaultConfiguration());
    }

    private static Lwjgl3ApplicationConfiguration getDefaultConfiguration() {
        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
        
        config.setTitle("Nimonscooked");
        config.setWindowIcon("icon/icon128.png", "icon/icon64.png", "icon/icon32.png", "icon/icon16.png");
        
        config.useVsync(true);
        config.setForegroundFPS(60);
        config.setIdleFPS(30);
        
        Graphics.DisplayMode primaryMode = Lwjgl3ApplicationConfiguration.getDisplayMode();
        windowedWidth = primaryMode.width;
        windowedHeight = primaryMode.height;
        
        config.setWindowedMode(windowedWidth, windowedHeight);
        config.setDecorated(true);
        config.setResizable(true);
        config.setMaximized(true);
        
        config.setWindowListener(new Lwjgl3WindowAdapter() {
            @Override
            public void filesDropped(String[] files) {
                
            }
            
            @Override
            public boolean closeRequested() {
                return true;
            }
        });
        
        return config;
    }
    
    public static void setWindowedMode(int width, int height) {
        windowedWidth = width;
        windowedHeight = height;
    }
    
    public static int getWindowedWidth() {
        return windowedWidth;
    }
    
    public static int getWindowedHeight() {
        return windowedHeight;
    }
}