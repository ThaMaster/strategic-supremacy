package se.umu.cs.ads.sp.model;

import se.umu.cs.ads.sp.controller.GameController;
import se.umu.cs.ads.sp.model.map.Map;
import se.umu.cs.ads.sp.model.objects.entities.Entity;
import se.umu.cs.ads.sp.utils.Constants;
import se.umu.cs.ads.sp.utils.Position;

import java.util.ArrayList;

public class ModelManager {

    private ArrayList<Entity> gameEntities = new ArrayList<>();
    private GameController gameController;
    private Map map;
    private int selectedUnit = 0;

    public ModelManager(GameController gameController) {
        this.gameController = gameController;
        map = new Map();

        map.loadMap("maps/map1.txt");


        Entity firstUnit = new Entity(0, new Position(100, 100), map);
        Entity secondUnit = new Entity(1, new Position(300, 400), map);
        Entity thirdUnit = new Entity(2, new Position(500, 100), map);
        gameEntities.add(firstUnit);
        gameEntities.add(secondUnit);
        gameEntities.add(thirdUnit);
    }


    public void update() {
        for (Entity entity : gameEntities) {
            entity.update();
        }
    }

    public ArrayList<Entity> getGameEntities() {
        return gameEntities;
    }

    public void setEntityPosition(Position newPosition) {
        gameEntities.get(selectedUnit).setDestination(newPosition);
    }

    public void setSelection(Position clickLocation) {
        ArrayList<Entity> hitEntities = new ArrayList<>();
        for (Entity entity : gameEntities) {
            if (Position.distance(entity.getPosition(), clickLocation) / Constants.ENTITY_WIDTH <= 1) {
                hitEntities.add(entity);
            }
        }

        if (!hitEntities.isEmpty()) {
            if (hitEntities.size() == 1) {
                selectedUnit = hitEntities.get(0).getID();
            } else {
                //Multiple entities were clicked. Get the entity with the closest distance from the click
                selectedUnit = getClosestHitUnit(hitEntities, clickLocation);
            }
        }
    }

    public void setSelection(int selectedUnit) {
        this.selectedUnit = selectedUnit;
    }

    private int getClosestHitUnit(ArrayList<Entity> hitEntities, Position clickLocation) {
        double closestDistance = Double.MAX_VALUE;
        Entity closestEntity = hitEntities.get(0);
        for (Entity entity : hitEntities) {
            if (Position.distance(entity.getPosition(), clickLocation) < closestDistance) {
                closestDistance = Position.distance(entity.getPosition(), clickLocation);
                closestEntity = entity;
            }
        }
        return closestEntity.getID();
    }

    public int getSelectedUnit() {
        return selectedUnit;
    }
}
