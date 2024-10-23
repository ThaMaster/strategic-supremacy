package se.umu.cs.ads.sp.controller;

import se.umu.cs.ads.ns.app.User;
import se.umu.cs.ads.sp.model.ModelManager;
import se.umu.cs.ads.sp.model.objects.entities.units.PlayerUnit;
import se.umu.cs.ads.sp.util.Position;
import se.umu.cs.ads.sp.util.UtilModel;

import javax.swing.*;
import java.util.ArrayList;

public class BotController implements Runnable {

    private ModelManager modelManager;
    private User user;

    private Timer updateLobbyTimer;
    private Timer updateTimer;
    private Timer gameTimer;

    public BotController(long lobbyId) {
        user = new User(UtilModel.generateRandomString(10), UtilModel.getLocalIP(), UtilModel.getFreePort());
        modelManager = new ModelManager(user);
        initTimers();
        if (lobbyId == -1) {
            joinLobby(modelManager.getLobbyHandler().fetchLobbies().get(0).id);
        } else {
            joinLobby(lobbyId);
        }
    }

    @Override
    public void run() {
        while (true) {
            try {
                Thread.sleep(1000);
                if (modelManager.hasGameStarted()) {
                    Thread.sleep(1000);
                    moveRandomUnits();
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
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
                updateTimer.start();
                gameTimer.start();
            }
        });
        updateTimer = new Timer(1000 / 60, e -> {
            update();
        });
    }

    private void joinLobby(long lobbyId) {
        modelManager.getLobbyHandler().joinLobby(lobbyId);
        updateLobbyTimer.start();
    }

    public void update() {
        modelManager.update();
    }

    private void moveRandomUnits() {
        int unitsToMove = UtilModel.getRandomInt(0, modelManager.getObjectHandler().getMyUnits().values().size());
        for (int i = 0; i < unitsToMove; i++) {
            PlayerUnit unit = new ArrayList<>(modelManager.getObjectHandler().getMyUnits().values()).get(i);
            modelManager.setSelection(unit.getId());
            modelManager.setEntityDestination(getRandomDestination(unit.getPosition()));
        }
    }

    private Position getRandomDestination(Position pos) {
        Position newPosition;
        int offset = 100;
        do {
            newPosition = new Position(UtilModel.getRandomInt(pos.getX(), pos.getX() + offset), UtilModel.getRandomInt(pos.getY(), pos.getY() + offset));
            offset += 100;
        } while (!modelManager.isWalkable(newPosition) && !modelManager.isInFov(newPosition));
        return newPosition;
    }
}
