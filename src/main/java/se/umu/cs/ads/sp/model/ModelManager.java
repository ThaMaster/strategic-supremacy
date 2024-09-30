package se.umu.cs.ads.sp.model;

import se.umu.cs.ads.sp.model.map.Map;
import se.umu.cs.ads.sp.model.objects.GameObject;
import se.umu.cs.ads.sp.model.objects.GoldMine;
import se.umu.cs.ads.sp.model.objects.GameObject;
import se.umu.cs.ads.sp.model.objects.collectables.Chest;
import se.umu.cs.ads.sp.model.objects.collectables.Collectable;
import se.umu.cs.ads.sp.model.objects.collectables.Gold;
import se.umu.cs.ads.sp.model.objects.collectables.Reward;
import se.umu.cs.ads.sp.model.objects.entities.Entity;
import se.umu.cs.ads.sp.model.objects.entities.units.PlayerUnit;
import se.umu.cs.ads.sp.utils.Constants;
import se.umu.cs.ads.sp.utils.Position;
import se.umu.cs.ads.sp.utils.UpdateEvent;
import se.umu.cs.ads.sp.utils.Utils;
import se.umu.cs.ads.sp.utils.enums.EventType;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;

public class ModelManager {

    private int currentGold;
    private int currentPoints;
    private final Map map;

    // My entities that I can control
    private HashMap<Long, Entity> myEntities = new HashMap<>();

    // Other entities that I cannot control
    private HashMap<Long, Entity> gameEntities = new HashMap<>();

    // All collectables
    private HashMap<Long, Collectable> collectables = new HashMap<>();

    private ArrayList<Long> selectedUnits = new ArrayList<>();
    private ArrayList<UpdateEvent> events;
    private ArrayList<GameObject> environment;

    public ModelManager() {
        map = new Map();
        map.loadMap("maps/map1.txt");
        events = new ArrayList<>();
        PlayerUnit firstUnit = new PlayerUnit(new Position(100, 100), map);
        PlayerUnit secondUnit = new PlayerUnit(new Position(300, 400), map);
        PlayerUnit thirdUnit = new PlayerUnit(new Position(500, 100), map);
        myEntities.put(firstUnit.getId(), firstUnit);
        myEntities.put(secondUnit.getId(), secondUnit);
        myEntities.put(thirdUnit.getId(), thirdUnit);

        // Maybe totally separate this?
        gameEntities.put(firstUnit.getId(), firstUnit);
        //gameEntities.put(secondUnit.getId(), secondUnit);
        //gameEntities.put(thirdUnit.getId(), thirdUnit);


        PlayerUnit firstEnemyUnit = new PlayerUnit(new Position(700, 100), map);
        PlayerUnit secondEnemyUnit = new PlayerUnit(new Position(850, 400), map);
        PlayerUnit thirdEnemyUnit = new PlayerUnit(new Position(800, 100), map);

        //gameEntities.put(firstEnemyUnit.getId(), firstEnemyUnit);
        //gameEntities.put(secondEnemyUnit.getId(), secondEnemyUnit);
        //gameEntities.put(thirdEnemyUnit.getId(), thirdEnemyUnit);

        spawnChest(new Position(600,450), new Reward(10, Reward.RewardType.POINT));
        spawnChest(new Position(500, 500), new Reward(2, Reward.RewardType.MOVEMENT));
        spawnGold(new Position(400, 250));
        spawnGold(new Position(450, 250));
        spawnGold(new Position(500, 250));
        spawnGoldMine(new Position(200,200));
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

    private void spawnGoldMine(Position spawnPosition){
        GoldMine goldMine = new GoldMine(spawnPosition,10);
        goldMine.spawn(map);
    }

    public void update() {
        // Update all entities in game, including my units.
        for (Entity entity : gameEntities.values()) {
            entity.update();
        }

        for (Entity entity : getGameEntities().values()) {
            if(entity instanceof PlayerUnit playerUnit){
                for (Collectable collected : playerUnit.getCollected()) {
                    //Should do here for example increment gold, points, add buffs depending on collected

                    if(collected instanceof Chest chest){
                        events.add(new UpdateEvent(collected.getId(), collected.getReward().toString(), EventType.CHEST_PICK_UP));
                    }
                    else if(collected instanceof Gold gold){
                        events.add(new UpdateEvent(collected.getId(), collected.getReward().toString(), EventType.GOLD_PICK_UP));
                    }
                }
                playerUnit.getCollected().clear();
            }
        }
    }

    public ArrayList<UpdateEvent> getEvents() {
        return events;
    }

    public void clearEvents(){
        this.events.clear();
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
            Position offsetPosition = newPosition;
            for (Long unit : selectedUnits) {
                myEntities.get(unit).setDestination(offsetPosition);
                do{
                    offsetPosition = new Position(newPosition.getX() + Utils.getRandomInt(-15, 15), newPosition.getY() + Utils.getRandomInt(-15, 15));
                }while(!isWalkable(offsetPosition));
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
        if (!(position.getY() < 0 || position.getX() < 0)) {
            int row = position.getY() / Constants.TILE_WIDTH;
            int col = position.getX() / Constants.TILE_HEIGHT;
            if (!(col < 0) && !(row < 0) && !(col >= map.getCols()) && !(row >= map.getRows())) {
                return !map.getModelMap().get(row).get(col).hasCollision();
            }
        }
        return false;
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
