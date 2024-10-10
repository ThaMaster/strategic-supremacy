package se.umu.cs.ads.sp.model;

import se.umu.cs.ads.sp.events.GameEvent;
import se.umu.cs.ads.sp.events.GameEvents;
import se.umu.cs.ads.sp.model.objects.GameObject;
import se.umu.cs.ads.sp.model.objects.collectables.Chest;
import se.umu.cs.ads.sp.model.objects.collectables.Collectable;
import se.umu.cs.ads.sp.model.objects.collectables.Gold;
import se.umu.cs.ads.sp.model.objects.entities.Entity;
import se.umu.cs.ads.sp.model.objects.entities.units.PlayerUnit;
import se.umu.cs.ads.sp.utils.Constants;
import se.umu.cs.ads.sp.utils.Position;
import se.umu.cs.ads.sp.utils.enums.EventType;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;

public class ObjectHandler {
    private HashMap<Long, PlayerUnit> myUnits = new HashMap<>();
    private HashMap<Long, PlayerUnit> enemyUnits = new HashMap<>();
    private HashMap<Long, Collectable> collectables = new HashMap<>();
    private ArrayList<Long> selectedUnitIds = new ArrayList<>();
    private ArrayList<GameObject> environment;

    public void update(){
        for (PlayerUnit entity : myUnits.values()) {
            entity.update();
            checkCollectables(entity);
        }
        for (PlayerUnit entity : enemyUnits.values()) {
            entity.update();
            checkCollectables(entity);
        }
    }

    public ArrayList<PlayerUnit> getSelectedUnits(){
        ArrayList<PlayerUnit> selectedUnits = new ArrayList<>();
        for(long id : selectedUnitIds){
            selectedUnits.add(myUnits.get(id));
        }
        return selectedUnits;
    }

    public void clearSelectedUnitIds(){
        for(long id : selectedUnitIds){
            myUnits.get(id).setSelected(false);
        }
        this.selectedUnitIds.clear();
    }

    public void addSelectionId(long id){
        myUnits.get(id).setSelected(true);
        this.selectedUnitIds.add(id);
    }

    public void setSelection(Position clickLocation){
        clearSelectedUnitIds();
        for (PlayerUnit unit : myUnits.values()) {
            if (Position.distance(unit.getPosition(), clickLocation) / Constants.ENTITY_WIDTH <= 1) {
                unit.setSelected(true);
                addSelectionId(unit.getId());
                return;
            }
        }
    }

    public ArrayList<Long> getSelectedUnitIds(){
        return selectedUnitIds;
    }

    public void stopSelectedEntities(){
        for (PlayerUnit unit : getSelectedUnits()) {
            unit.setDestination(unit.getPosition());
        }
    }

    public void setSelectedUnits(Rectangle area) {
        ArrayList<Long> hitEntities = new ArrayList<>();
        for (Entity entity : myUnits.values()) {
            entity.setSelected(false);
            if (entity.getCollisionBox().getCollisionShape().intersects(area)) {
                hitEntities.add(entity.getId());
                entity.setSelected(true);
            }

        }
        if (!hitEntities.isEmpty()) {
            selectedUnitIds.clear();
            selectedUnitIds = hitEntities;
        }
    }

    private void checkCollectables(PlayerUnit playerUnit){
        for (Collectable collected : playerUnit.getCollected()) {
            if (collected instanceof Chest) {
                GameEvents.getInstance().addEvent(new GameEvent(collected.getId(), collected.getReward().toString(), EventType.GOLD_PICK_UP));
            } else if (collected instanceof Gold) {
                GameEvents.getInstance().addEvent(new GameEvent(collected.getId(), collected.getReward().toString(), EventType.GOLD_PICK_UP));
            }
        }
        playerUnit.getCollected().clear();
    }

    public void addCollectable(Collectable collectable){
        collectables.put(collectable.getId(), collectable);
    }

    public void addMyUnit(PlayerUnit unit){
        this.myUnits.put(unit.getId(), unit);
    }

    public void addEnemyUnit(PlayerUnit unit){
        this.enemyUnits.put(unit.getId(), unit);
    }


    public HashMap<Long, PlayerUnit> getEnemyUnits() {
        return enemyUnits;
    }

    public HashMap<Long, PlayerUnit> getMyUnits() {
        return myUnits;
    }

    public HashMap<Long, Collectable> getCollectables() {
        return collectables;
    }
}
