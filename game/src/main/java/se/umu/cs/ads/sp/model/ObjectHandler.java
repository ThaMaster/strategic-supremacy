package se.umu.cs.ads.sp.model;

import se.umu.cs.ads.ns.app.User;
import se.umu.cs.ads.ns.util.Util;
import se.umu.cs.ads.sp.events.GameEvent;
import se.umu.cs.ads.sp.events.GameEvents;
import se.umu.cs.ads.sp.model.communication.dto.*;
import se.umu.cs.ads.sp.model.map.Map;
import se.umu.cs.ads.sp.model.objects.GameObject;
import se.umu.cs.ads.sp.model.objects.collectables.*;
import se.umu.cs.ads.sp.model.objects.entities.units.PlayerUnit;
import se.umu.cs.ads.sp.model.objects.environment.Base;
import se.umu.cs.ads.sp.model.objects.environment.Environment;
import se.umu.cs.ads.sp.model.objects.environment.GoldMine;
import se.umu.cs.ads.sp.utils.Constants;
import se.umu.cs.ads.sp.utils.Position;
import se.umu.cs.ads.sp.utils.Utils;
import se.umu.cs.ads.sp.utils.enums.*;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;

public class ObjectHandler {
    private HashMap<Long, PlayerUnit> myUnits = new HashMap<>();
    private HashMap<Long, PlayerUnit> enemyUnits = new HashMap<>();
    private HashMap<Long, Collectable> collectables = new HashMap<>();
    private HashMap<Long, Environment> environments = new HashMap<>();
    private ArrayList<Long> selectedUnitIds = new ArrayList<>();
    private ArrayList<Long> pickedUpCollectableIds = new ArrayList<>();

    private User user;

    public ObjectHandler(User user) {
        this.user = user;
    }

    public void update(Map map) {
        for (PlayerUnit entity : myUnits.values()) {
            entity.update();

            if (entity.getState() == EntityState.DEAD && entity.hasFlag()) {
                spawnFlag(map, entity.getPosition());
                entity.setHasFlag(false);
            }

            checkCollectables(entity);
        }

        for (PlayerUnit entity : enemyUnits.values()) {
            entity.update();
            checkCollectables(entity);
        }
    }

    public ArrayList<PlayerUnit> getSelectedUnits() {
        ArrayList<PlayerUnit> selectedUnits = new ArrayList<>();
        for (long id : selectedUnitIds) {
            selectedUnits.add(myUnits.get(id));
        }
        return selectedUnits;
    }

    public void clearSelectedUnitIds() {
        for (long id : selectedUnitIds) {
            myUnits.get(id).setSelected(false);
        }
        this.selectedUnitIds.clear();
    }

    public void addSelectionId(long id) {
        PlayerUnit unit = myUnits.get(id);
        if (unit.getState() != EntityState.DEAD) {
            myUnits.get(id).setSelected(true);
            this.selectedUnitIds.add(id);
        }
    }

    public void setSelection(Position clickLocation) {
        clearSelectedUnitIds();
        for (PlayerUnit unit : myUnits.values()) {
            if (Position.distance(unit.getPosition(), clickLocation) / Constants.ENTITY_WIDTH <= 1 && unit.getState() != EntityState.DEAD) {
                unit.setSelected(true);
                addSelectionId(unit.getId());
                return;
            }
        }
    }

    public ArrayList<Long> getSelectedUnitIds() {
        return selectedUnitIds;
    }

    public void stopSelectedEntities() {
        for (PlayerUnit unit : getSelectedUnits()) {
            unit.setDestination(unit.getPosition());
        }
    }

    public void setSelectedUnits(Rectangle area) {
        ArrayList<Long> hitEntities = new ArrayList<>();
        for (PlayerUnit unit : myUnits.values()) {
            unit.setSelected(false);
            if (unit.getCollisionBox().getCollisionShape().intersects(area) && unit.getState() != EntityState.DEAD) {
                hitEntities.add(unit.getId());
                unit.setSelected(true);
            }

        }
        if (!hitEntities.isEmpty()) {
            selectedUnitIds.clear();
            selectedUnitIds = hitEntities;
        }
    }

    private void checkCollectables(PlayerUnit playerUnit) {
        for (Collectable collected : playerUnit.getCollected()) {

            if (playerUnit.getState() == EntityState.DEAD) {
                continue;
            }

            if (collected instanceof Chest chest) {
                if (collected.getReward().getType().equals(RewardType.GOLD)) {
                    GameEvents.getInstance().addEvent(new GameEvent(collected.getId(), collected.getReward().toString(), EventType.GOLD_PICK_UP, playerUnit.getId()));
                } else if (collected.getReward().getType().equals(RewardType.POINT)) {
                    GameEvents.getInstance().addEvent(new GameEvent(collected.getId(), collected.getReward().toString(), EventType.POINT_PICK_UP, playerUnit.getId()));
                } else {
                    //Chest contains a BUFF, parse it
                    GameEvents.getInstance().addEvent(new GameEvent(collected.getId(), collected.getReward().toString(), EventType.BUFF_PICK_UP, playerUnit.getId()));
                    upgradeUnit(playerUnit.getId(), Reward.parseReward(collected.getReward().toString()), Reward.parseQuantity(collected.getReward().toString()));
                    System.out.println("BUFF -> " + Reward.parseReward(collected.getReward().toString()) + " Quantity -> " + Reward.parseQuantity(collected.getReward().toString()));
                }
            } else if (collected instanceof Gold) {
                GameEvents.getInstance().addEvent(new GameEvent(collected.getId(), collected.getReward().toString(), EventType.GOLD_PICK_UP, playerUnit.getId()));
            } else if (collected instanceof Flag) {
                GameEvents.getInstance().addEvent(new GameEvent(collected.getId(),
                        "Flag picked up, hurry up and get it to the base",
                        EventType.FLAG_PICK_UP, playerUnit.getId()));
                playerUnit.setHasFlag(true);
            }
            pickedUpCollectableIds.add(collected.getId());
        }
        playerUnit.getCollected().clear();
    }

    public void addCollectable(Collectable collectable) {
        collectables.put(collectable.getId(), collectable);
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

    public HashMap<Long, Environment> getEnvironments() {
        return environments;
    }

    public StartGameRequestDTO initializeWorld(Map map, ArrayList<User> users, ModelManager modelManager) {
        StartGameRequestDTO startGameRequest = new StartGameRequestDTO(new ArrayList<>(), new ArrayList<>(), new ArrayList<>());

        Flag flag = new Flag(map.getFlagPosition(), map);
        flag.setReward(new Reward(1, RewardType.FLAG));

        ArrayList<Position> basePositions = map.generateSpawnPoints(users.size());

        collectables = map.generateCollectables();
        collectables.put(flag.getId(), flag);

        for (Collectable collectable : collectables.values()) {
            startGameRequest.addCollectable(
                    new CollectableDTO(collectable.getId(),
                            collectable.getPosition(),
                            collectable.getReward().getType().label,
                            collectable.getReward()));
        }

        for (User user : users) {
            long userId = user.id;
            Position basePos = basePositions.get(0);
            Position goldMinePos = map.generateGoldMinePosition(basePos);
            long goldMineId = spawnGoldMine(map, goldMinePos);
            //Spawning base
            startGameRequest.environments().add(new EnvironmentDTO(Util.generateId(), userId, basePos, DtoType.BASE.label, 0));

            //Spawning goldmine
            startGameRequest.environments().add(new EnvironmentDTO(goldMineId, userId, goldMinePos, DtoType.GOLDMINE.label, 0));
            long baseId = spawnBase(map, basePos);


            //Spawning 3 entities
            for (int i = 0; i < 3; i++) {
                Position offsetPosition;

                do {
                    offsetPosition = new Position((basePos.getX() + Utils.getRandomInt(-30, 30)),
                            (basePos.getY() + Utils.getRandomInt(-30, 30)));
                } while (!modelManager.isWalkable(offsetPosition));

                EntitySkeletonDTO entitySkeletonDTO = new EntitySkeletonDTO(Utils.generateId(), userId, UnitType.GUNNER.label, offsetPosition);
                spawnUnit(map, entitySkeletonDTO.id(), entitySkeletonDTO.unitType(), offsetPosition, userId, baseId);

                startGameRequest.entitySkeletons().add(entitySkeletonDTO);
            }

            if (!basePositions.isEmpty()) {
                basePositions.remove(0);
            }
        }

        return startGameRequest;
    }

    public void populateWorld(StartGameRequestDTO request, Map map) {
        long baseId = 1L;
        for (EnvironmentDTO env : request.environments()) {
            DtoType type = DtoType.fromLabel(env.type());
            switch (type) {
                case BASE:
                    if (env.userId() == user.id) {
                        baseId = spawnBase(map, env.position());
                    } else {
                        spawnBase(map, env.position());
                    }
                    break;
                case GOLDMINE:
                    spawnGoldMine(map, env.position());
                    break;
                default:
                    System.out.println("Unexpected type on collectable");
                    break;
            }
        }
        for (CollectableDTO collectable : request.collectables()) {
            DtoType type = DtoType.fromLabel(collectable.type());
            switch (type) {
                case GOLD:
                    spawnGold(map, collectable.position());
                    break;
                case CHEST:
                    spawnChest(map, collectable.position(), collectable.reward());
                    break;
                case FLAG:
                    spawnFlag(map, collectable.position());
                default:
                    System.out.println("Unexpected type on collectable");
                    break;
            }
        }

        for (EntitySkeletonDTO unit : request.entitySkeletons()) {
            spawnUnit(map, unit.id(), unit.unitType(), unit.position(), unit.userId(), baseId);
        }
    }

    public ArrayList<Long> getCollectedIds() {
        return pickedUpCollectableIds;
    }

    public ArrayList<EntitySkeletonDTO> getAllEntitySkeletons() {
        ArrayList<EntitySkeletonDTO> skeletons = getMyUnitsToEntitySkeletons();
        for (PlayerUnit unit : enemyUnits.values()) {
            EntitySkeletonDTO skeletonDTO = new EntitySkeletonDTO(unit.getId(), unit.getId(), unit.getEntityType(), unit.getPosition());
            skeletons.add(skeletonDTO);
        }
        return skeletons;
    }

    public ArrayList<EnvironmentDTO> getAllEnvironmentsDTO() {
        ArrayList<EnvironmentDTO> environmentDtos = new ArrayList<>();
        for(Environment env : this.environments.values()){
            if(env instanceof GoldMine goldmine){
                environmentDtos.add(new EnvironmentDTO(env.getId(), -1, env.getPosition(), DtoType.BASE.label, goldmine.getRemainingResource()));
            }
        }
        return environmentDtos;
    }

    public void updateUnitPositions(ArrayList<EntitySkeletonDTO> skeletons) {
        for (EntitySkeletonDTO unit : skeletons) {
            if (enemyUnits.containsKey(unit.id())) {
                enemyUnits.get(unit.id()).setPosition(unit.position());
            }
        }
    }

    public void removeCollectables(Map map, ArrayList<Long> collectableIds){
        for(Long collectableId : collectableIds){
            this.collectables.get(collectableId).destroy(map);
            this.collectables.remove(collectableId);
        }
    }

    public void updateEnvironments(ArrayList<EnvironmentDTO> envDtos){
        for(EnvironmentDTO env : envDtos){
            if(this.environments.containsKey(env.id())){
                if(DtoType.fromLabel(env.type()) == DtoType.GOLDMINE){
                    GoldMine mine = (GoldMine)this.environments.get(env.id());
                    if(mine.getRemainingResource() > env.remainingResource()){
                        mine.setResource(env.remainingResource());
                    }
                }
            }
        }
    }

    public ArrayList<EntitySkeletonDTO> getMyUnitsToEntitySkeletons() {
        ArrayList<EntitySkeletonDTO> skeletons = new ArrayList<>();
        for (PlayerUnit unit : myUnits.values()) {
            EntitySkeletonDTO skeletonDTO = new EntitySkeletonDTO(unit.getId(), user.id, unit.getEntityType(), unit.getPosition());
            skeletons.add(skeletonDTO);
        }
        return skeletons;
    }

    //Return the base ID
    private long spawnBase(Map map, Position position) {
        Base base = new Base(position, map);
        addEnvironment(base);
        return base.getId();
    }

    public long spawnFlag(Map map, Position position) {
        Flag flag = new Flag(position, map);
        flag.setReward(new Reward(1, RewardType.FLAG));
        addCollectable(flag);
        return flag.getId();
    }

    private long spawnGoldMine(Map map, Position position) {
        GoldMine goldMine = new GoldMine(position, map, 100);
        addEnvironment(goldMine);
        return goldMine.getId();
    }

    private void spawnGold(Map map, Position position) {
        Gold coin = new Gold(position, map);
        coin.setReward(new Reward(10, RewardType.GOLD));
        addCollectable(coin);
    }

    private void spawnChest(Map map, Position position, Reward reward) {
        Chest chest = new Chest(position, map);
        chest.setReward(reward);
        addCollectable(chest);
    }

    private void spawnUnit(Map map, long unitId, String name, Position position, long unitOwnerId, long baseId) {
        PlayerUnit unit = new PlayerUnit(unitId, name, position, map);
        unit.setUserId(unitOwnerId);
        unit.setBase(baseId);

        if (this.user.id == unitOwnerId) {
            myUnits.put(unit.getId(), unit);
        } else {
            enemyUnits.put(unit.getId(), unit);
        }
    }

    public void updateEnemyUnits(ArrayList<CompleteUnitInfoDTO> updates) {
        for (CompleteUnitInfoDTO update : updates) {
            PlayerUnit enemyUnit = this.enemyUnits.get(update.unitId());
            long targetUnit = update.targetUnitId();
            if (targetUnit != -1) {
                enemyUnit.setAttackTarget(
                        myUnits.containsKey(targetUnit) ? myUnits.get(targetUnit) : enemyUnits.get(targetUnit)
                );
            } else {
                enemyUnit.setDestination(update.destination());
            }

            enemyUnit.setPosition(update.position());
            enemyUnit.setCurrentHp(update.currentHp());
            enemyUnit.setMaxHp(update.maxHp());
            enemyUnit.setSpeedBuff(update.speed());
        }
    }

    public ArrayList<Long> getMyUnitIds() {
        return new ArrayList<>(myUnits.values().stream().map(GameObject::getId).toList());
    }

    public void removeEnemyUnits(ArrayList<Long> unitIds) {
        for (Long id : unitIds) {
            enemyUnits.remove(id);
        }
    }

    public void addEnvironment(Environment environment) {
        environments.put(environment.getId(), environment);
    }

    public void upgradeUnit(long unitId, String type, int amount) {
        PlayerUnit unit;

        if(myUnits.containsKey(unitId)){
            unit = myUnits.get(unitId);
        }else{
            unit = enemyUnits.get(unitId);
        }
        RewardType upgrade = RewardType.fromLabel(type);
        switch (upgrade) {
            case MAX_HP -> unit.setMaxHp(unit.getMaxHp() + amount);
            case ATTACK_DMG -> unit.setAttackBuff(unit.getAttackBuff() + amount);
            case MOVEMENT_SPEED -> unit.setSpeedBuff(unit.getSpeedBuff() + amount);
            default -> System.out.println("ERROR: Unknown upgrade type - " + type);
        }
    }

}
