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

    private boolean upPressed, downPressed, leftPressed, rightPressed;

    @Override
    public boolean keyDown(int keycode) {
        switch (keycode) {
            case Input.Keys.W:
                commandQueue.add(new MoveCommand(Chef.Direction.UP));
                upPressed = true;
                break;
            case Input.Keys.S:
                commandQueue.add(new MoveCommand(Chef.Direction.DOWN));
                downPressed = true;
                break;
            case Input.Keys.A:
                commandQueue.add(new MoveCommand(Chef.Direction.LEFT));
                leftPressed = true;
                break;
            case Input.Keys.D:
                commandQueue.add(new MoveCommand(Chef.Direction.RIGHT));
                rightPressed = true;
                break;

            case Input.Keys.V:
                commandQueue.add(new InteractCommand());
                break;
            case Input.Keys.X:
                switchChefRequested = true;
                break;
            case Input.Keys.K:
                commandQueue.add(new ThrowCommand());
                break;

            case Input.Keys.SHIFT_LEFT:
                if (upPressed) commandQueue.add(new DashCommand(Chef.Direction.UP));
                else if (downPressed) commandQueue.add(new DashCommand(Chef.Direction.DOWN));
                else if (leftPressed) commandQueue.add(new DashCommand(Chef.Direction.LEFT));
                else if (rightPressed) commandQueue.add(new DashCommand(Chef.Direction.RIGHT));
                break;
        }
        return true;
    }

    @Override
    public boolean keyUp(int keycode) {
        switch (keycode) {
            case Input.Keys.W: upPressed = false; break;
            case Input.Keys.S: downPressed = false; break;
            case Input.Keys.A: leftPressed = false; break;
            case Input.Keys.D: rightPressed = false; break;
        }
        return true;
    }
}