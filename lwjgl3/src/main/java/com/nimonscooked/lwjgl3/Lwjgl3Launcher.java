package com.nimonscooked.lwjgl3;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.nimonscooked.NimonscookedGame;
import com.nimonscooked.config.GameConfig;

public class Lwjgl3Launcher {
    public static void main(String[] args) {
        if (StartupHelper.startNewJvmIfRequired()) return;
        createApplication();
    }

    private static Lwjgl3Application createApplication() {
        return new Lwjgl3Application(new NimonscookedGame(), getDefaultConfiguration());
    }

    private static Lwjgl3ApplicationConfiguration getDefaultConfiguration() {
        Lwjgl3ApplicationConfiguration configuration = new Lwjgl3ApplicationConfiguration();
        configuration.setTitle("Nimonscooked");
        configuration.useVsync(true);
        configuration.setForegroundFPS(60);

        // --- OPSI 1: Windowed Full HD (Maximized) - REKOMENDASI SAAT DEV ---
        // Ini akan membuka window sebesar 1920x1080, dan otomatis maximize memenuhi layar
        configuration.setWindowedMode(GameConfig.SCREEN_WIDTH, GameConfig.SCREEN_HEIGHT);
        configuration.setMaximized(true);
        configuration.setResizable(true); // Biar bisa di-resize manual kalau perlu

        // --- OPSI 2: True Fullscreen (Game Mode) ---
        // Hapus komentar di bawah ini (dan komen Opsi 1) jika ingin benar-benar Full Screen tanpa border window
        // configuration.setFullscreenMode(Lwjgl3ApplicationConfiguration.getDisplayMode());

        return configuration;
    }
}
