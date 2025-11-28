package com.nimonscooked.controller.command;

import com.nimonscooked.model.entity.Chef;
import com.nimonscooked.manager.MapManager;

public class DashCommand implements Command {
    private final Chef.Direction direction;

    public DashCommand(Chef.Direction direction) {
        this.direction = direction;
    }

    @Override
    public void execute(Chef chef) {
        if (chef != null) {
            chef.dash(direction, MapManager.getInstance().currentMap);
        }
    }
}