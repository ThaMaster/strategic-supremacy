package se.umu.cs.ads.sp.model;

import org.apache.commons.lang3.tuple.Pair;
import se.umu.cs.ads.ns.app.User;
import se.umu.cs.ads.sp.controller.GameController;
import se.umu.cs.ads.sp.events.GameEvent;
import se.umu.cs.ads.sp.events.GameEvents;
import se.umu.cs.ads.sp.model.communication.ComHandler;
import se.umu.cs.ads.sp.model.communication.dto.*;
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

    private Timer gameTimer;
    private long remainingTime = 150;
    private boolean started = false;

    private long currentScoreHolderId;

    private int currentGold = 180;
    private int currentPoints;

    public ModelManager(GameController controller, User player) {
        map = new Map();
        this.player = player;
        lobbyHandler = new LobbyHandler(this);
        objectHandler = new ObjectHandler(player);
        comHandler = new ComHandler(player.port, controller, this);

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
            //comHandler.updatePlayerUnits(createUnitUpdateRequest(targetId), getPlayersToUpdate());
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
            //comHandler.updatePlayerUnits(createUnitUpdateRequest(-1), getPlayersToUpdate());
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
        l3Timer = new Timer();
        startL3Timer(Constants.L3_UPDATE_TIME);
        startRoundTimer();
    }

    private void startL3Timer(long updateTime) {
        l3Timer.schedule(new TimerTask() {
            @Override
            public void run() {
                sendL3Update();
            }
        }, 0, updateTime);
    }

    private void sendL3Update() {
        boolean isLeader = lobbyHandler.getLobby().leader.id == player.id;
        comHandler.sendL3Update(constructL3Message(isLeader), isLeader);
    }

    public L3UpdateDTO constructL3Message(boolean fromLeader) {
        ArrayList<EntitySkeletonDTO> entities;
        if (fromLeader) {
            entities = objectHandler.getAllEntitySkeletons();
        } else {
            entities = objectHandler.getMyUnitsToEntitySkeletons();
        }
        return new L3UpdateDTO(entities,
                objectHandler.getCollectedIds(),
                remainingTime,
                currentScoreHolderId,
                objectHandler.getAllEnvironmentsDTO(),
                Constants.LOW_SEVERITY
        );
    }

    public void receiveL3Update(L3UpdateDTO update) {

        //Todo do not update units or stuff if the author of the message is within l2 or l1
        objectHandler.updateUnitPositions(update.entities());
        objectHandler.removeCollectables(map, update.pickedUpCollectables());
        objectHandler.updateEnvironments(update.environments());
        if (!iAmLeader()) {
            this.remainingTime = update.remainingTime();
        }
        this.currentScoreHolderId = update.currentScoreLeader();
    }

    private boolean iAmLeader() {
        return lobbyHandler.getLobby().leader.id == player.id;
    }

    // A request has come in to start the game
    public void startGameReq(StartGameRequestDTO request) {
        //Check bounding boxes
        objectHandler.populateWorld(request, map);
        this.fow = new FowModel(new ArrayList<>(objectHandler.getMyUnits().values()));
        started = true;
        l3Timer = new Timer();
        startL3Timer(Constants.L3_UPDATE_TIME / 2);
        currentScoreHolderId = lobbyHandler.getLobby().leader.id;
    }

    public void leaveOngoingGame() {
        System.out.println("[Client] Leaving ongoing game...");

        comHandler.removePlayerUnits();
        objectHandler.clearSelectedUnitIds();
        objectHandler.getMyUnits().clear();
        objectHandler.getEnvironments().clear();
        objectHandler.getCollectables().clear();
        lobbyHandler.leaveLobby();
    }

    public L1UpdateDTO createUnitUpdateRequest(long targetId) {
        ArrayList<EntityDTO> unitUpdates = new ArrayList<>();
        for (PlayerUnit unit : objectHandler.getSelectedUnits()) {
            unitUpdates.add(new EntityDTO(
                    unit.getId(),
                    targetId,
                    unit.getPosition(),
                    unit.getDestination(),
                    unit.getMaxHp(),
                    unit.getCurrentHp(),
                    unit.getBaseSpeed()));
        }
        return new L1UpdateDTO(unitUpdates, player.id);
    }

    public ArrayList<EntitySkeletonDTO> createMySkeletonList() {
        return new ArrayList<>(objectHandler.getMyUnits().values().stream()
                .map(
                        playerUnit -> new EntitySkeletonDTO(playerUnit.getId(), player.id, playerUnit.getEntityType(), playerUnit.getPosition())
                ).toList());
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
                        long id = objectHandler.spawnFlag(map, unit.getPosition());
                        unit.setHasFlag(false);
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
        }
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

    public Rectangle getPlayerBoundingBox(ArrayList<PlayerUnit> units, int unitRadius) {
        if (units.isEmpty()) {
            System.out.println("Error: Must at least have one unit to create a bounding box!");
            return null;
        }

        // Initialize min and max coordinates to the first position
        int minX = units.get(0).getPosition().getX();
        int minY = units.get(0).getPosition().getY();
        int maxX = units.get(0).getPosition().getX();
        int maxY = units.get(0).getPosition().getY();

        // Iterate over the positions to find the min/max x and y values
        for (PlayerUnit unit : units) {
            int x = unit.getPosition().getX();
            int y = unit.getPosition().getY();
            if (x < minX) minX = x;
            if (y < minY) minY = y;
            if (x > maxX) maxX = x;
            if (y > maxY) maxY = y;
        }

        // Expand the bounding box by the radius
        minX -= unitRadius;
        minY -= unitRadius;
        maxX += unitRadius;
        maxY += unitRadius;

        return new Rectangle(minX, minY, maxX - minX, maxY - minY);
    }

    public int getL1Range() {
        // Adjust to ensure that the units do not teleport into view
        return fow.getFowRange();
    }

    public int getL2Range() {
        // Adjust to ensure that the units do not teleport into view
        return fow.getFowRange() + 250;
    }
}
