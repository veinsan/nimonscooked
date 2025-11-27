package com.nimonscooked.manager;

import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;

public class AudioManager {
    private static final AudioManager instance = new AudioManager();
    public static AudioManager getInstance() { return instance; }

    private Music currentMusic;

    // Default values
    private float masterVolume = 1.0f;
    private float musicVolume = 0.5f;
    private float sfxVolume = 1.0f;

    private AudioManager() {}

    // --- PLAYBACK METHODS ---
    public void playMusic(String fileName) {
        Music newMusic = ResourceManager.getInstance().getMusic(fileName);
        if (newMusic == null) return;
        if (currentMusic == newMusic && currentMusic.isPlaying()) return;
        if (currentMusic != null) currentMusic.stop();

        currentMusic = newMusic;
        currentMusic.setLooping(true);
        updateMusicVolume(); // Set volume sesuai setting saat ini
        currentMusic.play();
    }

    public void stopMusic() {
        if (currentMusic != null) currentMusic.stop();
    }

    public void playSound(String fileName) {
        playSound(fileName, 1.0f);
    }

    public void playSound(String fileName, float pitch) {
        Sound sound = ResourceManager.getInstance().getSound(fileName);
        if (sound != null) {
            // Volume total = Master * SFX
            sound.play(masterVolume * sfxVolume, pitch, 0);
        }
    }

    // --- VOLUME SETTERS (Dipanggil dari Options Menu) ---

    public void setMasterVolume(float vol) {
        this.masterVolume = vol;
        updateMusicVolume(); // Update musik yang sedang jalan langsung
    }

    public void setMusicVolume(float vol) {
        this.musicVolume = vol;
        updateMusicVolume();
    }

    public void setSfxVolume(float vol) {
        this.sfxVolume = vol;
    }

    private void updateMusicVolume() {
        if (currentMusic != null) {
            // Volume total = Master * Music
            currentMusic.setVolume(masterVolume * musicVolume);
        }
    }

    // Getters untuk UI Slider
    public float getMasterVolume() { return masterVolume; }
    public float getMusicVolume() { return musicVolume; }
    public float getSfxVolume() { return sfxVolume; }
}
