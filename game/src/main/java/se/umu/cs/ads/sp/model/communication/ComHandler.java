package se.umu.cs.ads.sp.model.communication;

import io.grpc.Context;
import se.umu.cs.ads.ns.app.Lobby;
import se.umu.cs.ads.ns.app.User;
import se.umu.cs.ads.sp.model.ModelManager;
import se.umu.cs.ads.sp.model.communication.dto.L1UpdateDTO;
import se.umu.cs.ads.sp.model.communication.dto.L3UpdateDTO;
import se.umu.cs.ads.sp.model.communication.dto.StartGameRequestDTO;
import se.umu.cs.ads.sp.model.communication.gameCom.GameClient;
import se.umu.cs.ads.sp.model.communication.gameCom.GameServer;
import se.umu.cs.ads.sp.model.communication.nsCom.NsClient;
import se.umu.cs.ads.sp.utils.Constants;
import se.umu.cs.ads.sp.utils.Utils;

import java.util.ArrayList;
import java.util.HashMap;

public class ComHandler {

    private final NsClient nsClient;
    private final ModelManager modelManager;
    private final GameServer server;
    private final HashMap<Long, GameClient> l3Clients;
    private final HashMap<Long, GameClient> l2Clients;
    private final HashMap<Long, GameClient> l1Clients;
    private long timeSinceL3Update;

    public ComHandler(int port, ModelManager modelManager) {
        l3Clients = new HashMap<>();
        l2Clients = new HashMap<>();
        l1Clients = new HashMap<>();

        nsClient = new NsClient();
        this.modelManager = modelManager;
        server = new GameServer(port, this);
        server.start();
    }

    public void sendL3Update(L3UpdateDTO message, boolean fromLeader) {
        if (fromLeader) {
            for (GameClient client : l3Clients.values()) {
                //Send l3 update to everyone
                client.sendL3Message(message);
            }
        } else {
            User leader = modelManager.getLobbyHandler().getLobby().leader;
            if (l3Clients.containsKey(leader.id)) {
                GameClient client = l3Clients.get(leader.id);
                client.sendL3Message(message);
            }

        }
    }

    public boolean leaderIsAlive() {
        return (System.currentTimeMillis() - timeSinceL3Update) <
                Utils.getRandomInt((int) Constants.L3_UPDATE_TIME, (int) Constants.L3_UPDATE_TIME + 2000);
        //Randomize to minimize the risk of two candidates starting election at the same time
    }

    public void handleReceiveL3Msg(L3UpdateDTO message) {
        timeSinceL3Update = System.currentTimeMillis();
        modelManager.receiveL3Update(message);
    }

    public Long createLobby(User user, String name, int maxPlayers, String selectedMap) {
        return nsClient.createLobby(user, name, maxPlayers, selectedMap);
    }

    public void leaveLobby(Lobby lobbyToLeave, User user) {
        User newLeader = nsClient.leaveLobby(lobbyToLeave.id, user);
        if (newLeader != null) {
            lobbyToLeave.leader = newLeader;
            sendUpdatedLobby(lobbyToLeave);
        }
        l3Clients.clear();
        l2Clients.clear();
        l1Clients.clear();
    }

    public void updateLobby(Lobby updatedLobby) {
        for (User user : updatedLobby.users) {
            if (!l3Clients.containsKey(user.id) && user.id != modelManager.getPlayer().id) {
                GameClient client = new GameClient();
                client.create(user.ip, user.port);
                l3Clients.put(user.id, client);
            }
        }

        if (!modelManager.hasGameStarted()) {
            modelManager.loadMap(updatedLobby.selectedMap);
            modelManager.getLobbyHandler().setLobby(updatedLobby);
        }
    }

    public Lobby joinLobby(Long lobbyId, User user) {
        Lobby joinedLobby = nsClient.joinLobby(lobbyId, user);
        if (joinedLobby != null) {
            sendUpdatedLobby(joinedLobby);
        }
        return joinedLobby;
    }

    public ArrayList<Lobby> fetchLobbies() {
        return nsClient.fetchLobbies();
    }

    public void sendStartGameRequest(StartGameRequestDTO req, User user) {
        GameClient client = l3Clients.get(user.id);
        client.startGame(req);
        timeSinceL3Update = System.currentTimeMillis();
    }

    public void updatePlayerUnits(L1UpdateDTO req, ArrayList<Long> playerIds) {
        for (Long id : playerIds) {
            if (id == modelManager.getPlayer().id) {
                continue;
            }
            l3Clients.get(id).updateUnits(req);
        }
    }

    public void startGame(StartGameRequestDTO req) {
        modelManager.startGameReq(req);
    }

    private void sendUpdatedLobby(Lobby lobby) {
        Context newContext = Context.current().fork();
        Context origContext = newContext.attach();
        try {
            // Make all async calls here
            for (User user : lobby.users) {
                //No need to send the update to ourselves
                if (user.id == modelManager.getPlayer().id) {
                    continue;
                }
                GameClient client;
                if (!l3Clients.containsKey(user.id)) {
                    client = new GameClient();
                    client.create(user.ip, user.port);
                    l3Clients.put(user.id, client);
                } else {
                    client = l3Clients.get(user.id);
                }
                client.updateLobby(lobby, user);
            }
        } finally {
            // Return to old context
            newContext.detach(origContext);
        }
    }

    public void updateEnemyUnits(L1UpdateDTO req) {
        modelManager.getObjectHandler().updateEnemyUnits(req.unitUpdates());
    }

    public void removePlayerUnits() {
        if (modelManager.getObjectHandler().getMyUnits().isEmpty()) {
            return;
        }

        for (User user : modelManager.getLobbyHandler().getLobby().users) {
            //No need to send the update to ourselves
            if (user.id == modelManager.getPlayer().id) {
                continue;
            }
            l3Clients.get(user.id).removePlayerUnits(modelManager.createMySkeletonList());
        }
    }

    public void removePlayer(long userId, ArrayList<Long> unitIds) {
        removeGameClient(userId);
        modelManager.getObjectHandler().removeEnemyUnits(unitIds);
        modelManager.getLobbyHandler().removePlayer(userId);
    }

    private void removeGameClient(long userId) {
        if (l3Clients.containsKey(userId)) {
            GameClient client = l3Clients.get(userId);
            client.shutdown();
            l3Clients.remove(userId);
        }
    }

    public boolean requestVote() {
        //for(GameClient client : l3Clients){

        // }
        return true;
    }

    public void voteReceived() {

    }

    public void updateLayers(ArrayList<Long> newL3Clients, ArrayList<Long> newL2Clients, ArrayList<Long> newL1Clients) {
        for (Long id : newL3Clients) {
            if (l2Clients.containsKey(id)) {
                l3Clients.put(id, l2Clients.get(id));
                l2Clients.remove(id);
            } else if (l1Clients.containsKey(id)) {
                l3Clients.put(id, l1Clients.get(id));
                l1Clients.remove(id);
            }
        }

        for (Long id : newL2Clients) {
            if (l3Clients.containsKey(id)) {
                l2Clients.put(id, l3Clients.get(id));
                l3Clients.remove(id);
            } else if (l1Clients.containsKey(id)) {
                l2Clients.put(id, l1Clients.get(id));
                l1Clients.remove(id);
            }
        }

        for (Long id : newL1Clients) {
            if (l3Clients.containsKey(id)) {
                l1Clients.put(id, l3Clients.get(id));
                l3Clients.remove(id);
            } else if (l2Clients.containsKey(id)) {
                l1Clients.put(id, l2Clients.get(id));
                l2Clients.remove(id);
            }
        }
    }
}
