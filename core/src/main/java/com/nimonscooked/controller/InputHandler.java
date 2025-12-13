package com.nimonscooked.controller;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.nimonscooked.controller.command.*;
import com.nimonscooked.model.entity.Chef;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

public class InputHandler extends InputAdapter {
    public Queue<Command> commandQueue = new LinkedList<>();
    public boolean switchChefRequested = false;

    private boolean shiftPressed;
    
    private Map<Integer, Boolean> keysHeld = new HashMap<>();
    private Map<Integer, Float> keyHoldTime = new HashMap<>();
    
    private static final int MAX_QUEUE_SIZE = 5;
    private static final float HOLD_THRESHOLD = 0.3f;

    public InputHandler() {
        keysHeld.put(Input.Keys.V, false);
        keysHeld.put(Input.Keys.E, false);
        keyHoldTime.put(Input.Keys.V, 0f);
        keyHoldTime.put(Input.Keys.E, 0f);
    }

    public void update(float delta) {
        for (Integer key : keysHeld.keySet()) {
            if (keysHeld.get(key)) {
                float currentTime = keyHoldTime.get(key);
                keyHoldTime.put(key, currentTime + delta);
            }
        }
    }

    public boolean isInteractHeld() {
        boolean vHeld = keysHeld.getOrDefault(Input.Keys.V, false);
        boolean eHeld = keysHeld.getOrDefault(Input.Keys.E, false);
        
        if (vHeld && keyHoldTime.get(Input.Keys.V) >= HOLD_THRESHOLD) return true;
        if (eHeld && keyHoldTime.get(Input.Keys.E) >= HOLD_THRESHOLD) return true;
        
        return false;
    }

    public float getInteractHoldProgress() {
        float vTime = keysHeld.getOrDefault(Input.Keys.V, false) ? keyHoldTime.get(Input.Keys.V) : 0f;
        float eTime = keysHeld.getOrDefault(Input.Keys.E, false) ? keyHoldTime.get(Input.Keys.E) : 0f;
        
        return Math.max(vTime, eTime);
    }

    @Override
    public boolean keyDown(int keycode) {
        if (commandQueue.size() >= MAX_QUEUE_SIZE) {
            commandQueue.poll();
        }

        switch (keycode) {
            case Input.Keys.W:
            case Input.Keys.UP:
            case Input.Keys.S:
            case Input.Keys.DOWN:
            case Input.Keys.A:
            case Input.Keys.LEFT:
            case Input.Keys.D:
            case Input.Keys.RIGHT:
                break;
                
            case Input.Keys.V:
            case Input.Keys.E:
                if (!keysHeld.get(keycode)) {
                    keysHeld.put(keycode, true);
                    keyHoldTime.put(keycode, 0f);
                }
                break;
                
            case Input.Keys.X:
            case Input.Keys.TAB:
                switchChefRequested = true;
                break;
                
            case Input.Keys.K:
            case Input.Keys.F:
                commandQueue.add(new ThrowCommand());
                break;

            case Input.Keys.Q:
                commandQueue.add(new DropCommand());
                break;

            case Input.Keys.G:
                commandQueue.add(new PickupCommand());
                break;

            case Input.Keys.SHIFT_LEFT:
            case Input.Keys.SHIFT_RIGHT:
                shiftPressed = true;
                break;
        }
        return true;
    }

    @Override
    public boolean keyUp(int keycode) {
        switch (keycode) {
            case Input.Keys.V:
            case Input.Keys.E:
                if (keysHeld.containsKey(keycode)) {
                    float heldTime = keyHoldTime.get(keycode);
                    
                    if (heldTime < HOLD_THRESHOLD) {
                        commandQueue.add(new InteractCommand());
                    }
                    
                    keysHeld.put(keycode, false);
                    keyHoldTime.put(keycode, 0f);
                }
                break;
                
            case Input.Keys.SHIFT_LEFT:
            case Input.Keys.SHIFT_RIGHT:
                shiftPressed = false;
                break;
        }
        return true;
    }
    
    public boolean isMovementKeyPressed() {
        return com.badlogic.gdx.Gdx.input.isKeyPressed(Input.Keys.W) ||
               com.badlogic.gdx.Gdx.input.isKeyPressed(Input.Keys.S) ||
               com.badlogic.gdx.Gdx.input.isKeyPressed(Input.Keys.A) ||
               com.badlogic.gdx.Gdx.input.isKeyPressed(Input.Keys.D) ||
               com.badlogic.gdx.Gdx.input.isKeyPressed(Input.Keys.UP) ||
               com.badlogic.gdx.Gdx.input.isKeyPressed(Input.Keys.DOWN) ||
               com.badlogic.gdx.Gdx.input.isKeyPressed(Input.Keys.LEFT) ||
               com.badlogic.gdx.Gdx.input.isKeyPressed(Input.Keys.RIGHT);
    }

    public Chef.Direction getCurrentDirection() {
        if (com.badlogic.gdx.Gdx.input.isKeyPressed(Input.Keys.W) || 
            com.badlogic.gdx.Gdx.input.isKeyPressed(Input.Keys.UP)) {
            return Chef.Direction.UP;
        }
        if (com.badlogic.gdx.Gdx.input.isKeyPressed(Input.Keys.S) || 
            com.badlogic.gdx.Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            return Chef.Direction.DOWN;
        }
        if (com.badlogic.gdx.Gdx.input.isKeyPressed(Input.Keys.A) || 
            com.badlogic.gdx.Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            return Chef.Direction.LEFT;
        }
        if (com.badlogic.gdx.Gdx.input.isKeyPressed(Input.Keys.D) || 
            com.badlogic.gdx.Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            return Chef.Direction.RIGHT;
        }
        return null;
    }

    public boolean isShiftPressed() {
        return shiftPressed;
    }
    
    public void clearQueue() {
        commandQueue.clear();
    }
}