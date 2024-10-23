package se.umu.cs.ads.sp.model.communication;

import se.umu.cs.ads.ns.app.Lobby;
import se.umu.cs.ads.ns.app.User;
import se.umu.cs.ads.sp.model.ModelManager;
import se.umu.cs.ads.sp.model.communication.dto.L1UpdateDTO;
import se.umu.cs.ads.sp.model.communication.dto.L2UpdateDTO;
import se.umu.cs.ads.sp.model.communication.dto.L3UpdateDTO;
import se.umu.cs.ads.sp.model.communication.dto.StartGameRequestDTO;
import se.umu.cs.ads.sp.model.communication.gameCom.GameClient;
import se.umu.cs.ads.sp.model.communication.gameCom.GameServer;
import se.umu.cs.ads.sp.model.communication.nsCom.NsClient;
import se.umu.cs.ads.sp.utils.Constants;
import se.umu.cs.ads.sp.utils.Utils;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

public class ComHandler {

    private final NsClient nsClient;
    private final ModelManager modelManager;
    private final GameServer server;
    private final ConcurrentHashMap<Long, GameClient> l3Clients;
    private final ConcurrentHashMap<Long, GameClient> l2Clients;
    private final ConcurrentHashMap<Long, GameClient> l1Clients;
    private long timeSinceL3Update;

    public ComHandler(int port, ModelManager modelManager) {
        l3Clients = new ConcurrentHashMap<>();
        l2Clients = new ConcurrentHashMap<>();
        l1Clients = new ConcurrentHashMap<>();

        nsClient = new NsClient();
        this.modelManager = modelManager;
        server = new GameServer(port, this);
        server.start();
    }

    public void sendL3Update(L3UpdateDTO message, boolean fromLeader) {
        // Make all async calls here
        if (fromLeader) {
            for (GameClient client : l3Clients.values()) {
                // Send l3 update to everyone
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

    public void handleReceiveL3Msg(L3UpdateDTO message) {
        timeSinceL3Update = System.currentTimeMillis();
        modelManager.receiveL3Update(message);
    }

    public void sendL2Update(L2UpdateDTO message) {
        if (l2Clients.isEmpty()) {
            return;
        }
        for (GameClient client : l2Clients.values()) {
            // Send l2 update only to those in the zone
            client.sendL2Message(message);
        }
    }

    public void handleReceiveL2Msg(L2UpdateDTO message) {
        modelManager.receiveL2Update(message);
    }

    public void sendL1Update(L1UpdateDTO message) {
        if (l1Clients.isEmpty()) {
            return;
        }
        for (GameClient client : l1Clients.values()) {
            // Send l1 update only to those in the zone
            client.sendL1Message(message);
        }
    }

    public void handleReceiveL1Msg(L1UpdateDTO message) {
        modelManager.receiveL1Update(message);
    }

    public boolean leaderIsAlive() {
        return (System.currentTimeMillis() - timeSinceL3Update) <
                Utils.getRandomInt((int) Constants.L3_UPDATE_TIME, (int) Constants.L3_UPDATE_TIME + 2000);
        //Randomize to minimize the risk of two candidates starting election at the same time
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

    public void startGame(StartGameRequestDTO req) {
        modelManager.startGameReq(req);
    }

    private void sendUpdatedLobby(Lobby lobby) {
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
        if (l1Clients.containsKey(userId)) {
            l1Clients.get(userId).destroy();
            l1Clients.remove(userId);
        } else if (l2Clients.containsKey(userId)) {
            l2Clients.get(userId).destroy();
            l2Clients.remove(userId);
        }

        l3Clients.get(userId).destroy();
        l3Clients.remove(userId);
    }

    public boolean requestVote() {
        //for(GameClient client : l3Clients){

        // }
        return true;
    }

    public void voteReceived() {
    }

    public void moveUserToL3(Long userId) {
        if (l2Clients.containsKey(userId)) {
            l2Clients.get(userId).destroy();
            l2Clients.remove(userId);
        } else if (l1Clients.containsKey(userId)) {
            l1Clients.get(userId).destroy();
            l1Clients.remove(userId);
        }
    }

    public void moveUserToL2(Long userId) {
        if (l2Clients.containsKey(userId)) {
            return;
        }

        GameClient newClient;
        if (l1Clients.containsKey(userId)) {
            // Check if the client is in L1
            newClient = l1Clients.get(userId);
            l1Clients.remove(userId);
        } else {
            // If in L3, need to create a new client since context causes problem
            GameClient l3Client = l3Clients.get(userId);
            newClient = new GameClient();
            newClient.create(l3Client.getIp(), l3Client.getPort());
        }

        l2Clients.put(userId, newClient);
    }

    public void moveUserToL1(Long userId) {
        if (l1Clients.containsKey(userId)) {
            return;
        }

        GameClient newClient;
        if (l2Clients.containsKey(userId)) {
            // Check if the client is in L2
            newClient = l2Clients.get(userId);
            l2Clients.remove(userId);
        } else {
            // If in L3, need to create a new client since context causes problem
            GameClient l3Client = l3Clients.get(userId);
            newClient = new GameClient();
            newClient.create(l3Client.getIp(), l3Client.getPort());
        }

        l1Clients.put(userId, newClient);
    }

    public int getNrL1Clients() {
        return l1Clients.size();
    }

    public int getNrL2Clients() {
        return l2Clients.size();
    }
}
