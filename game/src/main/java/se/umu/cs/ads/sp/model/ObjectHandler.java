package se.umu.cs.ads.sp.model;

import proto.EntitySkeleton;
import se.umu.cs.ads.ns.app.User;
import se.umu.cs.ads.ns.util.Util;
import se.umu.cs.ads.sp.events.GameEvent;
import se.umu.cs.ads.sp.events.GameEvents;
import se.umu.cs.ads.sp.model.communication.dto.CollectableDTO;
import se.umu.cs.ads.sp.model.communication.dto.EntitySkeletonDTO;
import se.umu.cs.ads.sp.model.communication.dto.EnvironmentDTO;
import se.umu.cs.ads.sp.model.communication.dto.StartGameRequest;
import se.umu.cs.ads.sp.model.map.Map;
import se.umu.cs.ads.sp.model.objects.Environment.Base;
import se.umu.cs.ads.sp.model.objects.GameObject;
import se.umu.cs.ads.sp.model.objects.collectables.Chest;
import se.umu.cs.ads.sp.model.objects.collectables.Collectable;
import se.umu.cs.ads.sp.model.objects.collectables.Gold;
import se.umu.cs.ads.sp.model.objects.collectables.Reward;
import se.umu.cs.ads.sp.model.objects.entities.Entity;
import se.umu.cs.ads.sp.model.objects.entities.units.PlayerUnit;
import se.umu.cs.ads.sp.utils.Constants;
import se.umu.cs.ads.sp.utils.Position;
import se.umu.cs.ads.sp.utils.Utils;
import se.umu.cs.ads.sp.utils.enums.DtoTypes;
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

    public StartGameRequest initializeWorld(Map map, ArrayList<User> users, ModelManager modelManager){
        StartGameRequest startGameRequest = new StartGameRequest(new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
        ArrayList<Position> basePositions = map.getBasePositions();
        for(User user : users){
            long userId = user.id;
            Position basePos = basePositions.get(basePositions.size()-1);
            //Spawning base
            startGameRequest.getEnvironments().add(new EnvironmentDTO(Util.generateId(), userId, basePos, DtoTypes.BASE.type));
            spawnBase(map, basePos);

            //Spawning 3 entities
            for(int i = 0; i < 3; i++){
                Position offsetPosition = basePos;
                do {
                    offsetPosition = new Position((basePos.getX()
                            * Constants.TILE_HEIGHT) + Utils.getRandomInt(-15, 15),
                            (basePos.getY() * Constants.TILE_WIDTH) + Utils.getRandomInt(-15, 15));
                } while (!modelManager.isWalkable(offsetPosition));
                startGameRequest.getEntitySkeletons().add(new EntitySkeletonDTO(Utils.generateId(), userId, offsetPosition));
                spawnUnit(map, offsetPosition, userId == modelManager.getPlayer().id);
            }

            if(!basePositions.isEmpty()){
                basePositions.remove(basePositions.size()-1);
            }
        }
        //TEMP
        spawnGold(map, new Position(200,200));
        startGameRequest.addCollectable(
                new CollectableDTO(Utils.generateId(),
                new Position(200,200),
                DtoTypes.GOLD.type,
                new Reward(10, Reward.RewardType.GOLD))
        );
        return startGameRequest;
    }

    public void populateWorld(StartGameRequest request, Map map, ModelManager modelManager){

        for(EnvironmentDTO env : request.getEnvironments()){
            DtoTypes type = DtoTypes.fromLabel(env.type());
            switch(type){
                case BASE:
                    spawnBase(map, env.position());
                    break;
                case GOLDMINE:
                    break;
            }
        }

        for(CollectableDTO collectable : request.getCollectables()){
            DtoTypes type = DtoTypes.fromLabel(collectable.type());
            switch(type){
                case GOLD:
                    spawnGold(map, collectable.position());
                    break;
                default:
                    System.out.println("Unexpected type on collectable");
                    break;
            }
        }

        for(EntitySkeletonDTO unit : request.getEntitySkeletons()){
            spawnUnit(map, unit.position(), unit.userId() == modelManager.getPlayer().id);
        }
    }

    private void spawnBase(Map map, Position position){
        Base base = new Base(position);
        base.spawn(map);
    }

    private void spawnGold(Map map, Position position){
        Gold coin = new Gold(position, map);
        coin.setReward(new Reward(10, Reward.RewardType.GOLD));
        addCollectable(coin);
    }

    private void spawnUnit(Map map, Position position, boolean myUnit){
        PlayerUnit unit = new PlayerUnit(position, map);
        if(myUnit){
            myUnits.put(unit.getId(), unit);
        }else{
            enemyUnits.put(unit.getId(), unit);
        }
    }
}
