package se.umu.cs.ads.sp.model.communication;

import se.umu.cs.ads.ns.app.Lobby;
import se.umu.cs.ads.ns.app.User;
import se.umu.cs.ads.sp.controller.GameController;
import se.umu.cs.ads.sp.model.communication.nsCom.NsClient;
import se.umu.cs.ads.sp.model.communication.nsCom.NsServer;

import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;

public class ComHandler {
    private NsClient nsClient;
    private NsServer nsServer;
    private GameController controller;

    public ComHandler(int port, GameController controller) {
        nsClient = new NsClient();
        nsServer = new NsServer(port, this);
        nsServer.start();
        this.controller = controller;
    }

    public CompletableFuture<Long> createLobby(User user, String name, int maxPlayers, String selectedMap) {
        return nsClient.createLobby(user, name, maxPlayers, selectedMap);
    }

    public void leaveLobby(Long lobbyId, User user) {
        nsClient.leaveLobby(lobbyId, user);
    }

    public void updateLobby(Lobby updatedLobby) {
        String[][] lobbyData = new String[updatedLobby.users.size()][];
        for (int i = 0; i < updatedLobby.users.size(); i++) {
            lobbyData[i] = new String[]{
                    String.valueOf(updatedLobby.users.get(i).id),
                    updatedLobby.users.get(i).username,
            };
        }
        controller.updateLobby(updatedLobby.name, lobbyData, updatedLobby.currentPlayers, updatedLobby.maxPlayers, updatedLobby.selectedMap);
    }

    public CompletableFuture<Lobby> joinLobby(Long lobbyId, User user) {
        return nsClient.joinLobby(lobbyId, user);
    }

    public CompletableFuture<ArrayList<Lobby>> fetchLobbies() {
        return nsClient.fetchLobbies();
    }
}
