package se.umu.cs.ads.sp.model.communication;

import se.umu.cs.ads.ns.app.Lobby;
import se.umu.cs.ads.ns.app.User;

import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;

public class ComHandler {
    private NsClient nsClient;

    public ComHandler() {
        nsClient = new NsClient();
    }

    public CompletableFuture<Long> createLobby(User user, String name, int maxPlayers, String selectedMap) {
        return nsClient.createLobby(user, name, maxPlayers, selectedMap);
    }

    public void leaveLobby(Long lobbyId, User user) {
        nsClient.leaveLobby(lobbyId, user);
    }

    public CompletableFuture<Lobby> fetchDetailedLobbyInfo(Long lobbyId, User user) {
        return nsClient.fetchDetailedLobbyInfo(lobbyId, user);
    }

    public CompletableFuture<ArrayList<Lobby>> fetchLobbies() {
        return nsClient.fetchLobbies();
    }
}
