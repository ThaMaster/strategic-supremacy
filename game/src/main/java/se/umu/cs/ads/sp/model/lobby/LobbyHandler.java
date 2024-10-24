package se.umu.cs.ads.sp.model.lobby;

import se.umu.cs.ads.ns.app.Lobby;
import se.umu.cs.ads.ns.app.User;
import se.umu.cs.ads.sp.model.ModelManager;
import se.umu.cs.ads.sp.util.enums.LobbyClientState;

import java.util.ArrayList;

public class LobbyHandler {

    private Lobby lobby;
    private final ModelManager modelManager;
    private Raft raft;

    public LobbyHandler(ModelManager modelManager) {
        this.modelManager = modelManager;
    }

    public void createLobby(String lobbyName, int maxPlayers, String selectedMap) {
        long id = modelManager.getComHandler().createLobby(modelManager.getPlayer(), lobbyName, maxPlayers, selectedMap);
        lobby = new Lobby(id, lobbyName, maxPlayers);
        lobby.leader = modelManager.getPlayer();
        raft = new Raft(this, LobbyClientState.LEADER, modelManager.getComHandler());
        lobby.users.add(modelManager.getPlayer());
        lobby.selectedMap = selectedMap;
        modelManager.loadMap(selectedMap);
        lobby.currentPlayers = 1;
    }

    public void leaveLobby() {
        if (lobby == null) {
            return;
        }

        lobby.users.removeIf(user -> user.id == modelManager.getPlayer().id);
        lobby.currentPlayers--;
        modelManager.getComHandler().leaveLobby(lobby, modelManager.getPlayer());

        // Remove the lobby
        lobby = null;
    }

    public ArrayList<Lobby> fetchLobbies() {
        return modelManager.getComHandler().fetchLobbies();
    }

    public Lobby joinLobby(long lobbyId) {
        raft = new Raft(this, LobbyClientState.FOLLOWER, modelManager.getComHandler());
        lobby = modelManager.getComHandler().joinLobby(lobbyId, modelManager.getPlayer());
        modelManager.loadMap(lobby.selectedMap);
        return lobby;
    }

    public void setNewLeader(long userId) {
        for (User user : lobby.users) {
            if (user.id == userId) {
                lobby.leader = user;
            }
        }
        if (!raft.iAmLeader()) {
            raft.newLeaderElected();
        }
    }

    public Raft getRaft() {
        return raft;
    }

    public Lobby getLobby() {
        return lobby;
    }

    public void setLobby(Lobby newLobby) {
        lobby = newLobby;
    }

    public void removePlayer(long playerId) {
        ArrayList<User> users = this.lobby.users;
        int userIndex = -1;
        for (int i = 0; i < users.size(); i++) {
            if (users.get(i).id == playerId) {
                userIndex = i;
                break;
            }
        }
        if (userIndex != -1) {
            this.lobby.users.remove(userIndex);
        }
    }

    public User getPlayer(long userId) {
        for (User user : getLobby().users) {
            if (user.id == userId) {
                return user;
            }
        }
        return null;
    }

}
