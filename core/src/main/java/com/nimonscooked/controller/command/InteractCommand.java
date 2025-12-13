package com.nimonscooked.controller.command;

import com.nimonscooked.model.entity.Chef;
import com.nimonscooked.model.station.Station;
import com.nimonscooked.manager.MapManager;

public class InteractCommand implements Command {
    @Override
    public void execute(Chef chef) {
        if (chef.isBusy()) return;

        int targetCol = chef.position.col;
        int targetRow = chef.position.row;

        switch (chef.direction) {
            case UP: targetRow++; break;
            case DOWN: targetRow--; break;
            case LEFT: targetCol--; break;
            case RIGHT: targetCol++; break;
        }

        Station station = MapManager.getInstance().getStationAt(targetCol, targetRow);
        if (station != null) {
            station.interact(chef);
        }
    }
}