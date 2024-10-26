package se.umu.cs.ads.sp.controller;

import se.umu.cs.ads.ns.app.User;
import se.umu.cs.ads.sp.events.GameEvent;
import se.umu.cs.ads.sp.events.GameEvents;
import se.umu.cs.ads.sp.model.ModelManager;
import se.umu.cs.ads.sp.model.objects.entities.units.PlayerUnit;
import se.umu.cs.ads.sp.util.AppSettings;
import se.umu.cs.ads.sp.util.Constants;
import se.umu.cs.ads.sp.util.Position;
import se.umu.cs.ads.sp.util.UtilModel;

import javax.swing.*;
import java.util.ArrayList;

public class BotController implements Runnable {

    private final ModelManager modelManager;
    private Timer updateLobbyTimer;
    private Timer updateTimer;

    public BotController(long lobbyId) {
        User botUser = new User(UtilModel.generateRandomString(10), UtilModel.getLocalIP(), UtilModel.getFreePort());
        modelManager = new ModelManager(botUser);
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
                Thread.sleep(500);
                if (modelManager.hasGameStarted()) {
                    Thread.sleep(200);
                    moveRandomUnits();
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void initTimers() {

        updateLobbyTimer = new Timer(500, e -> {
            if (modelManager.hasGameStarted()) {
                updateLobbyTimer.stop();
                updateTimer.start();
            }
        });

        updateTimer = new Timer(1000 / Constants.FPS, e -> {
            if (modelManager.hasGameFinished()) {
                updateTimer.stop();
            }
            update();
        });
    }

    private void joinLobby(long lobbyId) {
        modelManager.getLobbyHandler().joinLobby(lobbyId);
        updateLobbyTimer.start();
    }

    public void update() {
        modelManager.update();
        GameEvents.getInstance().clearHistory();
    }

    private void moveRandomUnits() {
        int unitsToMove = UtilModel.getRandomInt(0, modelManager.getObjectHandler().getMyUnits().values().size() + 1);
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
            newPosition = new Position(UtilModel.getRandomInt(pos.getX() - offset, pos.getX() + offset), UtilModel.getRandomInt(pos.getY() - offset, pos.getY() + offset));
            offset += 100;
        } while (!modelManager.isWalkable(newPosition) && !modelManager.isInFov(newPosition));
        return newPosition;
    }

    public static void main(String[] args) {
        if (args.length != 1) {
            System.err.println("Usage: java AiController <lobbyId>");
            System.exit(1);
        }
        AppSettings.SetGameConfig();
        long lobbyId = Long.parseLong(args[0]);
        BotController aiController = new BotController(lobbyId);
        Thread aiThread = new Thread(aiController);
        aiThread.start();
    }
}

