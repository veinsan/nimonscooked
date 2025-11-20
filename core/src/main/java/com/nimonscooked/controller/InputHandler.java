package com.nimonscooked.controller;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;

public class InputHandler extends InputAdapter {
    public boolean up, down, left, right;
    public boolean interact, pickup, switchChef;

    @Override
    public boolean keyDown(int keycode) {
        switch (keycode) {
            case Input.Keys.W: up = true; break;
            case Input.Keys.S: down = true; break;
            case Input.Keys.A: left = true; break;
            case Input.Keys.D: right = true; break;
            case Input.Keys.V: interact = true; break;
            case Input.Keys.C: pickup = true; break;
            case Input.Keys.X: switchChef = true; break;
        }
        return true;
    }

    @Override
    public boolean keyUp(int keycode) {
        switch (keycode) {
            case Input.Keys.W: up = false; break;
            case Input.Keys.S: down = false; break;
            case Input.Keys.A: left = false; break;
            case Input.Keys.D: right = false; break;
            case Input.Keys.V: interact = false; break;
            case Input.Keys.C: pickup = false; break;
            case Input.Keys.X: switchChef = false; break;
        }
        return true;
    }
}
