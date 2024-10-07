package se.umu.cs.ads.sp.model.communication;

import se.umu.cs.ads.ns.app.Lobby;
import se.umu.cs.ads.ns.app.User;
import se.umu.cs.ads.sp.controller.GameController;

import java.util.ArrayList;

public class ComHandler {
    private NsClient namingService = null;
    private GameController gameController = null;
    public ComHandler(GameController controller) {
        this.namingService = new NsClient(this);
        this.gameController = controller;
    }

    public void createLobby(User user, String name, int maxPlayers){

        this.namingService.createLobby(user, name, maxPlayers);
    }

    public void fetchLobbies(){
        namingService.fetchLobbies();
    }

    public void onFetchLobbiesComplete(ArrayList<Lobby> lobbies){
        gameController.updateLobbies(lobbies);
    }
}
