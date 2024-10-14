package se.umu.cs.ads.sp.model;

import org.apache.commons.lang3.tuple.Pair;
import org.checkerframework.checker.units.qual.A;
import se.umu.cs.ads.ns.app.User;
import se.umu.cs.ads.sp.controller.GameController;
import se.umu.cs.ads.sp.events.GameEvents;
import se.umu.cs.ads.sp.model.communication.ComHandler;
import se.umu.cs.ads.sp.model.communication.dto.PlayerUnitUpdateRequest;
import se.umu.cs.ads.sp.model.communication.dto.StartGameRequest;
import se.umu.cs.ads.sp.model.communication.dto.UnitInfoDTO;
import se.umu.cs.ads.sp.model.map.FowModel;
import se.umu.cs.ads.sp.model.map.Map;
import se.umu.cs.ads.sp.model.objects.GameObject;
import se.umu.cs.ads.sp.model.objects.collectables.Gold;
import se.umu.cs.ads.sp.model.objects.collectables.Reward;
import se.umu.cs.ads.sp.model.objects.entities.units.PlayerUnit;
import se.umu.cs.ads.sp.utils.Constants;
import se.umu.cs.ads.sp.utils.Position;
import se.umu.cs.ads.sp.utils.Utils;

import java.util.ArrayList;

public class ModelManager {

    private int currentGold;
    private int currentPoints;
    private final Map map;
    private ObjectHandler objectHandler;

    private FowModel fow;
    private GameEvents gameEvents;

    private final ComHandler comHandler;
    private final User player;
    private final LobbyHandler lobbyHandler;

    public ModelManager(GameController controller, User player) {
        map = new Map();
        gameEvents = GameEvents.getInstance();
        this.player = player;
        lobbyHandler = new LobbyHandler(this);
        objectHandler = new ObjectHandler();
        comHandler = new ComHandler(player.port, controller, objectHandler);
    }

    public User getPlayer() {
        return this.player;
    }

    public ComHandler getComHandler() {
        return comHandler;
    }

    public void update() {
        // Update all entities in game, including my units.
        objectHandler.update();
        fow.updateUnitPositions(new ArrayList<>(objectHandler.getMyUnits().values()));
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
            comHandler.updatePlayerUnits(createUnitUpdateRequest(), getPlayersToUpdate());
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
        int col = position.getX() / Constants.TILE_HEIGHT;
        int row = position.getY() / Constants.TILE_WIDTH;
        if (map.inBounds(row, col)) {
            return !map.getModelMap().get(row).get(col).hasCollision();
        }
        return false;
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

    private void spawnGold(Position position) {
        Gold coin = new Gold(position, map);
        coin.setReward(new Reward(10, Reward.RewardType.GOLD));
        objectHandler.addCollectable(coin);
    }

    public void loadMap(String mapName) {
        map.loadMap("maps/" + mapName + ".txt");
    }

    public LobbyHandler getLobbyHandler() {
        return this.lobbyHandler;
    }

    public void startGame() {
        StartGameRequest req = objectHandler.initializeWorld(map, lobbyHandler.getLobby().users, this);
        for (User user : lobbyHandler.getLobby().users) {
            if (user.id != player.id) {
                System.out.println("Sending out to start game to " + user.username);
                comHandler.sendStartGameRequest(req, user);
            }
        }
        this.fow = new FowModel(new ArrayList<>(objectHandler.getMyUnits().values()));
    }

    // A request has come in to start the game
    public void startGameReq(StartGameRequest request) {
        objectHandler.populateWorld(request, map, this);
        this.fow = new FowModel(new ArrayList<>(objectHandler.getMyUnits().values()));
    }

    public PlayerUnitUpdateRequest createUnitUpdateRequest() {
        ArrayList<UnitInfoDTO> unitUpdates = new ArrayList<>();
        for(PlayerUnit unit : objectHandler.getMyUnits().values()) {
            unitUpdates.add(new UnitInfoDTO(
                    unit.getId(),
                    unit.getPosition(),
                    unit.getDestination(),
                    unit.getMaxHp(),
                    unit.getCurrentHp(),
                    unit.getSpeed()));
        }
        return new PlayerUnitUpdateRequest(unitUpdates, player.id);
    }

    public ArrayList<Long> getPlayersToUpdate() {
        // This function will maybe check which users are in L1, L2, L3?
        // Could also be somewhere else?
        return new ArrayList<>(lobbyHandler.getLobby().users.stream().map(user -> user.id).toList());
    }

}
