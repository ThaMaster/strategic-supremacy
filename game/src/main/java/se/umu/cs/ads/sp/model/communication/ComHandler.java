package se.umu.cs.ads.sp.model.communication;

import io.grpc.Context;
import io.grpc.StatusRuntimeException;
import se.umu.cs.ads.ns.app.Lobby;
import se.umu.cs.ads.ns.app.User;
import se.umu.cs.ads.sp.model.ModelManager;
import se.umu.cs.ads.sp.model.communication.dto.*;
import se.umu.cs.ads.sp.model.communication.gameCom.GameClient;
import se.umu.cs.ads.sp.model.communication.gameCom.GameServer;
import se.umu.cs.ads.sp.model.communication.nsCom.NsClient;
import se.umu.cs.ads.sp.model.lobby.Raft;
import se.umu.cs.ads.sp.util.Constants;
import se.umu.cs.ads.sp.util.UtilModel;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

public class ComHandler {

    private final NsClient nsClient;
    private final ModelManager modelManager;
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
        new GameServer(port, this).start();
    }

    public void sendDefeatUpdate(long userId) {
        for (GameClient client : l3Clients.values()) {
            client.sendDefeatUpdate(userId);
        }
    }

    public void handleReceiveDefeatUpdate(long userId) {
        modelManager.receiveDefeatUpdate(userId);
    }

    public void sendEndGameMessage(UserScoreDTO userScore) {
        System.out.println("[Client] Sending message to end the game...");
        for (GameClient client : l3Clients.values()) {
            client.sendEndGameMessage(userScore);
        }
    }

    public void handleReceiveEndGameMessage(UserScoreDTO userScore) {
        modelManager.receiveEndGameMessage(userScore);
    }

    public void sendNextRound(StartGameRequestDTO request) {
        for (GameClient client : l3Clients.values()) {
            client.sendNextRoundRequest(request);
        }
    }

    public void handleReceiveNextRound(StartGameRequestDTO request) {
        modelManager.receiveNextRound(request);
    }

    public void sendL3Update(L3UpdateDTO message, boolean fromLeader) {
        if (fromLeader) {
            for (GameClient client : l3Clients.values()) {
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
                UtilModel.getRandomInt((int) Constants.L3_UPDATE_TIME, (int) Constants.L3_UPDATE_TIME + 2000);
        //Randomize to minimize the risk of two candidates starting election at the same time
    }

    public Long createLobby(User user, String name, int maxPlayers, String selectedMap) throws StatusRuntimeException {
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
                client.create(user.ip, user.port, user.username);
                l3Clients.put(user.id, client);
            }
        }

        if (!modelManager.hasGameStarted()) {
            System.out.println("Starting map " + updatedLobby.selectedMap);
            modelManager.loadMap(updatedLobby.selectedMap);
            modelManager.getLobbyHandler().setLobby(updatedLobby);
        }
    }

    public Lobby joinLobby(Long lobbyId, User user) throws StatusRuntimeException {
        Lobby joinedLobby = nsClient.joinLobby(lobbyId, user);
        if (joinedLobby != null) {
            sendUpdatedLobby(joinedLobby);
        }
        return joinedLobby;
    }

    public ArrayList<Lobby> fetchLobbies() throws StatusRuntimeException {
        return nsClient.fetchLobbies();
    }

    public void sendStartGameRequest(StartGameRequestDTO req, User user) {
        GameClient client = l3Clients.get(user.id);
        client.startGame(req);
        timeSinceL3Update = System.currentTimeMillis();
    }

    public void startGame(StartGameRequestDTO req) {
        modelManager.startGameReq(req);
        timeSinceL3Update = System.currentTimeMillis();
    }

    public void markLobbyStarted(long lobbyId) {
        nsClient.startLobby(lobbyId);
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
                client.create(user.ip, user.port, user.username);
                l3Clients.put(user.id, client);
            } else {
                client = l3Clients.get(user.id);
            }
            client.updateLobby(lobby, user);
        }
    }

    public synchronized void removePlayerUnits() {
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
        modelManager.removePlayer(userId, unitIds);
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

    public void requestVote(Raft raft) {
        long playerId = modelManager.getPlayer().id;
        LeaderRequestDto dto = new LeaderRequestDto(raft.getMsgCount(), playerId);

        for (GameClient client : l3Clients.values()) {
            client.requestVote(raft, dto);
        }
    }

    public boolean requestVoteRequest(int msgCount) {
        return modelManager.getRaft().approveNewLeader(msgCount);
    }

    public void moveUserToL3(Long userId) {
        if (l2Clients.containsKey(userId)) {
            l2Clients.remove(userId);
        } else if (l1Clients.containsKey(userId)) {
            l1Clients.remove(userId);
        }
    }

    public void moveUserToL2(Long userId) {
        if (l2Clients.containsKey(userId)) {
            return;
        }

        GameClient newClient;
        if (l1Clients.containsKey(userId)) {
            //Check if the client is in L1
            newClient = l1Clients.get(userId);
            l1Clients.remove(userId);
        } else {
            newClient = l3Clients.get(userId);
        }

        l2Clients.put(userId, newClient);
        updateInNewContext(2);
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
            newClient = l3Clients.get(userId);
        }

        l1Clients.put(userId, newClient);
        updateInNewContext(1);
    }

    public void notifyNewLeader() {
        long playerId = modelManager.getPlayer().id;
        for (GameClient client : l3Clients.values()) {
            System.out.println("\t Sending to " + client.getUsername());
            client.notifyNewLeader(GrpcUtil.toGrpcUserId(playerId));
        }
        modelManager.setNewLeader(modelManager.getPlayer().id);
        timeSinceL3Update = System.currentTimeMillis();
    }

    public void newLeaderReceived(Long userId) {
        modelManager.setNewLeader(userId);
        timeSinceL3Update = System.currentTimeMillis();
    }

    public int getNrL1Clients() {
        return l1Clients.size();
    }

    public int getNrL2Clients() {
        return l2Clients.size();
    }

    private void updateInNewContext(int layerIndex) {
        if (layerIndex < 0 || layerIndex > 2) {
            return;
        }

        // Create a new context and attach it (Needed because other
        // will immediately also use this sub).
        Context newContext = Context.current().fork();
        Context previousContext = newContext.attach();
        try {
            if (layerIndex == 1) {
                sendL1Update(modelManager.constructL1Message());
            } else if (layerIndex == 2) {
                sendL2Update(modelManager.constructL2Message());
            }
        } finally {
            // Switch back to the previous context
            newContext.detach(previousContext);
        }
    }

    public void resetClients() {
        l1Clients.clear();
        l2Clients.clear();
    }
}
