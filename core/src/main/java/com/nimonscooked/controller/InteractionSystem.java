package com.nimonscooked.core;

import com.nimonscooked.model.chef.Chef;
import com.nimonscooked.map.GameMap;
import com.nimonscooked.map.Tile;
import com.nimonscooked.model.Item;
import com.nimonscooked.model.dish.Dish;
import com.nimonscooked.model.ingredient.Ingredient;
import com.nimonscooked.model.utensil.Plate;
import com.nimonscooked.model.utensil.FryingPan;
import com.nimonscooked.model.utensil.BoilingPot;
import com.nimonscooked.model.utensil.CookingDevice;
import com.nimonscooked.model.station.*;
import com.nimonscooked.model.order.OrderManager;

import java.util.ArrayList;
import java.util.List;

public class InteractionSystem {

    public static boolean pickFromFloor(Chef chef, GameMap map) {
        Tile frontTile = chef.getFrontTile(map);
        if (frontTile == null || !frontTile.hasItem()) {
            return false;
        }

        Item floorItem = frontTile.getItem();
        Item chefItem = chef.getInventory();

        if (chefItem instanceof Plate plate && plate.isClean()) {
            if (canBePlated(floorItem)) {
                performPlating(plate, floorItem, chef, frontTile);
                return true;
            }
        }

        if (!chef.hasItem()) {
            chef.setInventory(floorItem);
            frontTile.setItem(null);
            System.out.println("Picked up: " + floorItem.getDisplayName());
            return true;
        }

        return false;
    }

    public static boolean dropToFloor(Chef chef, GameMap map) {
        Tile frontTile = chef.getFrontTile(map);
        if (frontTile == null || !chef.hasItem()) {
            return false;
        }

        if (frontTile.hasItem()) {
            System.out.println("Cannot drop here - space occupied!");
            return false;
        }

        Item droppedItem = chef.removeInventory();
        frontTile.setItem(droppedItem);
        System.out.println("Dropped: " + droppedItem.getDisplayName());
        return true;
    }

    public static void interact(Chef chef, GameMap map, OrderManager orderManager) {
        Tile frontTile = chef.getFrontTile(map);
        if (frontTile == null || !frontTile.hasStation()) {
            if (!pickFromFloor(chef, map)) {
                dropToFloor(chef, map);
            }
            return;
        }

        Station station = frontTile.getStation();

        if (station instanceof IngredientStorage) {
            handleIngredientStorage(chef, (IngredientStorage) station);
        } else if (station instanceof PlateStorage) {
            handlePlateStorage(chef, (PlateStorage) station);
        } else if (station instanceof CuttingStation) {
            handleCuttingStation(chef, (CuttingStation) station);
        } else if (station instanceof CookingStation) {
            handleCookingStation(chef, (CookingStation) station);
        } else if (station instanceof AssemblyStation) {
            handleAssemblyStation(chef, (AssemblyStation) station);
        } else if (station instanceof ServingCounter) {
            handleServingCounter(chef, (ServingCounter) station, orderManager);
        } else if (station instanceof WashingStation) {
            handleWashingStation(chef, (WashingStation) station);
        } else if (station instanceof TrashStation) {
            handleTrashStation(chef, (TrashStation) station);
        }
    }

    private static void handleIngredientStorage(Chef chef, IngredientStorage storage) {
        if (!chef.hasItem()) {
            Ingredient ingredient = storage.getIngredient();
            chef.setInventory(ingredient);
            System.out.println("Picked up: " + ingredient.getDisplayName());
        } else {
            System.out.println("Hands full! Cannot take ingredient.");
        }
    }

    private static void handlePlateStorage(Chef chef, PlateStorage storage) {
        if (!chef.hasItem()) {
            Plate plate = storage.getPlate();
            chef.setInventory(plate);
            System.out.println("Picked up: Clean Plate");
        } else {
            System.out.println("Hands full! Cannot take plate.");
        }
    }

    private static void handleCuttingStation(Chef chef, CuttingStation station) {
        Item chefItem = chef.getInventory();

        if (!chef.hasItem() && station.hasItem()) {
            chef.setInventory(station.getItemOnStation());
            station.setItemOnStation(null);
            System.out.println("Picked up item from cutting station");
            return;
        }

        if (chef.hasItem() && !station.hasItem()) {
            if (chefItem instanceof Ingredient ingredient) {
                if (ingredient.canBeChopped()) {
                    Ingredient processed = (Ingredient) station.process(ingredient);
                    if (processed != null) {
                        station.setItemOnStation(processed);
                        chef.removeInventory();
                        System.out.println("✂️ Cutting: " + ingredient.getName() + "...");
                    } else {
                        System.out.println("Cannot cut this item!");
                    }
                } else {
                    System.out.println("This item is already chopped or cannot be chopped!");
                }
            } else {
                System.out.println("Can only cut ingredients!");
            }
            return;
        }

        System.out.println("Station is occupied or invalid action!");
    }

    private static void handleCookingStation(Chef chef, CookingStation station) {
        Item chefItem = chef.getInventory();

        if (!station.hasCookingDevice()) {
            if (chefItem instanceof CookingDevice device) {
                if (((Item) device).getName().equals("Frying Pan") ||
                    ((Item) device).getName().equals("Boiling Pot")) {
                    station.placeCookingDevice(device);
                    chef.removeInventory();
                    System.out.println("Placed " + ((Item) device).getName() + " on stove");
                    return;
                }
            }
            System.out.println("This cooking station needs a FryingPan or BoilingPot!");
            return;
        }

        if (!chef.hasItem() && station.hasItem()) {
            chef.setInventory(station.getItemOnStation());
            station.setItemOnStation(null);
            System.out.println("Picked up item from cooking station");
            return;
        }

        if (chef.hasItem() && !station.hasItem()) {
            if (chefItem instanceof Ingredient ingredient) {
                if (ingredient.canBeCooked()) {
                    Ingredient processed = (Ingredient) station.process(ingredient);
                    if (processed != null) {
                        station.setItemOnStation(processed);
                        chef.removeInventory();
                        System.out.println("🔥 Cooking: " + ingredient.getName() + "...");
                    } else {
                        System.out.println("Cannot cook this item with current device!");
                    }
                } else {
                    System.out.println("This item cannot be cooked!");
                }
            } else {
                System.out.println("Can only cook ingredients!");
            }
            return;
        }

        System.out.println("Station is occupied or invalid action!");
    }

    private static void handleAssemblyStation(Chef chef, AssemblyStation station) {
        Item chefItem = chef.getInventory();

        if (!chef.hasItem() && station.hasItem()) {
            Item stationItem = station.getItemOnStation();
            chef.setInventory(stationItem);
            station.setItemOnStation(null);
            System.out.println("Picked up: " + stationItem.getDisplayName());
            return;
        }

        if (chef.hasItem() && !station.hasItem()) {
            station.setItemOnStation(chefItem);
            chef.removeInventory();
            System.out.println("Placed on assembly station: " + chefItem.getDisplayName());
            return;
        }

        if (chef.hasItem() && station.hasItem()) {
            Item stationItem = station.getItemOnStation();

            if (chefItem instanceof Plate plate && plate.isClean() && canBePlated(stationItem)) {
                performPlatingOnStation(plate, stationItem, chef, station);
            } else if (stationItem instanceof Plate plate && plate.isClean() && canBePlated(chefItem)) {
                performPlatingFromChef(plate, chefItem, chef, station);
            } else {
                System.out.println("Cannot combine these items!");
            }
        }
    }

    private static void handleServingCounter(Chef chef, ServingCounter counter, OrderManager orderManager) {
        if (!chef.hasItem()) {
            System.out.println("❌ No dish to serve!");
            return;
        }

        Item chefItem = chef.getInventory();

        if (!(chefItem instanceof Plate plate)) {
            System.out.println("❌ Must serve dish on a plate!");
            return;
        }

        if (plate.getContainedDish() == null) {
            System.out.println("❌ Plate is empty!");
            return;
        }

        Dish dish = plate.getContainedDish();
        boolean success = orderManager.tryServe(dish);

        if (success) {
            System.out.println("✅ Order completed successfully!");
            plate.setClean(false);
            plate.setContainedDish(null);
            counter.setItemOnStation(plate);
            chef.removeInventory();
        } else {
            System.out.println("❌ Wrong dish! Try again.");
        }
    }

    private static void handleWashingStation(Chef chef, WashingStation station) {
        if (!chef.hasItem()) {
            System.out.println("No plate to wash!");
            return;
        }

        Item chefItem = chef.getInventory();
        if (!(chefItem instanceof Plate plate)) {
            System.out.println("Can only wash plates!");
            return;
        }

        if (plate.isClean()) {
            System.out.println("Plate is already clean!");
            return;
        }

        Item cleaned = station.wash(plate);
        chef.setInventory(cleaned);
        System.out.println("✨ Plate washed and clean!");
    }

    private static void handleTrashStation(Chef chef, TrashStation station) {
        if (!chef.hasItem()) {
            System.out.println("No item to trash!");
            return;
        }

        Item trashedItem = chef.removeInventory();
        System.out.println("🗑️ Trashed: " + trashedItem.getDisplayName());
    }

    private static boolean canBePlated(Item item) {
        if (item instanceof Ingredient ingredient) {
            return ingredient.canBePlacedOnPlate();
        }
        return item instanceof Dish;
    }

    private static void performPlating(Plate plate, Item item, Chef chef, Tile tile) {
        List<Item> components = new ArrayList<>();

        if (plate.getContainedDish() != null) {
            components.addAll(plate.getContainedDish().getComponents());
        }

        components.add(item);

        Dish newDish = new Dish("Assembled Dish", components);
        plate.setContainedDish(newDish);
        tile.setItem(null);

        System.out.println("🍽️ Plated: " + item.getDisplayName());
    }

    private static void performPlatingOnStation(Plate plate, Item stationItem, Chef chef, AssemblyStation station) {
        List<Item> components = new ArrayList<>();

        if (plate.getContainedDish() != null) {
            components.addAll(plate.getContainedDish().getComponents());
        }

        components.add(stationItem);

        Dish newDish = new Dish("Assembled Dish", components);
        plate.setContainedDish(newDish);
        station.setItemOnStation(null);

        System.out.println("🍽️ Plated from station: " + stationItem.getDisplayName());
    }

    private static void performPlatingFromChef(Plate plate, Item chefItem, Chef chef, AssemblyStation station) {
        List<Item> components = new ArrayList<>();

        if (plate.getContainedDish() != null) {
            components.addAll(plate.getContainedDish().getComponents());
        }

        components.add(chefItem);

        Dish newDish = new Dish("Assembled Dish", components);
        plate.setContainedDish(newDish);
        chef.removeInventory();

        System.out.println("🍽️ Plated onto station plate: " + chefItem.getDisplayName());
    }
}