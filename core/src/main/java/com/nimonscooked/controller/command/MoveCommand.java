package com.nimonscooked.controller.command;

import com.nimonscooked.model.entity.Chef; // Import Chef
import com.nimonscooked.manager.MapManager;

public class MoveCommand implements Command {
    private final Chef.Direction direction;

    public MoveCommand(Chef.Direction direction) {
        this.direction = direction;
    }

    @Override
    public void execute(Chef chef) {
        if (chef != null) {
            chef.move(direction, MapManager.getInstance().currentMap);
        }
    }
}
