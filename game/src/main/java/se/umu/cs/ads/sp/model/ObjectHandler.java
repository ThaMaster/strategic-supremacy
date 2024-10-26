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
import se.umu.cs.ads.sp.util.Constants;
import se.umu.cs.ads.sp.util.Position;
import se.umu.cs.ads.sp.util.UtilModel;
import se.umu.cs.ads.sp.util.enums.*;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;

public class ObjectHandler {
    private final HashMap<Long, PlayerUnit> myUnits = new HashMap<>();
    private final HashMap<Long, PlayerUnit> enemyUnits = new HashMap<>();
    private final HashMap<Long, Environment> environments = new HashMap<>();
    private HashMap<Long, Collectable> collectables = new HashMap<>();

    private final ArrayList<Long> pickedUpCollectableIds = new ArrayList<>();
    private final ArrayList<Long> spawnedCollectables = new ArrayList<>();

    private final User user;

    private ArrayList<Long> selectedUnitIds = new ArrayList<>();

    private boolean myUnitsDead = false;

    public ObjectHandler(User user) {
        this.user = user;
    }

    public void update(Map map) {
        if (!myUnitsDead) {
            for (PlayerUnit unit : myUnits.values()) {
                unit.update();
                if (unit.getState() == EntityState.DEAD && unit.hasFlag()) {
                    spawnFlag(map, unit.getPosition(), unit.getFlagId());
                    spawnedCollectables.add(unit.getFlagId());
                    unit.setHasFlag(false, null);
                }
                checkCollectables(unit);
            }
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
        if (unit != null && unit.getState() != EntityState.DEAD) {
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
            if (collected instanceof Chest) {
                if (collected.getReward().getType().equals(RewardType.GOLD)) {
                    GameEvents.getInstance().addEvent(new GameEvent(collected.getId(), collected.getReward().toString(), EventType.GOLD_PICK_UP, playerUnit.getId()));
                } else if (collected.getReward().getType().equals(RewardType.POINT)) {
                    GameEvents.getInstance().addEvent(new GameEvent(collected.getId(), collected.getReward().toString(), EventType.POINT_PICK_UP, playerUnit.getId()));
                } else {
                    GameEvents.getInstance().addEvent(new GameEvent(collected.getId(), collected.getReward().toString(), EventType.BUFF_PICK_UP, playerUnit.getId()));
                    upgradeUnit(playerUnit.getId(), Reward.parseReward(collected.getReward().toString()), Reward.parseQuantity(collected.getReward().toString()));
                }
            } else if (collected instanceof Gold) {
                GameEvents.getInstance().addEvent(new GameEvent(collected.getId(), collected.getReward().toString(), EventType.GOLD_PICK_UP, playerUnit.getId()));
                this.collectables.remove(collected.getId());
            } else if (collected instanceof Flag) {
                GameEvents.getInstance().addEvent(new GameEvent(collected.getId(),
                        "Flag picked up, hurry up and get it to the base",
                        EventType.FLAG_PICK_UP, playerUnit.getId()));
                playerUnit.setHasFlag(true, collected.getId());
                this.collectables.remove(collected.getId());
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

    public ArrayList<Collectable> getCollectablesArray() {
        return new ArrayList<>(collectables.values());
    }

    public HashMap<Long, Environment> getEnvironments() {
        return environments;
    }

    public StartGameRequestDTO generateNextRound(Map map, ArrayList<User> users) {
        myUnitsDead = false;
        this.collectables.clear();
        this.environments.clear();

        StartGameRequestDTO startGameRequest = new StartGameRequestDTO(new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
        ArrayList<Position> basePositions = map.generateSpawnPoints(users.size());
        Flag flag = new Flag(map.getFlagPosition(basePositions), map);
        flag.setReward(new Reward(1, RewardType.FLAG));
        collectables = map.generateCollectables();
        collectables.put(flag.getId(), flag);

        for (Collectable collectable : collectables.values()) {
            startGameRequest.addCollectable(
                    new CollectableDTO(collectable.getId(),
                            collectable.getPosition(),
                            collectable.getType().label,
                            collectable.getReward()));
        }

        for (int uIndex = 0; uIndex < users.size(); uIndex++) {
            long userId = users.get(uIndex).id;

            //Spawning base
            Position basePos = basePositions.get(uIndex);
            startGameRequest.environments().add(new EnvironmentDTO(Util.generateId(), userId, basePos, EnvironmentType.BASE.label, 0));
            long baseId = spawnBase(map, basePos, null);

            //Spawning goldmine
            Position goldMinePos = map.generateGoldMinePosition(basePos);
            int goldReserve = 100;
            long goldMineId = spawnGoldMine(map, goldMinePos, goldReserve, null);
            startGameRequest.environments().add(new EnvironmentDTO(goldMineId, userId, goldMinePos, EnvironmentType.GOLDMINE.label, goldReserve));

            // Spawning 3 entities
            ArrayList<EntitySkeletonDTO> skeletons = new ArrayList<>();
            ArrayList<PlayerUnit> usersUnits = getPlayerUnits(userId);
            for (PlayerUnit unit : usersUnits) {
                Position offsetPosition;
                do {
                    offsetPosition = new Position((basePos.getX() + UtilModel.getRandomInt(-30, 30)),
                            (basePos.getY() + UtilModel.getRandomInt(-30, 30)));
                } while (!map.isWalkable(offsetPosition));

                unit.reset(offsetPosition);
                unit.setBase(baseId);
                skeletons.add(new EntitySkeletonDTO(unit.getId(), unit.getEntityType(), unit.getPosition()));
            }
            startGameRequest.userSkeletons().add(new UserSkeletonsDTO(userId, skeletons));
        }

        return startGameRequest;
    }

    public StartGameRequestDTO initializeWorld(Map map, ArrayList<User> users) {
        myUnitsDead = false;
        StartGameRequestDTO startGameRequest = new StartGameRequestDTO(new ArrayList<>(), new ArrayList<>(), new ArrayList<>());

        ArrayList<Position> basePositions = map.generateSpawnPoints(users.size());
        Flag flag = new Flag(map.getFlagPosition(basePositions), map);
        flag.setReward(new Reward(1, RewardType.FLAG));

        collectables = map.generateCollectables();
        collectables.put(flag.getId(), flag);

        for (Collectable collectable : collectables.values()) {
            startGameRequest.addCollectable(
                    new CollectableDTO(collectable.getId(),
                            collectable.getPosition(),
                            collectable.getType().label,
                            collectable.getReward()));
        }

        for (int uIndex = 0; uIndex < users.size(); uIndex++) {
            long userId = users.get(uIndex).id;

            //Spawning base
            Position basePos = basePositions.get(uIndex);
            startGameRequest.environments().add(new EnvironmentDTO(Util.generateId(), userId, basePos, EnvironmentType.BASE.label, 0));
            long baseId = spawnBase(map, basePos, null);

            //Spawning goldmine
            Position goldMinePos = map.generateGoldMinePosition(basePos);
            int goldReserve = 100;
            long goldMineId = spawnGoldMine(map, goldMinePos, goldReserve, null);
            startGameRequest.environments().add(new EnvironmentDTO(goldMineId, userId, goldMinePos, EnvironmentType.GOLDMINE.label, goldReserve));

            // Spawning 3 entities
            ArrayList<EntitySkeletonDTO> skeletons = new ArrayList<>();
            for (int i = 0; i < 3; i++) {
                Position offsetPosition;
                do {
                    offsetPosition = new Position((basePos.getX() + UtilModel.getRandomInt(-30, 30)),
                            (basePos.getY() + UtilModel.getRandomInt(-30, 30)));
                } while (!map.isWalkable(offsetPosition));

                EntitySkeletonDTO entitySkeletonDTO = new EntitySkeletonDTO(UtilModel.generateId(), UnitType.GUNNER.label, offsetPosition);
                spawnUnit(map, entitySkeletonDTO.id(), entitySkeletonDTO.unitType(), offsetPosition, userId, baseId);
                skeletons.add(entitySkeletonDTO);
            }
            startGameRequest.userSkeletons().add(new UserSkeletonsDTO(userId, skeletons));
        }

        return startGameRequest;
    }

    public void populateWorld(StartGameRequestDTO request, Map map, boolean nextRound) {
        myUnitsDead = false;
        this.collectables.clear();
        this.environments.clear();
        long baseId = 1L;
        for (EnvironmentDTO env : request.environments()) {
            EnvironmentType type = EnvironmentType.fromLabel(env.type());
            switch (type) {
                case BASE:
                    if (env.userId() == user.id) {
                        baseId = spawnBase(map, env.position(), env.id());
                    } else {
                        spawnBase(map, env.position(), env.id());
                    }
                    break;
                case GOLDMINE:
                    spawnGoldMine(map, env.position(), env.remainingResource(), env.id());
                    break;
                default:
                    break;
            }
        }

        for (CollectableDTO collectable : request.collectables()) {
            CollectableType type = CollectableType.fromLabel(collectable.type());
            switch (type) {
                case GOLD:
                    spawnGold(map, collectable.position(), collectable.id());
                    break;
                case CHEST:
                    spawnChest(map, collectable.position(), collectable.reward(), collectable.id());
                    break;
                case FLAG:
                    spawnFlag(map, collectable.position(), collectable.id());
                    break;
                default:
                    break;
            }
        }

        for (UserSkeletonsDTO skeletons : request.userSkeletons()) {
            for (EntitySkeletonDTO skeleton : skeletons.entities()) {
                if (nextRound) {
                    PlayerUnit unit = myUnits.containsKey(skeleton.id()) ? myUnits.get(skeleton.id()) : enemyUnits.get(skeleton.id());
                    unit.reset(skeleton.position());
                    unit.setBase(baseId);
                } else {
                    spawnUnit(map, skeleton.id(), skeleton.unitType(), skeleton.position(), skeletons.userId(), baseId);
                }
            }

        }
    }

    public ArrayList<Long> getCollectedIds() {
        return pickedUpCollectableIds;
    }

    public ArrayList<CollectableDTO> getSpawnedCollectables() {
        ArrayList<CollectableDTO> spawnedCollectables = new ArrayList<>();
        for (Collectable collectable : collectables.values()) {
            if (pickedUpCollectableIds.contains(collectable.getId())) {
                spawnedCollectables.add(new CollectableDTO(
                        collectable.getId(),
                        collectable.getPosition(),
                        collectable.getType().label,
                        collectable.getReward()));
            }
        }
        return spawnedCollectables;
    }

    public ArrayList<UserSkeletonsDTO> getAllEntitySkeletons() {
        HashMap<Long, UserSkeletonsDTO> skeletons = new HashMap<>();
        ArrayList<PlayerUnit> allUnits = new ArrayList<>();
        allUnits.addAll(myUnits.values());
        allUnits.addAll(enemyUnits.values());
        for (PlayerUnit unit : allUnits) {
            EntitySkeletonDTO skeletonDTO = new EntitySkeletonDTO(unit.getId(), unit.getEntityType(), unit.getPosition());
            if (skeletons.containsKey(unit.getUserId())) {
                skeletons.get(unit.getUserId()).addSkeleton(skeletonDTO);
            } else {
                skeletons.put(unit.getUserId(), new UserSkeletonsDTO(unit.getUserId(), new ArrayList<>()));
                skeletons.get(unit.getUserId()).addSkeleton(skeletonDTO);
            }
        }
        return new ArrayList<>(skeletons.values());
    }

    public ArrayList<EnvironmentDTO> getAllEnvironmentsDTO() {
        ArrayList<EnvironmentDTO> environmentDTOs = new ArrayList<>();
        for (Environment env : this.environments.values()) {
            if (env instanceof GoldMine goldmine) {
                environmentDTOs.add(new EnvironmentDTO(
                        env.getId(),
                        -1,
                        env.getPosition(),
                        EnvironmentType.GOLDMINE.label,
                        goldmine.getRemainingResource()));
            }
        }
        return environmentDTOs;
    }

    public void updateUnitPositions(ArrayList<UserSkeletonsDTO> skeletons) {
        for (UserSkeletonsDTO units : skeletons) {
            for (EntitySkeletonDTO unit : units.entities()) {
                if (enemyUnits.containsKey(unit.id())) {
                    enemyUnits.get(unit.id()).setPosition(unit.position());
                }
            }
        }
    }

    public void updateUnitPositionsInL2(ArrayList<UserSkeletonsDTO> skeletons) {
        for (UserSkeletonsDTO units : skeletons) {
            for (EntitySkeletonDTO unit : units.entities()) {
                if (enemyUnits.containsKey(unit.id())) {
                    enemyUnits.get(unit.id()).setPosition(unit.position());
                    enemyUnits.get(unit.id()).setDestination(unit.position());
                }
            }
        }
    }

    public void spawnCollectables(Map map, ArrayList<CollectableDTO> collectableDTOS) {
        for (CollectableDTO collectable : collectableDTOS) {
            if (CollectableType.fromLabel(collectable.type()) == CollectableType.FLAG) {
                spawnFlag(map, collectable.position(), collectable.id());
            }
        }
    }

    public void removeCollectables(Map map, ArrayList<Long> collectableIds) {
        for (Long collectableId : collectableIds) {
            if (this.collectables.containsKey(collectableId)) {
                Collectable currentColl = collectables.get(collectableId);
                currentColl.pickUp(map);
                if (currentColl.getType() != CollectableType.CHEST) {
                    this.collectables.remove(collectableId);
                }
                GameEvents.getInstance().addEvent(new GameEvent(currentColl.getId(), currentColl.getReward().toString(), EventType.ENEMY_PICK_UP, -1));
            }
        }
    }

    public void updateEnvironments(ArrayList<EnvironmentDTO> envDTOs) {
        for (EnvironmentDTO env : envDTOs) {
            if (this.environments.containsKey(env.id())) {
                Environment envModel = environments.get(env.id());
                if (envModel instanceof GoldMine goldMine) {
                    if (goldMine.getRemainingResource() > env.remainingResource()) {
                        goldMine.setResource(env.remainingResource());
                        if (!goldMine.hasResourceLeft()) {
                            GameEvents.getInstance().moveToHistory(new GameEvent(goldMine.getId(), "Mine depleted from objecthandler", EventType.MINE_DEPLETED, -1));
                        }
                    }
                }
            }
        }
    }

    public ArrayList<EntitySkeletonDTO> getMyUnitsToEntitySkeletons() {
        ArrayList<EntitySkeletonDTO> skeletons = new ArrayList<>();
        for (PlayerUnit unit : myUnits.values()) {
            skeletons.add(new EntitySkeletonDTO(unit.getId(), unit.getEntityType(), unit.getPosition()));
        }
        return skeletons;
    }

    //Return the base ID
    private long spawnBase(Map map, Position position, Long id) {
        Base base;
        if (id != null) {
            base = new Base(position, map, id);
        } else {
            base = new Base(position, map);
        }

        addEnvironment(base);
        return base.getId();
    }

    public long spawnFlag(Map map, Position position, Long id) {
        Flag flag;
        if (id != null) {
            flag = new Flag(position, map, id);
        } else {
            flag = new Flag(position, map);
        }

        flag.setReward(new Reward(1, RewardType.FLAG));
        addCollectable(flag);
        return flag.getId();
    }

    private long spawnGoldMine(Map map, Position position, int goldReserve, Long id) {
        GoldMine goldMine;
        if (id != null) {
            goldMine = new GoldMine(position, map, goldReserve, id);
        } else {
            goldMine = new GoldMine(position, map, goldReserve);
        }

        addEnvironment(goldMine);
        return goldMine.getId();
    }

    private void spawnGold(Map map, Position position, Long id) {
        Gold coin;
        if (id != null) {
            coin = new Gold(position, map, id);
        } else {
            coin = new Gold(position, map);
        }
        coin.setReward(new Reward(10, RewardType.GOLD));
        addCollectable(coin);
    }

    private void spawnChest(Map map, Position position, Reward reward, Long id) {
        Chest chest;
        if (id != null) {
            chest = new Chest(position, map, id);
        } else {
            chest = new Chest(position, map);
        }
        chest.setReward(reward);
        addCollectable(chest);
    }

    private void spawnUnit(Map map, long unitId, String name, Position position, long unitOwnerId, long baseId) {
        PlayerUnit unit = new PlayerUnit(unitId, name, position, map);
        unit.setUserId(unitOwnerId);
        unit.setBase(baseId);

        if (this.user.id == unitOwnerId) {
            myUnitsDead = false;
            myUnits.put(unit.getId(), unit);
        } else {
            enemyUnits.put(unit.getId(), unit);
        }
    }

    public void updateEnemyUnits(ArrayList<UnitDTO> updates) {
        for (UnitDTO update : updates) {
            PlayerUnit enemyUnit = this.enemyUnits.get(update.unitId());
            enemyUnit.setPosition(update.position());

            long targetUnit = update.targetUnitId();
            if (targetUnit != -1) {
                enemyUnit.setAttackTarget(
                        myUnits.containsKey(targetUnit) ? myUnits.get(targetUnit) : enemyUnits.get(targetUnit)
                );
            } else {
                enemyUnit.setAttackTarget(null);
                enemyUnit.setDestination(update.destination());
            }
            if (update.flagId() != -1) {
                enemyUnit.setHasFlag(true, update.flagId());
            } else {
                enemyUnit.setHasFlag(false, null);
            }
            enemyUnit.setMaxHp(update.maxHp());
            enemyUnit.setCurrentHp(update.currentHp());
            enemyUnit.setSpeedBuff(update.speedBuff());
            enemyUnit.setAttackBuff(update.attackBuff());
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

        if (myUnits.containsKey(unitId)) {
            unit = myUnits.get(unitId);
        } else {
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

    public void clearGameObjects() {
        this.myUnits.clear();
        this.enemyUnits.clear();
        this.collectables.clear();
        this.environments.clear();
    }

    public boolean checkDefeated() {
        for (PlayerUnit unit : myUnits.values()) {
            if (unit.getState() != EntityState.DEAD) {
                return false;
            }
        }
        myUnitsDead = true;
        return true;
    }

    public boolean isMyUnitsDead() {
        return myUnitsDead;
    }

    public ArrayList<PlayerUnit> getPlayerUnits(long userId) {
        ArrayList<PlayerUnit> units;
        if (userId == user.id) {
            units = new ArrayList<>(myUnits.values().stream().filter(unit -> unit.getUserId() == userId).toList());
        } else {
            units = new ArrayList<>(enemyUnits.values().stream().filter(unit -> unit.getUserId() == userId).toList());
        }
        return units;
    }
}
