package se.umu.cs.ads.sp.controller;

import se.umu.cs.ads.ns.app.Lobby;
import se.umu.cs.ads.ns.app.User;
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
    private final int lobbyCount;

    public BotController(long lobbyId, boolean isLeader, String forceFlag, int nrBots, int mapIndex, boolean runTests, boolean testAll) {
        lobbyCount = nrBots;
        User botUser = new User(UtilModel.generateRandomString(10), UtilModel.getLocalIP(), UtilModel.getFreePort());
        modelManager = new ModelManager(botUser);
        initTimers();
        if (isLeader) {
            System.out.println("[Leader Bot] Trying to create lobby with the following settings:");
            String ff = forceFlag.isEmpty() ? "None" : forceFlag;
            String testing = testAll ? "ALL" : (runTests ? "LEADER_ONLY" : "NONE");
            System.out.println("\t Force-Flag: " + ff + " Bots: " + nrBots + " MapIndex: " + mapIndex + " Testing: " + testing);
            if (createLobby(nrBots, mapIndex)) {
                System.out.println("\t Lobby created, spawning bots...");
                updateLobbyTimer.start();
            } else {
                System.out.println("[Leader Bot] Could not create a lobby, shutting down...");
                System.exit(0);
            }
        } else {
            System.out.println("[Follower Bot] Trying to join lobby");
            if (lobbyId == -1) {
                for (Lobby lobby : modelManager.getLobbyHandler().fetchLobbies()) {
                    if (joinLobby(lobby.id)) {
                        // Successfully joined lobby
                        break;
                    }
                }
            } else {
                joinLobby(lobbyId);
            }
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
            if (modelManager.iAmLeader()) {
                // I am leader, waiting for bots to populate lobby
                if (lobbyCount == modelManager.getLobbyHandler().getLobby().currentPlayers) {
                    System.out.println("[Leader Bot] All bots connected, starting game...");
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException exception) {
                        exception.printStackTrace();
                    }
                    modelManager.startGame();
                    updateLobbyTimer.stop();
                    updateTimer.start();
                } else {
                    System.out.println("[Leader Bot] " + modelManager.getLobbyHandler().getLobby().currentPlayers + "/" + lobbyCount + " in lobby...");
                }
            } else {
                // I am follower, waiting for game to start...
                if (modelManager.hasGameStarted()) {
                    System.out.println("[Follower Bot] Game started, time to play!");
                    updateLobbyTimer.stop();
                    updateTimer.start();
                } else {
                    System.out.println("[Follower Bot] Still waiting for game to start...");
                }
            }
        });
        updateTimer = new Timer(1000 / Constants.FPS, e -> {
            if (modelManager.hasGameFinished()) {
                if (modelManager.iAmLeader()) {
                    System.out.println("[Leader Bot] Game Finished!");
                } else {
                    System.out.println("[Follower Bot] Game Finished!");
                }
                updateTimer.stop();
                modelManager.leaveOngoingGame();
                System.exit(0);
            }
            update();
        });
    }

    private boolean joinLobby(long lobbyId) {
        modelManager.getLobbyHandler().joinLobby(lobbyId);
        if (modelManager.getLobbyHandler().hasErrorOccurred()) {
            System.out.println(modelManager.getLobbyHandler().getErrorMessage());
            modelManager.getLobbyHandler().clearErrors();
            return false;
        }
        updateLobbyTimer.start();
        return true;
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
        if (args.length != 7) {
            System.err.println("Usage: java AiController <lobbyId> <isLeader> <forceFlag> <nrBots> <mapIndex> <runTests> <testAll>");
            System.exit(1);
        }
        AppSettings.SetGameConfig();
        long lobbyId = Long.parseLong(args[0]);
        boolean isLeader = Boolean.parseBoolean(args[1]);
        String forceFlag = args[2];
        int nrBots = Integer.parseInt(args[3]);
        int mapIndex = Integer.parseInt(args[4]);
        boolean runTests = Boolean.parseBoolean(args[5]);
        boolean testAll = Boolean.parseBoolean(args[6]);

        AppSettings.RUN_PERFORMANCE_TEST = runTests;
        AppSettings.NUM_BOTS = nrBots;
        if (forceFlag.equals("-l1")) {
            AppSettings.FORCE_L1 = true;
        } else if (forceFlag.equals("-l2")) {
            AppSettings.FORCE_L2 = true;
        } else if (forceFlag.equals("-l3")) {
            AppSettings.FORCE_L3 = true;
        }

        BotController aiController = new BotController(lobbyId, isLeader, forceFlag, nrBots, mapIndex, runTests, testAll);
        Thread aiThread = new Thread(aiController);
        aiThread.start();
    }

    private boolean createLobby(int nrBots, int mapIndex) {
        String selectedMap = switch (mapIndex) {
            case 0 -> "Beginner";
            case 1 -> "Easy";
            case 2 -> "Medium";
            case 3 -> "Hard";
            case 4 -> "Extreme";
            default -> "";
        };

        modelManager.getLobbyHandler().createLobby("TestLobby-CreatedByBot", nrBots, selectedMap);
        if (modelManager.getLobbyHandler().hasErrorOccurred()) {
            System.out.println(modelManager.getLobbyHandler().getErrorMessage());
            modelManager.getLobbyHandler().clearErrors();
            return false;
        }
        return true;
    }
}

