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

    public void interact() {
        int rowtile = chef.getPosition().getRow() + chef.getDirection().dRow;
        int coltile = chef.getPosition().getCol() + chef.getDirection().dCol;
        if (gameMap.isValidPosition(rowtile, coltile)) {
            if (gameMap.getTile(rowtile, coltile).hasStation()) {
               Station station = gameMap.getTile(rowtile, coltile).getStation();
               switch (station) {
                    case AssemblyStation assemblyStation:
                        // Logic for picking up ingredient
                        break;
                    case CookingStation cookingStation:
                        // Logic for cooking item
                        break;
                    case IngredientStorage ingredientStorage:
                        // Logic for plating item
                        break;
                    case ServingCounter servingCounter:
                        // Logic for serving order
                        break;
                }
            }
        }
        
    }

  
}
