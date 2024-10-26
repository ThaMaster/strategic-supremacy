package se.umu.cs.ads.sp.model;

import org.apache.commons.lang3.tuple.Pair;
import se.umu.cs.ads.ns.app.User;
import se.umu.cs.ads.sp.events.GameEvent;
import se.umu.cs.ads.sp.events.GameEvents;
import se.umu.cs.ads.sp.model.communication.ComHandler;
import se.umu.cs.ads.sp.model.communication.dto.*;
import se.umu.cs.ads.sp.model.lobby.LobbyHandler;
import se.umu.cs.ads.sp.model.lobby.Raft;
import se.umu.cs.ads.sp.model.map.FovModel;
import se.umu.cs.ads.sp.model.map.Map;
import se.umu.cs.ads.sp.model.objects.GameObject;
import se.umu.cs.ads.sp.model.objects.collectables.Reward;
import se.umu.cs.ads.sp.model.objects.entities.units.PlayerUnit;
import se.umu.cs.ads.sp.util.Constants;
import se.umu.cs.ads.sp.util.Position;
import se.umu.cs.ads.sp.util.UtilModel;
import se.umu.cs.ads.sp.util.enums.EntityState;
import se.umu.cs.ads.sp.util.enums.EventType;

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
    private FovModel fov;

    private Timer l3Timer;
    private Timer l2Timer;

    private Timer gameTimer;
    private long remainingTime = Constants.ROUND_TIME;

    private long currentScoreHolderId;
    private int currentScoreHolderScore;

    private int currentGold = 180;
    private int currentPoints;

    private int defeatedPlayers = 0;

    private boolean started = false;
    private boolean finished = false;

    private int currentRound = 0;

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
        fov.updateUnitPositions(new ArrayList<>(objectHandler.getMyUnits().values()));
        if (!objectHandler.isMyUnitsDead() && objectHandler.checkDefeated()) {
            sendDefeatUpdate();
        }
        collectEvents();

    }

    public ObjectHandler getObjectHandler() {
        return objectHandler;
    }

    public boolean setEntityDestination(Position newPosition) {
        long targetId = checkEntityHit(newPosition);
        if (targetId != -1 && fov.isInFov(newPosition)) {
            for (PlayerUnit unit : objectHandler.getSelectedUnits()) {
                unit.setAttackTarget(objectHandler.getEnemyUnits().get(targetId));
            }
            sendL1Update();
            return true;
        }

        if (isWalkable(newPosition)) {
            // Slightly randomise the units, so they do not get the EXACT same position.
            Position offsetPosition = newPosition;
            for (PlayerUnit unit : objectHandler.getSelectedUnits()) {
                unit.setDestination(offsetPosition);
                do {
                    offsetPosition = new Position(newPosition.getX() + UtilModel.getRandomInt(-15, 15), newPosition.getY() + UtilModel.getRandomInt(-15, 15));
                } while (!isWalkable(offsetPosition));
            }

            sendL1Update();
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

    public boolean isInFov(Position position) {
        return fov.isInFov(position);
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
                !objectHandler.getMyUnits().containsValue(unit) &&
                unit.getState() != EntityState.DEAD;
    }

    public void loadMap(String mapName) {
        if (map.getModelMap().isEmpty()) {
            map.loadMap("maps/" + mapName + ".txt");
        }
    }

    public LobbyHandler getLobbyHandler() {
        return this.lobbyHandler;
    }

    /**
     * This function is run only by the leader when it
     * should send out a start game request to all clients
     * in the lobby. It will
     */
    public void startGame() {

        System.out.println("[Client] Sending out start request to lobby...");
        comHandler.markLobbyStarted(lobbyHandler.getLobby().id);
        StartGameRequestDTO req = objectHandler.initializeWorld(map, lobbyHandler.getLobby().users);
        for (User user : lobbyHandler.getLobby().users) {
            if (user.id != player.id) {
                comHandler.sendStartGameRequest(req, user);
            }
        }

        this.fov = new FovModel(new ArrayList<>(objectHandler.getMyUnits().values()));
        currentScoreHolderId = lobbyHandler.getLobby().leader.id;

        startL3Timer(Constants.L3_UPDATE_TIME);
        startL2Timer();
        startRoundTimer();

        started = true;
        finished = false;
    }

    /**
     * This function is run on all users except the leader
     * where they start the game. It will
     * start the L3 timer with a shorter interval than the leader
     * to keep the leaders information more correct.
     *
     * @param request Incoming start game request
     */
    public void startGameReq(StartGameRequestDTO request) {
        objectHandler.populateWorld(request, map, false);

        this.fov = new FovModel(new ArrayList<>(objectHandler.getMyUnits().values()));
        currentScoreHolderId = lobbyHandler.getLobby().leader.id;

        l3Timer = new Timer();
        startL3Timer(Constants.L3_UPDATE_TIME / 2);
        startL2Timer();

        started = true;
        finished = false;
    }

    /**
     * Function for starting the L3 timer and have it send L3 updates at a specified
     * interval. This timer will keep track if the leader has become unresponsive and
     * start leader election.
     *
     * @param updateTime Specified interval (ms) to send L3 updates.
     */
    private void startL3Timer(long updateTime) {
        l3Timer = new Timer();
        l3Timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (comHandler.leaderIsAlive()) {
                    sendL3Update();
                } else if (!iAmLeader() && !lobbyHandler.getRaft().leaderElectionStarted()) {
                    System.out.println("START LEADER ELECTION");
                    lobbyHandler.getRaft().initiateLeaderElection();
                }
            }
        }, 0, updateTime);
    }

    private void startL2Timer() {
        l2Timer = new Timer();
        l2Timer.schedule(new TimerTask() {
            @Override
            public void run() {
                sendL2Update();
            }
        }, 0, Constants.L2_UPDATE_TIME);
    }

    private void sendDefeatUpdate() {
        GameEvents.getInstance().addEvent(new GameEvent(player.id, " You have been defeated!", EventType.PLAYER_DEFEATED, -1));
        comHandler.sendDefeatUpdate(player.id);

        defeatedPlayers++;
        if (iAmLeader()) {
            if (defeatedPlayers == lobbyHandler.getLobby().currentPlayers - 1) {
                this.remainingTime = 10;
            }
        }
    }

    public void receiveDefeatUpdate(long userId) {
        GameEvents.getInstance().addEvent(new GameEvent(userId, lobbyHandler.getPlayer(userId).username + " has been defeated!", EventType.PLAYER_DEFEATED, -1));
        defeatedPlayers++;
        if (iAmLeader()) {
            if (defeatedPlayers == lobbyHandler.getLobby().currentPlayers - 1) {
                this.remainingTime = 10;
            }
        }
    }

    public void sendNextRoundRequest() {
        map.clearMap();
        StartGameRequestDTO request = objectHandler.generateNextRound(map, lobbyHandler.getLobby().users);
        comHandler.sendNextRound(request);
        fov.updateUnitPositions(new ArrayList<>(objectHandler.getMyUnits().values()));
        startRoundTimer();
        GameEvents.getInstance().addEvent(new GameEvent(player.id, "Next Round!", EventType.NEW_ROUND, -1));
        defeatedPlayers = 0;
        currentRound++;
        comHandler.resetClients();
    }

    public void receiveNextRound(StartGameRequestDTO request) {
        map.clearMap();
        objectHandler.populateWorld(request, map, true);
        fov.updateUnitPositions(new ArrayList<>(objectHandler.getMyUnits().values()));
        GameEvents.getInstance().addEvent(new GameEvent(player.id, "Next Round!", EventType.NEW_ROUND, -1));
        defeatedPlayers = 0;
        currentRound++;
        comHandler.resetClients();
    }

    public void sendEndGameMessage() {
        comHandler.sendEndGameMessage(new UserScoreDTO(currentScoreHolderId, currentScoreHolderScore));
        handleEndGame(currentScoreHolderId, currentScoreHolderScore);
    }

    public void receiveEndGameMessage(UserScoreDTO message) {
        handleEndGame(message.userId(), message.score());
    }

    private void handleEndGame(long userId, int score) {
        if (gameTimer != null) gameTimer.cancel();
        if (l3Timer != null) l3Timer.cancel();
        if (l2Timer != null) l2Timer.cancel();

        // TODO: Maybe remove all clients also?
        finished = true;
        GameEvents.getInstance().addEvent(new GameEvent(userId, lobbyHandler.getPlayer(userId).username + " won the game with " + score + " points!", EventType.GAME_FINISHED, -1));
    }

    private void sendL3Update() {
        comHandler.sendL3Update(constructL3Message(iAmLeader()), iAmLeader());
    }

    private void sendL2Update() {
        if (comHandler.getNrL2Clients() > 0) {
            comHandler.sendL2Update(constructL2Message());
        }
    }

    private void sendL1Update() {
        L1UpdateDTO update = constructL1Message();
        if (comHandler.getNrL1Clients() > 0) {
            comHandler.sendL1Update(update);
        }
    }

    /**
     * Function for handling an incoming L3 update. Both the followers
     * and the leader will run this function so it has to handle the
     * content differently depending on which state the leader is in.
     */
    public void receiveL3Update(L3UpdateDTO update) {
        if (iAmLeader()) {
            // Leader gets update from follower
            if (update.scoreInfo().score() > currentScoreHolderScore) {
                currentScoreHolderId = update.scoreInfo().userId();
                currentScoreHolderScore = update.scoreInfo().score();
            }

        } else {
            // Follower gets update from leader
            lobbyHandler.getRaft().updateMsgCount(update.msgCount());
            this.remainingTime = update.remainingTime();
        }
        ArrayList<UserSkeletonsDTO> l3Units = new ArrayList<>(
                update.entities().stream()
                        .filter(e -> !comHandler.l1Clients.containsKey(e.userId()))
                        .toList()
        );
        objectHandler.updateUnitPositions(l3Units);
        objectHandler.removeCollectables(map, update.pickedUpCollectables());
        objectHandler.spawnCollectables(map, update.spawnedCollectables());
        objectHandler.updateEnvironments(update.environments());
        this.currentScoreHolderId = update.currentScoreLeader();

        updateBoundaries(update.entities());
    }

    /**
     * Function for handling an incoming L2 update. Both the followers
     * and the leader will run this function, but they should all handle
     * L2 updates the same.
     */
    public void receiveL2Update(L2UpdateDTO update) {
        objectHandler.updateUnitPositionsInL2(
                new ArrayList<>(Collections.singletonList(new UserSkeletonsDTO(update.userId(), update.entities()))));
        objectHandler.removeCollectables(map, update.pickedUpCollectables());
        objectHandler.spawnCollectables(map, update.spawnedCollectables());
        objectHandler.updateEnvironments(update.environments());
        updateBoundariesFromL2Message(update);
    }

    /**
     * Function for handling an incoming L1 update. Both the followers
     * and the leader will run this function, but they should all handle
     * L1 updates the same.
     *
     * @param update The L1 update message.
     */
    public void receiveL1Update(L1UpdateDTO update) {
        objectHandler.updateEnemyUnits(update.entities());
        updateBoundariesFromL1Message(update);
    }

    /**
     * Function for constructing a new L3 update message. This message contains
     * information about the games state. If the leader constructs this message
     * it will contain a message count variable and all the positions of the
     * players units, including its own. If followers constructs this message
     * it will contain the positions of the follower unit positions.
     *
     * @param fromLeader boolean if the user constructing the message is the leader
     * @return the new L3 update message
     */
    public L3UpdateDTO constructL3Message(boolean fromLeader) {
        ArrayList<UserSkeletonsDTO> entities;
        int msgCount = lobbyHandler.getRaft().getMsgCount();
        if (fromLeader) {
            entities = objectHandler.getAllEntitySkeletons();
            msgCount = lobbyHandler.getRaft().incMsgCount();
        } else {
            entities = new ArrayList<>(Collections.singletonList(new UserSkeletonsDTO(player.id, objectHandler.getMyUnitsToEntitySkeletons())));
        }

        return new L3UpdateDTO(
                msgCount,
                entities,
                objectHandler.getCollectedIds(),
                objectHandler.getSpawnedCollectables(),
                remainingTime,
                currentScoreHolderId,
                new UserScoreDTO(player.id, currentPoints),
                objectHandler.getAllEnvironmentsDTO(),
                Constants.LOW_SEVERITY
        );
    }

    /**
     * Function for constructing a new L2 update message. This
     * update message includes only object information about
     * the game, no game state.
     *
     * @return the new L2 update message
     */
    public L2UpdateDTO constructL2Message() {
        return new L2UpdateDTO(
                player.id,
                objectHandler.getMyUnitsToEntitySkeletons(),
                objectHandler.getCollectedIds(),
                objectHandler.getSpawnedCollectables(),
                objectHandler.getAllEnvironmentsDTO(),
                Constants.LOW_SEVERITY
        );
    }

    /**
     * Function for constructing a new L1 update message. This
     * update message contains complete information of the
     * players units.
     *
     * @return the new L1 update message
     */
    public L1UpdateDTO constructL1Message() {
        ArrayList<UnitDTO> unitUpdates = new ArrayList<>();
        for (PlayerUnit unit : objectHandler.getMyUnits().values()) {
            UnitDTO dto = new UnitDTO(
                    unit.getId(),
                    unit.getTargetId(),
                    unit.getFlagId(),
                    unit.getEntityType(),
                    unit.getPosition(),
                    unit.getDestination(),
                    unit.getMaxHp(),
                    unit.getCurrentHp(),
                    unit.getSpeedBuff(),
                    unit.getAttackBuff()
            );
            unitUpdates.add(dto);
        }
        return new L1UpdateDTO(
                player.id,
                unitUpdates,
                Constants.LOW_SEVERITY
        );
    }

    /**
     * Function for checking if a number of positions are
     * inside the specified layers bounding box. This is to
     * be used to check if clients are to be moved between
     * layers, i.e. L1, L2, or L3.
     *
     * @param positions  The positions to check.
     * @param layerIndex The layer to check, 1 = L1, 2 = L2, otherwise returns false.
     * @return true/false if the positions are inside the specified layer.
     */
    private boolean isInsideLayer(ArrayList<Position> positions, int layerIndex) {
        if (objectHandler.getMyUnits().isEmpty()) {
            return false;
        }

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

    /**
     * Function for leaving the game while the game is running.
     * It will cancel all the started timers and remove all content
     * in the object handler so that it is possible to join/create
     * and start a new game.
     */
    public void leaveOngoingGame() {
        System.out.println("[Client] Leaving ongoing game...");

        if (l3Timer != null) l3Timer.cancel();
        if (l2Timer != null) l2Timer.cancel();
        if (gameTimer != null) gameTimer.cancel();

        comHandler.removePlayerUnits();
        objectHandler.clearSelectedUnitIds();
        objectHandler.clearGameObjects();
        lobbyHandler.leaveLobby();
        started = false;
    }

    public void removePlayer(long userId, ArrayList<Long> unitIds) {
        objectHandler.removeEnemyUnits(unitIds);
        User leavingPlayer = lobbyHandler.getPlayer(userId);
        GameEvents.getInstance().addEvent(new GameEvent(leavingPlayer.id, leavingPlayer.username + " left the game!", EventType.PLAYER_LEFT, userId));
        lobbyHandler.removePlayer(userId);
    }

    public UserSkeletonsDTO createMySkeletonList() {
        ArrayList<EntitySkeletonDTO> skeletons = new ArrayList<>(objectHandler.getMyUnits().values().stream()
                .map(
                        playerUnit -> new EntitySkeletonDTO(playerUnit.getId(), playerUnit.getEntityType(), playerUnit.getPosition())
                ).toList());
        return new UserSkeletonsDTO(player.id, skeletons);
    }

    private void collectEvents() {
        ArrayList<GameEvent> events = GameEvents.getInstance().getEvents();
        for (GameEvent event : events) {
            switch (event.getType()) {
                case NEW_ROUND:
                    //Create logic
                    break;
                case LOGG:
                    break;
                case DEATH:
                    PlayerUnit unit;
                    if (objectHandler.getMyUnits().containsKey(event.getId())) {
                        this.currentPoints++;
                        this.currentGold += 10;
                    }
                    if (objectHandler.getMyUnitIds().contains(event.getEventAuthor())) {
                        unit = objectHandler.getMyUnits().get(event.getEventAuthor());
                    } else {
                        unit = objectHandler.getEnemyUnits().get(event.getEventAuthor());
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

    private void startRoundTimer() {
        this.remainingTime = Constants.ROUND_TIME;
        this.gameTimer = new Timer();
        this.gameTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (remainingTime > 0) {
                    remainingTime--;
                } else {
                    if (iAmLeader()) {
                        if (currentRound == 2) {
                            sendEndGameMessage();
                        } else {
                            sendNextRoundRequest();
                        }
                        gameTimer.cancel();
                    }
                }
            }
        }, 0, 1000);
    }

    public long getRoundRemainingTime() {
        return remainingTime;
    }

    public Raft getRaft() {
        return lobbyHandler.getRaft();
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

    public void setNewLeader(long userId) {
        if (l3Timer != null) l3Timer.cancel();
        l3Timer = new Timer();
        lobbyHandler.setNewLeader(userId);
        long updateTime = Constants.L3_UPDATE_TIME;
        if (!iAmLeader()) {
            updateTime /= 2;
        }
        startL3Timer(updateTime);
    }

    public boolean iAmLeader() {
        return lobbyHandler.getLobby() != null && lobbyHandler.getRaft().iAmLeader();
    }

    public boolean hasGameStarted() {
        return started;
    }

    public boolean hasGameFinished() {
        return finished;
    }
}
