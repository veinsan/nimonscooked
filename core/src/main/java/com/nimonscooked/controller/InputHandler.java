package com.nimonscooked.controller;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.nimonscooked.controller.command.*;
import com.nimonscooked.model.entity.Chef;

import java.util.LinkedList;
import java.util.Queue;

public class InputHandler extends InputAdapter {
    public Queue<Command> commandQueue = new LinkedList<>();
    public boolean switchChefRequested = false;

    private boolean shiftPressed;
    
    private static final int MAX_QUEUE_SIZE = 5;

    @Override
    public boolean keyDown(int keycode) {
        if (commandQueue.size() >= MAX_QUEUE_SIZE) {
            commandQueue.poll();
        }

        switch (keycode) {
            case Input.Keys.W:
            case Input.Keys.UP:
                if (shiftPressed) {
                    commandQueue.clear();
                    commandQueue.add(new DashCommand(Chef.Direction.UP));
                } else {
                    commandQueue.add(new MoveCommand(Chef.Direction.UP));
                }
                break;
                
            case Input.Keys.S:
            case Input.Keys.DOWN:
                if (shiftPressed) {
                    commandQueue.clear();
                    commandQueue.add(new DashCommand(Chef.Direction.DOWN));
                } else {
                    commandQueue.add(new MoveCommand(Chef.Direction.DOWN));
                }
                break;
                
            case Input.Keys.A:
            case Input.Keys.LEFT:
                if (shiftPressed) {
                    commandQueue.clear();
                    commandQueue.add(new DashCommand(Chef.Direction.LEFT));
                } else {
                    commandQueue.add(new MoveCommand(Chef.Direction.LEFT));
                }
                break;
                
            case Input.Keys.D:
            case Input.Keys.RIGHT:
                if (shiftPressed) {
                    commandQueue.clear();
                    commandQueue.add(new DashCommand(Chef.Direction.RIGHT));
                } else {
                    commandQueue.add(new MoveCommand(Chef.Direction.RIGHT));
                }
                break;

            case Input.Keys.V:
            case Input.Keys.E:
                commandQueue.add(new InteractCommand());
                break;
                
            case Input.Keys.X:
            case Input.Keys.TAB:
                switchChefRequested = true;
                break;
                
            case Input.Keys.K:
            case Input.Keys.F:
                commandQueue.add(new ThrowCommand());
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
            case Input.Keys.SHIFT_LEFT:
            case Input.Keys.SHIFT_RIGHT:
                shiftPressed = false;
                break;
        }
        return true;
    }
    
    public void clearQueue() {
        commandQueue.clear();
    }
}