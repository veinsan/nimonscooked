package com.nimonscooked.controller;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.nimonscooked.controller.command.*;
// PASTIKAN IMPORT INI ADA
import com.nimonscooked.model.entity.Chef;

import java.util.LinkedList;
import java.util.Queue;

public class InputHandler extends InputAdapter {
    public Queue<Command> commandQueue = new LinkedList<>();
    public boolean switchChefRequested = false;

    @Override
    public boolean keyDown(int keycode) {
        switch (keycode) {
            // Gunakan Chef.Direction.[ARAH]
            case Input.Keys.W: commandQueue.add(new MoveCommand(Chef.Direction.UP)); break;
            case Input.Keys.S: commandQueue.add(new MoveCommand(Chef.Direction.DOWN)); break;
            case Input.Keys.A: commandQueue.add(new MoveCommand(Chef.Direction.LEFT)); break;
            case Input.Keys.D: commandQueue.add(new MoveCommand(Chef.Direction.RIGHT)); break;

            case Input.Keys.V: commandQueue.add(new InteractCommand()); break;
            case Input.Keys.X: switchChefRequested = true; break;
        }
        return true;
    }
}
