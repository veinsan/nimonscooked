package com.nimonscooked.controller;

import com.badlogic.gdx.Gdx;
import com.nimonscooked.manager.MapManager;
import com.nimonscooked.model.entity.Chef;
import com.nimonscooked.model.map.GridMap;
import com.nimonscooked.model.station.Station;
import com.nimonscooked.controller.command.Command;

import java.util.List;

public class PlayerController {
    private InputHandler inputHandler;
    private MapManager mapManager;
    private GridMap map;

    private float inputCooldown = 0f;
    private static final float ACTION_DELAY = 0.2f;

    public PlayerController(InputHandler inputHandler) {
        this.inputHandler = inputHandler;
        this.mapManager = MapManager.getInstance();
        this.map = mapManager.currentMap;
    }

    public void update(float delta) {
        if (mapManager.currentMap != map) this.map = mapManager.currentMap;

        inputHandler.update(delta);

        if (mapManager.chefs != null) {
            for (Chef c : mapManager.chefs) {
                c.update(delta);
            }
        }

        if (mapManager.activeChef == null) return;

        Chef activeChef = mapManager.activeChef;

        if (!activeChef.isBusy() && inputHandler.isMovementKeyPressed()) {
            activeChef.isMoving = true;
            Chef.Direction dir = inputHandler.getCurrentDirection();
            
            if (dir != null) {
                if (inputHandler.isShiftPressed() && activeChef.canDash()) {
                    activeChef.dash(dir, map);
                } else {
                    activeChef.move(dir, map, delta);
                }
            }
        } else {
            activeChef.isMoving = false;
        }

        handleHoldInteraction(activeChef, delta);

        if (inputCooldown > 0) {
            inputCooldown -= delta;
        } else {
            while (!inputHandler.commandQueue.isEmpty()) {
                Command cmd = inputHandler.commandQueue.poll();
                if (cmd != null) {
                    cmd.execute(activeChef);
                    inputCooldown = ACTION_DELAY;
                    break;
                }
            }
        }

        if (inputHandler.switchChefRequested) {
            switchChef();
            inputHandler.switchChefRequested = false;
            inputCooldown = ACTION_DELAY;
        }
    }

    private void handleHoldInteraction(Chef chef, float delta) {
        if (inputHandler.isInteractHeld()) {
            int targetCol = chef.position.col;
            int targetRow = chef.position.row;

            switch (chef.direction) {
                case UP: targetRow++; break;
                case DOWN: targetRow--; break;
                case LEFT: targetCol--; break;
                case RIGHT: targetCol++; break;
            }

            Station station = mapManager.getStationAt(targetCol, targetRow);
            if (station != null) {
                station.processHold(chef, delta);
            }
        }
    }

    private void switchChef() {
        List<Chef> chefs = mapManager.chefs;
        if (chefs.size() < 2) return;

        int currentIndex = chefs.indexOf(mapManager.activeChef);
        int nextIndex = (currentIndex + 1) % chefs.size();
        mapManager.activeChef = chefs.get(nextIndex);

        Gdx.app.log("PlayerController", "Switched to Chef #" + (nextIndex + 1));
    }
}