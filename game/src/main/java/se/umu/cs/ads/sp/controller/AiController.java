package se.umu.cs.ads.sp.controller;

import se.umu.cs.ads.ns.app.User;
import se.umu.cs.ads.sp.model.ModelManager;
import se.umu.cs.ads.sp.model.objects.entities.units.PlayerUnit;
import se.umu.cs.ads.sp.utils.Constants;
import se.umu.cs.ads.sp.utils.Position;
import se.umu.cs.ads.sp.utils.Utils;

import javax.swing.*;
import java.util.ArrayList;

public class AiController implements Runnable {

    private ModelManager modelManager;
    private User user;

    private Timer updateLobbyTimer;
    private Timer updateTimer;
    private Timer gameTimer;

    public AiController(long lobbyId) {
        user = new User(Utils.generateRandomString(10), Utils.getLocalIP(), Utils.getFreePort());
        modelManager = new ModelManager(user);

        initTimers();
        joinLobby(lobbyId);
    }

    private void initTimers() {
        // TODO: Should the AIs have these timers or should the model handle next round, starting the game, and
        // TODO: update the game by itself?

        gameTimer = new Timer(700, e -> {
            long remainingTime = modelManager.getRoundRemainingTime();
            if (remainingTime <= 0) {
                // TODO: next round stuff
            }
        });

        updateLobbyTimer = new Timer(500, e -> {
            if (modelManager.hasGameStarted()) {
                // TODO: If game has started, start updating the game!
                updateLobbyTimer.stop();
            }
        });
    }

    private void joinLobby(long lobbyId) {
        modelManager.getLobbyHandler().joinLobby(lobbyId);
        updateLobbyTimer.start();
    }

    private void createLobby(String lobbyName, int maxPlayers, String selectedMap) {
        modelManager.getLobbyHandler().createLobby(lobbyName, maxPlayers, selectedMap);
    }

    @Override
    public void run() {
    }


    private void moveRandomUnits() {
        int unitsToMove = Utils.getRandomInt(0, modelManager.getObjectHandler().getMyUnits().values().size());
        for (int i = 0; i < unitsToMove; i++) {
            PlayerUnit unit = new ArrayList<>(modelManager.getObjectHandler().getMyUnits().values()).get(i);
            modelManager.setSelection(unit.getId());
            modelManager.setEntityDestination(getRandomDestination());
        }
    }

    private Position getRandomDestination() {
        int maxX = modelManager.getMap().getCols() * Constants.TILE_WIDTH;
        int maxY = modelManager.getMap().getRows() * Constants.TILE_HEIGHT;

        Position newPosition;
        do {
            newPosition = new Position(Utils.getRandomInt(0, maxX), Utils.getRandomInt(0, maxY));
        } while (!modelManager.isWalkable(newPosition) && !modelManager.isInFov(newPosition));
        return newPosition;
    }
}
