package se.umu.cs.ads.sp.model.communication;

import se.umu.cs.ads.ns.app.Lobby;
import se.umu.cs.ads.ns.app.User;
import se.umu.cs.ads.sp.controller.GameController;

import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;

public class ComHandler {
    private NsClient nsClient = null;
    private GameController gameController = null;
    public ComHandler(GameController controller) {
        this.nsClient = new NsClient(this);
        this.gameController = controller;
    }

    public void createLobby(User user, String name, int maxPlayers){
        this.nsClient.createLobby(user, name, maxPlayers);
    }

    public void fetchPlayersFromLobby(Long lobbyId, User user){
        this.nsClient.fetchPlayersFromLobby(lobbyId, user);
    }

    public CompletableFuture<ArrayList<Lobby>> fetchLobbies() {
        return nsClient.fetchLobbies();
    }

    public void onFetchLobbyPlayersComplete(Lobby lobby, int selectedMap){
        gameController.updateLobbyPage(lobby, selectedMap);
    }
}
