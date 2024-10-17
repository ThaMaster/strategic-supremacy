package se.umu.cs.ads.sp.model.communication;

import io.grpc.Context;
import se.umu.cs.ads.ns.app.Lobby;
import se.umu.cs.ads.ns.app.User;
import se.umu.cs.ads.sp.controller.GameController;
import se.umu.cs.ads.sp.model.ModelManager;
import se.umu.cs.ads.sp.model.communication.dto.PlayerUnitUpdateRequest;
import se.umu.cs.ads.sp.model.communication.dto.StartGameRequest;
import se.umu.cs.ads.sp.model.communication.gameCom.GameClient;
import se.umu.cs.ads.sp.model.communication.gameCom.GameServer;
import se.umu.cs.ads.sp.model.communication.nsCom.NsClient;

import java.util.ArrayList;
import java.util.HashMap;

public class ComHandler {

    private NsClient nsClient;
    private GameController controller;
    private ModelManager modelManager;
    private GameServer server;
    private HashMap<Long, GameClient> l1Clients;
    private HashMap<Long, GameClient> l2Clients;
    private HashMap<Long, GameClient> l3Clients;

    public ComHandler(int port, GameController controller, ModelManager modelManager) {
        l3Clients = new HashMap<>();
        nsClient = new NsClient();
        this.controller = controller;
        this.modelManager = modelManager;
        server = new GameServer(port, this);
        server.start();
    }

    public void sendL3Update(boolean fromLeader){
        if(fromLeader){
            for(GameClient client : l3Clients.values()){
                //Send l3 update to everyone
                System.out.println("SENDING UPDATE FROM LEADER");
            }
        }else{
            User leader = modelManager.getLobbyHandler().getLobby().leader;
            GameClient client = l3Clients.get(leader.id);
            System.out.println("SENDING UPDATE TO LEADER");
            //Send l3 to leader
        }
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
    }

    public void updateLobby(Lobby updatedLobby) {
        for (User user : updatedLobby.users) {

            if (!l3Clients.containsKey(user.id)) {
                GameClient client = new GameClient();
                client.create(user.ip, user.port);
                l3Clients.put(user.id, client);
            }
        }
        controller.updateLobby(updatedLobby);
    }

    public Lobby joinLobby(Long lobbyId, User user) {
        Lobby joinedLobby = nsClient.joinLobby(lobbyId, user);
        if(joinedLobby != null) {
            sendUpdatedLobby(joinedLobby);
        }
        return joinedLobby;
    }

    public ArrayList<Lobby> fetchLobbies() {
        return nsClient.fetchLobbies();
    }

    public void sendStartGameRequest(StartGameRequest req, User user) {
        GameClient client = l3Clients.get(user.id);
        client.startGame(req);
    }

    public void updatePlayerUnits(PlayerUnitUpdateRequest req, ArrayList<Long> playerIds) {
        for (Long id : playerIds) {
            if (id == controller.getModelManager().getPlayer().id) {
                continue;
            }
            l3Clients.get(id).updateUnits(req);
        }
    }

    public void startGame(StartGameRequest req) {
        controller.startGame(req);
    }

    private void sendUpdatedLobby(Lobby lobby) {
        Context newContext = Context.current().fork();
        Context origContext = newContext.attach();
        try {
            // Make all async calls here
            for (User user : lobby.users) {
                //No need to send the update to ourselves
                if (user.id == controller.getModelManager().getPlayer().id) {
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

    public void updateEnemyUnits(PlayerUnitUpdateRequest req) {
        modelManager.getObjectHandler().updateEnemyUnits(req.unitUpdates());
    }

    public void removePlayerUnits() {
        if(modelManager.getObjectHandler().getMyUnits().isEmpty()) {
            return;
        }

        for (User user : modelManager.getLobbyHandler().getLobby().users) {
            //No need to send the update to ourselves
            if (user.id == controller.getModelManager().getPlayer().id) {
                continue;
            }
            l3Clients.get(user.id).removePlayerUnits(modelManager.createMySkeletonList());
        }
    }

    public void removePlayer(long userId, ArrayList<Long> unitIds) {
        removeGameClient(userId);
        controller.removeEnemyUnits(userId, unitIds);
    }

    private void removeGameClient(long userId) {
        if (l3Clients.containsKey(userId)) {
            GameClient client = l3Clients.get(userId);
            client.shutdown();
            l3Clients.remove(userId);
        }
    }
}
