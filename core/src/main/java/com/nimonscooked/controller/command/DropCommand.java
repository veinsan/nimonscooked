package com.nimonscooked.controller.command;

import com.nimonscooked.model.entity.Chef;
import com.nimonscooked.manager.MapManager;

public class DropCommand implements Command {
    @Override
    public void execute(Chef chef) {
        if (chef != null) {
            chef.dropItem(MapManager.getInstance().currentMap);
        }
    }
}