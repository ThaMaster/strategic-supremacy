package se.umu.cs.ads.sp.model;

import se.umu.cs.ads.ns.app.Lobby;

import java.util.ArrayList;

public class LobbyHandler {

    private Lobby lobby;
    private final ModelManager modelManager;
    private String[][] fetchedLobbies;

    public LobbyHandler(ModelManager modelManager) {
        this.modelManager = modelManager;
    }

    public void createLobby(String lobbyName, int maxPlayers, String selectedMap) {
        long id = modelManager.getComHandler().createLobby(modelManager.getPlayer(), lobbyName, maxPlayers, selectedMap);
        lobby = new Lobby(id, lobbyName, maxPlayers);
        lobby.users.add(modelManager.getPlayer());
        lobby.selectedMap = selectedMap;
    }

    public void leaveLobby() {
        modelManager.getComHandler().leaveLobby(this.lobby.id, modelManager.getPlayer());
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
}
