package com.nimonscooked.manager;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import java.util.HashMap;
import java.util.Map;

public class ShaderManager {
    private static ShaderManager instance;
    
    private Map<String, ShaderProgram> shaders;
    private ShaderProgram currentShader;
    
    private static final String SHADER_PATH = "textures/shaders/";    
    
    private ShaderManager() {
        shaders = new HashMap<>();
        ShaderProgram.pedantic = false;
        loadShaders();
    }
    
    public static ShaderManager getInstance() {
        if (instance == null) {
            instance = new ShaderManager();
        }
        return instance;
    }
    
    private void loadShaders() {
        // Original working shaders
        loadShader("default", "default.vert", "vignette.frag");
        loadShader("lighting", "default.vert", "lighting.frag");
        
        // NEW Octopath-style shaders
        loadShader("advanced_lighting", "default.vert", "advanced_lighting.frag");
        loadShader("atmospheric", "default.vert", "atmospheric.frag");
        
        // TAVERN shader - Warm & Bright! ⭐
        loadShader("tavern", "default.vert", "tavern.frag");
        
        Gdx.app.log("ShaderManager", "Loaded " + shaders.size() + " shaders");
    }
    
    private void loadShader(String name, String vertFile, String fragFile) {
        try {
            String vertPath = SHADER_PATH + vertFile;
            String fragPath = SHADER_PATH + fragFile;
            
            String vertexShader = Gdx.files.internal(vertPath).readString();
            String fragmentShader = Gdx.files.internal(fragPath).readString();
            
            ShaderProgram shader = new ShaderProgram(vertexShader, fragmentShader);
            
            if (!shader.isCompiled()) {
                Gdx.app.error("ShaderManager", "Shader " + name + " failed to compile:");
                Gdx.app.error("ShaderManager", shader.getLog());
                shader.dispose();
                return;
            }
            
            shaders.put(name, shader);
            Gdx.app.log("ShaderManager", "Loaded shader: " + name);
            
        } catch (Exception e) {
            Gdx.app.error("ShaderManager", "Error loading shader " + name, e);
        }
    }
    
    public ShaderProgram getShader(String name) {
        return shaders.getOrDefault(name, null);
    }
    
    public void setShader(String name) {
        ShaderProgram shader = shaders.get(name);
        if (shader != null) {
            currentShader = shader;
        }
    }
    
    public ShaderProgram getCurrentShader() {
        return currentShader;
    }
    
    public void dispose() {
        for (ShaderProgram shader : shaders.values()) {
            shader.dispose();
        }
        shaders.clear();
    }
}