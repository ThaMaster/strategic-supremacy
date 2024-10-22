package se.umu.cs.ads.sp.model;

import org.apache.commons.lang3.tuple.Pair;
import se.umu.cs.ads.ns.app.User;
import se.umu.cs.ads.sp.events.GameEvent;
import se.umu.cs.ads.sp.events.GameEvents;
import se.umu.cs.ads.sp.model.communication.ComHandler;
import se.umu.cs.ads.sp.model.communication.dto.*;
import se.umu.cs.ads.sp.model.lobby.LobbyHandler;
import se.umu.cs.ads.sp.model.map.FowModel;
import se.umu.cs.ads.sp.model.map.Map;
import se.umu.cs.ads.sp.model.objects.GameObject;
import se.umu.cs.ads.sp.model.objects.collectables.Reward;
import se.umu.cs.ads.sp.model.objects.entities.units.PlayerUnit;
import se.umu.cs.ads.sp.utils.Constants;
import se.umu.cs.ads.sp.utils.Position;
import se.umu.cs.ads.sp.utils.Utils;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Timer;
import java.util.TimerTask;

public class ModelManager {

    private final Map map;
    private final ObjectHandler objectHandler;
    private final LobbyHandler lobbyHandler;
    private final ComHandler comHandler;
    private final User player;
    private FowModel fow;

    private Timer l3Timer;
    private Timer l2Timer;

    private Timer gameTimer;
    private long remainingTime = 150;
    private boolean started = false;

    private long currentScoreHolderId;

    private int currentGold = 180;
    private int currentPoints;

    public ModelManager(User player) {
        map = new Map();
        this.player = player;
        lobbyHandler = new LobbyHandler(this);
        objectHandler = new ObjectHandler(player);
        comHandler = new ComHandler(player.port, this);

        Runtime.getRuntime().addShutdownHook(new Thread(this::leaveOngoingGame));
    }

    public User getPlayer() {
        return this.player;
    }

    public ComHandler getComHandler() {
        return comHandler;
    }

    public void update() {
        // Update all entities in game, including my units.
        objectHandler.update(map);
        fow.updateUnitPositions(new ArrayList<>(objectHandler.getMyUnits().values()));
        collectEvents();
    }

    public ObjectHandler getObjectHandler() {
        return objectHandler;
    }

    public boolean setEntityDestination(Position newPosition) {
        long targetId = checkEntityHit(newPosition);
        if (targetId != -1 && fow.isInFow(newPosition)) {
            for (PlayerUnit unit : objectHandler.getSelectedUnits()) {
                unit.setAttackTarget(objectHandler.getEnemyUnits().get(targetId));
            }
            sendL1Update(constructL1Message(targetId));
            return true;
        }

        if (isWalkable(newPosition)) {
            // Slightly randomise the units, so they do not get the EXACT same position.
            Position offsetPosition = newPosition;
            for (PlayerUnit unit : objectHandler.getSelectedUnits()) {
                unit.setDestination(offsetPosition);
                do {
                    offsetPosition = new Position(newPosition.getX() + Utils.getRandomInt(-15, 15), newPosition.getY() + Utils.getRandomInt(-15, 15));
                } while (!isWalkable(offsetPosition));
            }

            sendL1Update(constructL1Message(-1));
            return true;
        }
        return false;
    }

    public void setSelection(long selectedUnit) {
        objectHandler.clearSelectedUnitIds();
        objectHandler.addSelectionId(selectedUnit);
    }

    public Map getMap() {
        return this.map;
    }

    public boolean isWalkable(Position position) {
        return map.isWalkable(position);
    }

    private long checkEntityHit(Position position) {
        // Make this smarter in some way?
        int col = position.getX() / Constants.TILE_WIDTH;
        int row = position.getY() / Constants.TILE_HEIGHT;

        int colOffset = (position.getX() % Constants.TILE_WIDTH > Constants.TILE_WIDTH / 2) ? 1 : -1;
        int rowOffset = (position.getY() % Constants.TILE_HEIGHT > Constants.TILE_HEIGHT / 2) ? 1 : -1;

        ArrayList<Pair<Integer, Integer>> pairsToCheck = new ArrayList<>();
        if (row >= 0 && col >= 0)
            pairsToCheck.add(Pair.of(row, col));
        if (col >= 0 && row + rowOffset >= 0)
            pairsToCheck.add(Pair.of(row + rowOffset, col));
        if (row >= 0 && col + colOffset >= 0)
            pairsToCheck.add(Pair.of(row, col + colOffset));
        if (row + rowOffset >= 0 && col + colOffset >= 0)
            pairsToCheck.add(Pair.of(row + rowOffset, col + colOffset));

        for (Pair<Integer, Integer> pair : pairsToCheck) {
            for (GameObject object : map.getInhabitants(pair.getLeft(), pair.getRight())) {
                if (hitUnit(position, object)) {
                    return object.getId();
                }
            }
        }
        return -1;
    }

    private boolean hitUnit(Position position, GameObject object) {
        return object instanceof PlayerUnit unit &&
                unit.getCollisionBox().contains(position) &&
                !objectHandler.getMyUnits().containsValue(unit);
    }

    public void loadMap(String mapName) {
        map.loadMap("maps/" + mapName + ".txt");
    }

    public LobbyHandler getLobbyHandler() {
        return this.lobbyHandler;
    }

    public void startGame() {
        System.out.println("[Client] Sending out start request to lobby...");
        StartGameRequestDTO req = objectHandler.initializeWorld(map, lobbyHandler.getLobby().users, this);
        for (User user : lobbyHandler.getLobby().users) {
            if (user.id != player.id) {
                comHandler.sendStartGameRequest(req, user);
            }
        }

        this.fow = new FowModel(new ArrayList<>(objectHandler.getMyUnits().values()));
        currentScoreHolderId = lobbyHandler.getLobby().leader.id;

        started = true;
        startL3Timer(Constants.L3_UPDATE_TIME);
        startL2Timer(Constants.L2_UPDATE_TIME);
        startRoundTimer();
    }

    private void startL3Timer(long updateTime) {
        l3Timer = new Timer();
        l3Timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (comHandler.leaderIsAlive()) {
                    sendL3Update();
                } else if (!iAmLeader()) { //Should not need to check this probably
                    System.out.println("START LEADER ELECTION");
                    lobbyHandler.initiateLeaderElection();
                }
            }
        }, 0, updateTime);
    }

    private void startL2Timer(long updateTime) {
        l2Timer = new Timer();
        l2Timer.schedule(new TimerTask() {
            @Override
            public void run() {
                sendL2Update();
            }
        }, 0, updateTime);

    }

    public L3UpdateDTO constructL3Message(boolean fromLeader) {
        ArrayList<UserSkeletonsDTO> entities;
        int msgCount = lobbyHandler.getMsgCount();
        if (fromLeader) {
            entities = objectHandler.getAllEntitySkeletons();
            msgCount = lobbyHandler.incMsgCount();
        } else {
            entities = new ArrayList<>(Collections.singletonList(new UserSkeletonsDTO(player.id, objectHandler.getMyUnitsToEntitySkeletons())));
        }

        return new L3UpdateDTO(
                msgCount,
                entities,
                objectHandler.getCollectedIds(),
                remainingTime,
                currentScoreHolderId,
                objectHandler.getAllEnvironmentsDTO(),
                Constants.LOW_SEVERITY
        );
    }

    public L2UpdateDTO constructL2Message() {
        return new L2UpdateDTO(
                player.id,
                objectHandler.getMyUnitsToEntitySkeletons(),
                objectHandler.getCollectedIds(),
                objectHandler.getAllEnvironmentsDTO(),
                Constants.LOW_SEVERITY
        );
    }

    public L1UpdateDTO constructL1Message(long targetId) {
        ArrayList<UnitDTO> unitUpdates = new ArrayList<>();
        for (PlayerUnit unit : objectHandler.getSelectedUnits()) {
            unitUpdates.add(new UnitDTO(
                    unit.getId(),
                    targetId,
                    unit.getEntityType(),
                    unit.getPosition(),
                    unit.getDestination(),
                    unit.getMaxHp(),
                    unit.getCurrentHp(),
                    unit.getSpeedBuff(),
                    unit.getAttackBuff())
            );
        }
        return new L1UpdateDTO(
                player.id,
                unitUpdates,
                Constants.LOW_SEVERITY
        );
    }

    private void sendL3Update() {
        comHandler.sendL3Update(constructL3Message(iAmLeader()), iAmLeader());
    }

    private void sendL2Update() {
        comHandler.sendL2Update(constructL2Message());
    }

    private void sendL1Update(L1UpdateDTO update) {
        if (comHandler.getNrL1Clients() > 0) {
            comHandler.sendL1Update(update);
        }
    }

    public void receiveL3Update(L3UpdateDTO update) {

        if (!iAmLeader()) {
            lobbyHandler.updateMsgCount(update.msgCount());
            this.remainingTime = update.remainingTime();
        }
        objectHandler.updateUnitPositions(update.entities());
        objectHandler.removeCollectables(map, update.pickedUpCollectables());
        objectHandler.updateEnvironments(update.environments());
        this.currentScoreHolderId = update.currentScoreLeader();

        updateBoundaries(update.entities());
    }

    public void receiveL2Update(L2UpdateDTO update) {
        objectHandler.updateUnitPositions(
                new ArrayList<>(Collections.singletonList(new UserSkeletonsDTO(update.userId(), update.entities()))));
        objectHandler.removeCollectables(map, update.pickedUpCollectables());
        objectHandler.updateEnvironments(update.environments());
        updateBoundariesFromL2Message(update);
    }

    public void receiveL1Update(L1UpdateDTO update) {
        objectHandler.updateEnemyUnits(update.entities());
        updateBoundariesFromL1Message(update);
    }

    private boolean isInsideLayer(ArrayList<Position> positions, int layerIndex) {
        ArrayList<Rectangle> myBBs = getBBByUnits(new ArrayList<>(objectHandler.getMyUnits().values()));
        ArrayList<Rectangle> externalBBs = getBBByPositions(positions);
        int index = layerIndex - 1;

        // Should incomplete initialize info occur, just return false
        if ((index != 0 && index != 1) || myBBs == null || externalBBs == null) {
            return false;
        }

        // Checks if users bounding box intersects with other users bounding box
        if (myBBs.get(index).intersects(externalBBs.get(index))) {
            // Now check each unit to see if they are inside the bounding box
            for (Position pos : positions) {
                if (myBBs.get(index).contains(new Point(pos.getX(), pos.getY()))) {
                    return true;
                }
            }
        }
        return false;
    }

    // A request has come in to start the game
    public void startGameReq(StartGameRequestDTO request) {
        //Check bounding boxes
        objectHandler.populateWorld(request, map);
        this.fow = new FowModel(new ArrayList<>(objectHandler.getMyUnits().values()));
        started = true;
        l3Timer = new Timer();
        startL3Timer(Constants.L3_UPDATE_TIME / 2);
        startL2Timer(Constants.L2_UPDATE_TIME);
        currentScoreHolderId = lobbyHandler.getLobby().leader.id;
    }

    public void leaveOngoingGame() {
        System.out.println("[Client] Leaving ongoing game...");

        if (l3Timer != null) l3Timer.cancel();
        if (l2Timer != null) l2Timer.cancel();
        if (gameTimer != null) gameTimer.cancel();

        comHandler.removePlayerUnits();
        objectHandler.clearSelectedUnitIds();
        objectHandler.getMyUnits().clear();
        objectHandler.getEnvironments().clear();
        objectHandler.getCollectables().clear();
        lobbyHandler.leaveLobby();
        started = false;
    }

    public UserSkeletonsDTO createMySkeletonList() {
        ArrayList<EntitySkeletonDTO> skeletons = new ArrayList<>(objectHandler.getMyUnits().values().stream()
                .map(
                        playerUnit -> new EntitySkeletonDTO(playerUnit.getId(), playerUnit.getEntityType(), playerUnit.getPosition())
                ).toList());
        return new UserSkeletonsDTO(player.id, skeletons);
    }

    public boolean hasGameStarted() {
        return started;
    }

    private void collectEvents() {
        ArrayList<GameEvent> events = GameEvents.getInstance().getEvents();
        for (GameEvent event : events) {
            switch (event.getType()) {
                case NEW_ROUND:
                    //Create logiko
                    break;
                case LOGG:
                    break;
                case DEATH:
                    PlayerUnit unit;
                    if (objectHandler.getMyUnitIds().contains(event.getId())) {
                        unit = objectHandler.getMyUnits().get(event.getId());
                    } else {
                        unit = objectHandler.getEnemyUnits().get(event.getId());
                    }
                    if (unit.hasFlag()) {
                        objectHandler.spawnFlag(map, unit.getPosition(), unit.getFlagId());
                        unit.setHasFlag(false, null);
                    }
                    break;
                case ATTACK:
                    break;
                case TAKE_DMG:
                    break;
                case FLAG_TO_BASE:
                    if (eventCreatedByMyUnit(event.getEventAuthor())) {
                        this.currentPoints += 10;
                    }
                    break;
                case POINT_PICK_UP:
                    if (eventCreatedByMyUnit(event.getEventAuthor())) {
                        int point = Reward.parseQuantity(event.getEvent());
                        this.currentPoints += point;
                    }
                    break;
                case GOLD_PICK_UP:
                    if (eventCreatedByMyUnit(event.getEventAuthor())) {
                        int gold = Reward.parseQuantity(event.getEvent());
                        this.currentGold += gold;
                    }
                    break;
                default:
                    break;
            }
            GameEvents.getInstance().moveToHistory(event);
        }
        GameEvents.getInstance().clearEvents();
    }

    public long getRoundRemainingTime() {
        return remainingTime;
    }

    private void startRoundTimer() {
        this.gameTimer = new Timer();
        this.gameTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (remainingTime > 0) {
                    remainingTime--;
                } else {
                    gameTimer.cancel();
                }
            }
        }, 0, 1000);
    }

    private boolean eventCreatedByMyUnit(long eventId) {
        return objectHandler.getMyUnitIds().contains(eventId);
    }

    public int getCurrentGold() {
        return currentGold;
    }

    public void setCurrentGold(int newGold) {
        this.currentGold = newGold;
    }

    public int getCurrentPoints() {
        return currentPoints;
    }

    private void updateBoundariesFromL2Message(L2UpdateDTO message) {
        updateBoundaries(new ArrayList<>(Collections.singletonList(new UserSkeletonsDTO(message.userId(), message.entities()))));
    }

    private void updateBoundariesFromL1Message(L1UpdateDTO message) {
        ArrayList<EntitySkeletonDTO> skeletons = new ArrayList<>(message.entities().stream()
                .map(unit -> new EntitySkeletonDTO(message.userId(), unit.unitType(), unit.position()))
                .toList()
        );
        updateBoundaries(new ArrayList<>(Collections.singletonList(new UserSkeletonsDTO(message.userId(), skeletons))));
    }


    private void updateBoundaries(ArrayList<UserSkeletonsDTO> userSkeletons) {
        for (UserSkeletonsDTO skeletons : userSkeletons) {
            // Skip ourselves
            if (skeletons.userId() == player.id) {
                continue;
            }
            ArrayList<Position> skeletonPos = new ArrayList<>(skeletons.entities().stream().map(EntitySkeletonDTO::position).toList());
            // Check l1, then l2, otherwise just move user to l3
            if (isInsideLayer(skeletonPos, 1)) {
                comHandler.moveUserToL1(skeletons.userId());
            } else if (isInsideLayer(skeletonPos, 2)) {
                comHandler.moveUserToL2(skeletons.userId());
            } else {
                comHandler.moveUserToL3(skeletons.userId());
            }
        }
    }

    public ArrayList<Rectangle> getBBByUnits(ArrayList<PlayerUnit> units) {
        return getBBByPositions(new ArrayList<>(units.stream().map(GameObject::getPosition).toList()));
    }

    public ArrayList<Rectangle> getBBByPositions(ArrayList<Position> positions) {
        if (positions.isEmpty()) {
            return null;
        }

        ArrayList<Rectangle> bbs = new ArrayList<>();

        // Initialize min and max coordinates to the first position
        int minX = positions.get(0).getX();
        int minY = positions.get(0).getY();
        int maxX = positions.get(0).getX();
        int maxY = positions.get(0).getY();

        // Iterate over the positions to find the min/max x and y values
        for (Position pos : positions) {
            int x = pos.getX();
            int y = pos.getY();
            if (x < minX) minX = x;
            if (y < minY) minY = y;
            if (x > maxX) maxX = x;
            if (y > maxY) maxY = y;
        }

        // Expand the bounding box by the radius
        Position minPosL1 = new Position(minX - Constants.L1_RADIUS, minY - Constants.L1_RADIUS);
        Position maxPosL1 = new Position(maxX + Constants.L1_RADIUS, maxY + Constants.L1_RADIUS);
        bbs.add(new Rectangle(minPosL1.getX(), minPosL1.getY(), maxPosL1.getX() - minPosL1.getX(), maxPosL1.getY() - minPosL1.getY()));

        Position minPosL2 = new Position(minX - Constants.L2_RADIUS, minY - Constants.L2_RADIUS);
        Position maxPosL2 = new Position(maxX + Constants.L2_RADIUS, maxY + Constants.L2_RADIUS);
        bbs.add(new Rectangle(minPosL2.getX(), minPosL2.getY(), maxPosL2.getX() - minPosL2.getX(), maxPosL2.getY() - minPosL2.getY()));
        return bbs;
    }

    public boolean iAmLeader() {
        return lobbyHandler.getLobby() != null && lobbyHandler.getLobby().leader.id == player.id;
    }
}
