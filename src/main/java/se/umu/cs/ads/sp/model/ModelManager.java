package se.umu.cs.ads.sp.model;

import org.checkerframework.checker.units.qual.A;
import se.umu.cs.ads.sp.model.map.Map;
import se.umu.cs.ads.sp.model.objects.GameObject;
import se.umu.cs.ads.sp.model.objects.collectables.Chest;
import se.umu.cs.ads.sp.model.objects.collectables.Collectable;
import se.umu.cs.ads.sp.model.objects.collectables.Gold;
import se.umu.cs.ads.sp.model.objects.entities.Entity;
import se.umu.cs.ads.sp.utils.Constants;
import se.umu.cs.ads.sp.utils.Position;
import se.umu.cs.ads.sp.utils.Utils;

import java.awt.*;
import java.util.ArrayList;
public class ModelManager {

    private ArrayList<Entity> gameEntities = new ArrayList<>();
    private ArrayList<Collectable> collectables = new ArrayList<>();
    private ArrayList<Integer> selectedUnit = new ArrayList<>();
    private Map map;
    private int collidedCollectable = -1;


    public ModelManager() {
        map = new Map();
        selectedUnit = new ArrayList<>();
        map.loadMap("maps/map1.txt");

        Entity firstUnit = new Entity(0, new Position(100, 100), map);
        Entity secondUnit = new Entity(1, new Position(300, 400), map);
        Entity thirdUnit = new Entity(2, new Position(500, 100), map);
        selectedUnit.add(0);
        gameEntities.add(firstUnit);
        gameEntities.add(secondUnit);
        gameEntities.add(thirdUnit);

        Position chestPosition = new Position(200, 100);
        Chest firstChest = new Chest(chestPosition, new Gold(chestPosition), map);
        collectables.add(firstChest);
    }

    public void update() {
        for (Entity entity : gameEntities) {
            entity.update();
        }
    }

    public ArrayList<Entity> getGameEntities() {
        return gameEntities;
    }

    public ArrayList<Collectable> getCollectables() {

        return collectables;
    }

    public void setEntityDestination(Position newPosition) {
        for(Integer unit : selectedUnit) {
            gameEntities.get(unit).setDestination(newPosition);
        }
    }

    public void setSelection(Position clickLocation) {
        ArrayList<Entity> hitEntities = new ArrayList<>();
        int prevSelectedUnit = selectedUnit.get(0);
        selectedUnit.clear();
        for (Entity entity : gameEntities) {
            if (Position.distance(entity.getPosition(), clickLocation) / Constants.ENTITY_WIDTH <= 1) {
                hitEntities.add(entity);
            }
        }

        if (!hitEntities.isEmpty()) {
            if (hitEntities.size() == 1) {
                selectedUnit.add(hitEntities.get(0).getID());
            } else {
                //Multiple entities were clicked. Get the entity with the closest distance from the click
                selectedUnit.add(getClosestHitUnit(hitEntities, clickLocation));
            }
        }else{
            selectedUnit.add(prevSelectedUnit);
        }
    }

    public void setSelection(int selectedUnit) {
        this.selectedUnit.clear();
        this.selectedUnit.add(selectedUnit);
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
        return this.selectedUnit.get(0);
    }

    public Map getMap() {
        return this.map;
    }

    public void stopSelectedEntity() {
        Entity unit = gameEntities.get(this.selectedUnit.get(0));
        unit.setDestination(unit.getPosition());
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

    public void setSelectedUnit(Rectangle area){
        ArrayList<Integer> hitEntities = new ArrayList<>();
        for(Entity entity : gameEntities) {

            if(entity.getCollisionBox().getCollisionShape().intersects(area)){
                hitEntities.add(entity.getID());

            }

        }
        if(!hitEntities.isEmpty()){
            selectedUnit.clear();
            selectedUnit = hitEntities;
        }
    }

    public void setCollidedCollectable(int i) {
        this.collidedCollectable = i;
    }
}
