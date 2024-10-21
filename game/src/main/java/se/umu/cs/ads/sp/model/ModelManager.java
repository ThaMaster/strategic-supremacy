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
import se.umu.cs.ads.sp.view.soundmanager.SoundFX;
import se.umu.cs.ads.sp.view.soundmanager.SoundManager;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class ModelManager {

    private int currentGold = 180;
    private int currentPoints;
    private final Map map;
    private ObjectHandler objectHandler;

    private FowModel fow;

    private final ComHandler comHandler;
    private final User player;
    private final LobbyHandler lobbyHandler;
    private boolean started = false;
    private Timer l3Timer;

    private GameController controller;

    public ModelManager(GameController controller, User player) {
        map = new Map();
        this.player = player;
        lobbyHandler = new LobbyHandler(this);
        objectHandler = new ObjectHandler(player);
        comHandler = new ComHandler(player.port, controller, this);

        Runtime.getRuntime().addShutdownHook(new Thread(this::leaveOngoingGame));
        this.controller = controller;
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
            comHandler.updatePlayerUnits(createUnitUpdateRequest(targetId), getPlayersToUpdate());
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
            comHandler.updatePlayerUnits(createUnitUpdateRequest(-1), getPlayersToUpdate());
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
        started = true;
        l3Timer = new Timer();
        //startL3Timer(Constants.L3_UPDATE_TIME);
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
        L3UpdateDTO dto;
        ArrayList<EntitySkeletonDTO> entities = new ArrayList<>();
        if (fromLeader) {
            //Get the current state of the world
            entities = objectHandler.getAllEntitySkeletons();
        } else {
            //Only send my units to leader
            entities = objectHandler.getMyUnitsToEntitySkeletons();

        }
        ArrayList<Long> collectedIds = objectHandler.getCollectedIds();
        return new L3UpdateDTO(entities,
                objectHandler.getCollectedIds(),
                10L, //Todo send remaining time
                1L, //Todo send current leader
                new ArrayList<>(), //Todo sent environment
                Constants.LOW_SEVERITY
        );
    }

    public void receiveL3Update(L3UpdateDTO update) {
        //Todo
        objectHandler.updateUnitPositions(update.entities());
    }

    // A request has come in to start the game
    public void startGameReq(StartGameRequestDTO request) {
        //Check bounding boxes
        objectHandler.populateWorld(request, map);
        this.fow = new FowModel(new ArrayList<>(objectHandler.getMyUnits().values()));
        started = true;
        l3Timer = new Timer();
        //startL3Timer(Constants.L3_UPDATE_TIME / 2);
    }

    public void leaveOngoingGame() {
        System.out.println("[Client] Leaving ongoing game...");

        comHandler.removePlayerUnits();
        objectHandler.getMyUnits().clear();
        objectHandler.getSelectedUnits().clear();
        objectHandler.getEnvironments().clear();
        objectHandler.getCollectables().clear();
        lobbyHandler.leaveLobby();
    }

    public PlayerUnitUpdateRequestDTO createUnitUpdateRequest(long targetId) {
        ArrayList<CompleteUnitInfoDTO> unitUpdates = new ArrayList<>();
        for (PlayerUnit unit : objectHandler.getSelectedUnits()) {
            unitUpdates.add(new CompleteUnitInfoDTO(
                    unit.getId(),
                    targetId,
                    unit.getPosition(),
                    unit.getDestination(),
                    unit.getMaxHp(),
                    unit.getCurrentHp(),
                    unit.getBaseSpeed()));
        }
        return new PlayerUnitUpdateRequestDTO(unitUpdates, player.id);
    }

    public ArrayList<Long> getPlayersToUpdate() {
        // This function will maybe check which users are in L1, L2, L3?

        return new ArrayList<>(lobbyHandler.getLobby().users.stream().map(user -> user.id).toList());
    }

    public ArrayList<EntitySkeletonDTO> createMySkeletonList() {
        return new ArrayList<>(objectHandler.getMyUnits().values().stream()
                .map(
                        playerUnit -> new EntitySkeletonDTO(playerUnit.getId(), player.id, playerUnit.getEntityName(), playerUnit.getPosition())
                ).toList());
    }

    public boolean hasGameStarted() {
        return started;
    }

    private void collectEvents() {
        ArrayList<GameEvent> events = GameEvents.getInstance().getEvents();
        for (GameEvent event : events) {
            switch (event.getType()) {
                case COLLECT:
                    break;
                case NEW_ROUND:
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
                        controller.spawnFlag(id, unit.getPosition());
                    }
                    break;
                case ATTACK:
                    SoundManager.getInstance().play(SoundFX.ATTACK);
                    break;
                case TAKE_DMG:
                    SoundManager.getInstance().play(SoundFX.TAKE_DMG);
                    break;
                case FLAG_TO_BASE:
                    this.currentPoints += 10;
                    break;
                case POINT_PICK_UP:
                    int point = Reward.parseQuantity(event.getEvent());
                    this.currentPoints += point;
                    break;
                case GOLD_PICK_UP:
                    int gold = Reward.parseQuantity(event.getEvent());
                    this.currentGold += gold;
                    break;
                default:
                    //This is default case, it's a collectable we have stored
                    break;
            }
        }
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
}
