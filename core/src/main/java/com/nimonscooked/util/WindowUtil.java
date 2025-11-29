package com.nimonscooked.util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics;

public class WindowUtil {
    
    private static boolean wasMaximized = true;
    
    public static void setMaximizedWindowed() {
        if (Gdx.graphics.isFullscreen()) {
            Graphics.Monitor primaryMonitor = Gdx.graphics.getPrimaryMonitor();
            Graphics.DisplayMode displayMode = Gdx.graphics.getDisplayMode(primaryMonitor);
            
            Gdx.graphics.setWindowedMode(displayMode.width, displayMode.height);
            wasMaximized = true;
        }
    }
    
    public static void toggleFullscreen() {
        if (Gdx.graphics.isFullscreen()) {
            setMaximizedWindowed();
        } else {
            Graphics.DisplayMode currentMode = Gdx.graphics.getDisplayMode();
            Gdx.graphics.setFullscreenMode(currentMode);
        }
    }
    
    public static boolean wasMaximized() {
        return wasMaximized;
    }
}