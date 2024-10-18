package se.umu.cs.ads.sp.model;

import se.umu.cs.ads.ns.app.Lobby;
import se.umu.cs.ads.ns.app.User;

import java.util.ArrayList;

public class LobbyHandler {

    private Lobby lobby;
    private final ModelManager modelManager;

    public LobbyHandler(ModelManager modelManager) {
        this.modelManager = modelManager;
    }

    public void createLobby(String lobbyName, int maxPlayers, String selectedMap) {
        long id = modelManager.getComHandler().createLobby(modelManager.getPlayer(), lobbyName, maxPlayers, selectedMap);
        lobby = new Lobby(id, lobbyName, maxPlayers);
        lobby.leader = modelManager.getPlayer();
        lobby.users.add(modelManager.getPlayer());
        lobby.selectedMap = selectedMap;
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
        lobby = modelManager.getComHandler().joinLobby(lobbyId, modelManager.getPlayer());
        return lobby;
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
}
