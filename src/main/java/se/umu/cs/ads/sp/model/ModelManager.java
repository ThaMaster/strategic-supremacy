package se.umu.cs.ads.sp.model;

import jdk.jshell.execution.Util;
import se.umu.cs.ads.sp.model.map.Map;
import se.umu.cs.ads.sp.model.objects.collectables.Chest;
import se.umu.cs.ads.sp.model.objects.collectables.Collectable;
import se.umu.cs.ads.sp.model.objects.collectables.Gold;
import se.umu.cs.ads.sp.model.objects.collectables.Reward;
import se.umu.cs.ads.sp.model.objects.entities.Entity;
import se.umu.cs.ads.sp.model.objects.entities.units.PlayerUnit;
import se.umu.cs.ads.sp.utils.Constants;
import se.umu.cs.ads.sp.utils.Position;
import se.umu.cs.ads.sp.utils.Utils;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;

public class ModelManager {

    // My entities that I can control
    private HashMap<Long, Entity> myEntities = new HashMap<>();

    // Other entities that I cannot control
    private HashMap<Long, Entity> gameEntities = new HashMap<>();
    private HashMap<Long, Collectable> collectables = new HashMap<>();
    private ArrayList<Long> selectedUnits = new ArrayList<>();
    private Map map;

    public ModelManager() {
        map = new Map();
        map.loadMap("maps/map1.txt");

        PlayerUnit firstUnit = new PlayerUnit(new Position(100, 100), map);
        PlayerUnit secondUnit = new PlayerUnit(new Position(300, 400), map);
        PlayerUnit thirdUnit = new PlayerUnit(new Position(500, 100), map);
        myEntities.put(firstUnit.getId(), firstUnit);
        myEntities.put(secondUnit.getId(), secondUnit);
        myEntities.put(thirdUnit.getId(), thirdUnit);

        // Maybe totally separate this?
        gameEntities.put(firstUnit.getId(), firstUnit);
        gameEntities.put(secondUnit.getId(), secondUnit);
        gameEntities.put(thirdUnit.getId(), thirdUnit);


        PlayerUnit firstEnemyUnit = new PlayerUnit(new Position(700, 100), map);
        PlayerUnit secondEnemyUnit = new PlayerUnit(new Position(850, 400), map);
        PlayerUnit thirdEnemyUnit = new PlayerUnit(new Position(800, 100), map);

        gameEntities.put(firstEnemyUnit.getId(), firstEnemyUnit);
        gameEntities.put(secondEnemyUnit.getId(), secondEnemyUnit);
        gameEntities.put(thirdEnemyUnit.getId(), thirdEnemyUnit);

        spawnChest(new Position(600,450), new Reward(10, Reward.RewardType.POINT));
        spawnChest(new Position(500, 500), new Reward(2, Reward.RewardType.MOVEMENT));
        spawnGold(new Position(400, 250));
        spawnGold(new Position(450, 250));
        spawnGold(new Position(500, 250));
    }

    private void spawnChest(Position spawnPosition, Reward reward){
        Chest chest = new Chest(spawnPosition, map);
        chest.setReward(reward);
        collectables.put(chest.getId(), chest);
    }

    private void spawnGold(Position spawnPosition){
        Gold coin = new Gold(spawnPosition, map);
        coin.setReward(new Reward(10, Reward.RewardType.GOLD));
        collectables.put(coin.getId(), coin);
    }

    public void update() {
        // Update all entities in game, including my units.
        for (Entity entity : gameEntities.values()) {
            entity.update();
        }
    }

    public HashMap<Long, Entity> getGameEntities() {
        return gameEntities;
    }

    public HashMap<Long, Collectable> getCollectables() {
        return collectables;
    }

    public boolean setEntityDestination(Position newPosition) {
        if (isWalkable(newPosition)) {
            // Slightly randomise the units so they do not get the EXACT same position.
            for (Long unit : selectedUnits) {
                myEntities.get(unit).setDestination(newPosition);
            }
            return true;
        }
        return false;
    }

    public void setSelection(Position clickLocation) {
        ArrayList<Entity> hitEntities = new ArrayList<>();
        selectedUnits.clear();
        for (Entity entity : myEntities.values()) {
            if (Position.distance(entity.getPosition(), clickLocation) / Constants.ENTITY_WIDTH <= 1) {
                entity.setSelected(true);
                hitEntities.add(entity);
            }
        }

        if (hitEntities.size() == 1) {
            selectedUnits.add(hitEntities.get(0).getId());
        } else if(hitEntities.size() > 1) {
            //Multiple entities were clicked. Get the entity with the closest distance from the click
            selectedUnits.add(getClosestHitUnit(hitEntities, clickLocation));
        }
    }

    public void setSelection(long selectedUnit) {
        this.selectedUnits.clear();
        this.selectedUnits.add(selectedUnit);
        this.myEntities.get(selectedUnit).setSelected(true);
    }

    private long getClosestHitUnit(ArrayList<Entity> hitEntities, Position clickLocation) {
        double closestDistance = Double.MAX_VALUE;
        Entity closestEntity = hitEntities.get(0);
        for (Entity entity : hitEntities) {
            if (Position.distance(entity.getPosition(), clickLocation) < closestDistance) {
                closestDistance = Position.distance(entity.getPosition(), clickLocation);
                closestEntity = entity;
            }
        }
        return closestEntity.getId();
    }

    public ArrayList<Long> getSelectedUnits() {
        return this.selectedUnits;
    }

    public Map getMap() {
        return this.map;
    }

    public void stopSelectedEntities() {
        for (Long unitId : selectedUnits) {
            Entity unit = myEntities.get(unitId);
            unit.setDestination(unit.getPosition());
        }
    }

    public boolean isWalkable(Position position) {
        int row = position.getY() / Constants.TILE_WIDTH;
        int col = position.getX() / Constants.TILE_HEIGHT;

        if (!(col < 0) && !(row < 0) && !(col >= map.getCols()) && !(row >= map.getRows())) {
            return !map.getModelMap().get(row).get(col).hasCollision();
        } else {
            return false;
        }
    }

    public void setSelectedUnits(Rectangle area) {
        ArrayList<Long> hitEntities = new ArrayList<>();
        for (Entity entity : myEntities.values()) {
            entity.setSelected(false);
            if (entity.getCollisionBox().getCollisionShape().intersects(area)) {
                hitEntities.add(entity.getId());
                entity.setSelected(true);
            }

        }
        if (!hitEntities.isEmpty()) {
            selectedUnits.clear();
            selectedUnits = hitEntities;
        }
    }
}
