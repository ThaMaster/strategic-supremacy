package se.umu.cs.ads.sp.model;

import io.grpc.internal.JsonUtil;
import se.umu.cs.ads.ns.app.Lobby;
import se.umu.cs.ads.sp.controller.GameController;

import javax.swing.*;
import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;

public class LobbyHandler {

    private Lobby lobby;
    private ModelManager modelManager;
    private String[][] fetchedLobbies;

    public LobbyHandler(ModelManager modelManager){
        this.modelManager = modelManager;
    }


    public void createLobby(String lobbyName, int maxPlayers, String selectedMap){
        long id = modelManager.getComHandler().createLobby(modelManager.getPlayer(), lobbyName, maxPlayers, selectedMap);
        lobby = new Lobby(id, lobbyName, maxPlayers);
        lobby.selectedMap = selectedMap;
    }

    public void leaveLobby(){
        modelManager.getComHandler().leaveLobby(this.lobby.id, modelManager.getPlayer());
    }

    public ArrayList<Lobby> fetchLobbies(){
        return modelManager.getComHandler().fetchLobbies();
    }

    public Lobby joinLobby(long lobbyId){
        lobby = modelManager.getComHandler().joinLobby(lobbyId, modelManager.getPlayer());
        return lobby;
    }
}
