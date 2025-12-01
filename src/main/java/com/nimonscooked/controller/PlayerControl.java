package com.nimonscooked.controller;

import com.nimonscooked.model.chef.Chef;
import com.nimonscooked.map.GameMap;
import com.nimonscooked.model.chef.Direction;
import com.nimonscooked.model.station.AssemblyStation;
import com.nimonscooked.model.station.CookingStation;
import com.nimonscooked.model.station.IngredientStorage;
import com.nimonscooked.model.station.ServingCounter;
import com.nimonscooked.model.station.*;

public class PlayerControl {
    private Chef chef;
    private GameMap gameMap;

    public PlayerControl(Chef chef, GameMap gameMap) {
        this.chef = chef;
        this.gameMap = gameMap;
    }

    public void move( Chef chef, Direction direction) {

        chef.setDirection(direction);
        int newRow = chef.getPosition().getRow() + direction.dRow;
        int newCol = chef.getPosition().getCol() + direction.dCol;
        if (gameMap.isValidPosition(newRow, newCol)) {
            chef.move(newRow, newCol);
        }

       
    }
    public void gamemove(String input){
        if (input.equals("W")) {
            move(chef, Direction.UP);
        } else if (input.equals("S")) {
            move(chef, Direction.DOWN);
        } else if (input.equals("A")) {
            move(chef, Direction.LEFT);
        } else if (input.equals("D")) {
            move(chef, Direction.RIGHT);
        }
    }
    public void interact() {
        int rowtile = chef.getPosition().getRow() + chef.getDirection().dRow;
        int coltile = chef.getPosition().getCol() + chef.getDirection().dCol;
        if (gameMap.isValidPosition(rowtile, coltile)) {
            if (gameMap.getTile(rowtile, coltile).hasStation()) {
               Station station = gameMap.getTile(rowtile, coltile).getStation();
               if (station instanceof IngredientStorage) {
                   ((IngredientStorage) station).interact(chef);
               } else if (station instanceof CookingStation) {
                   ((CookingStation) station).interact(chef);
               } else if (station instanceof AssemblyStation) {
                   ((AssemblyStation) station).interact(chef);
               } else if (station instanceof ServingCounter) {
                   ((ServingCounter) station).interact(chef);
            }
        }
        
    }

  
    }

    