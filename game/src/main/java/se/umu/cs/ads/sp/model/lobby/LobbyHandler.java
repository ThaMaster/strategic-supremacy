package se.umu.cs.ads.sp.model.lobby;

import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import se.umu.cs.ads.ns.app.Lobby;
import se.umu.cs.ads.ns.app.User;
import se.umu.cs.ads.sp.model.ModelManager;
import se.umu.cs.ads.sp.util.enums.LobbyClientState;

import java.util.ArrayList;

public class LobbyHandler {

    private Lobby lobby;
    private final ModelManager modelManager;
    private Raft raft;

    private Status status;

    public LobbyHandler(ModelManager modelManager) {
        this.modelManager = modelManager;
    }

    public void createLobby(String lobbyName, int maxPlayers, String selectedMap) {
        long lobbyId;
        try {
            lobbyId = modelManager.getComHandler().createLobby(modelManager.getPlayer(), lobbyName, maxPlayers, selectedMap);
        } catch (StatusRuntimeException e) {
            status = e.getStatus();
            return;
        }

        lobby = new Lobby(lobbyId, lobbyName, maxPlayers);
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
        try {
            return modelManager.getComHandler().fetchLobbies();
        } catch (StatusRuntimeException e) {
            status = e.getStatus();
            return null;
        }
    }

    public Lobby joinLobby(long lobbyId) {
        raft = new Raft(this, LobbyClientState.FOLLOWER, modelManager.getComHandler());
        try {
            lobby = modelManager.getComHandler().joinLobby(lobbyId, modelManager.getPlayer());
        } catch (StatusRuntimeException e) {
            status = e.getStatus();
            return null;
        }
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

    public String getErrorMessage() {
        String errorMsg;
        if (status.getCode() == Status.Code.PERMISSION_DENIED) {
            errorMsg = "Error: Lobby has already started.";
        } else if (status.getCode() == Status.Code.RESOURCE_EXHAUSTED) {
            errorMsg = "Error: Lobby is full. ";
        } else if (status.getCode() == Status.Code.UNAVAILABLE) {
            errorMsg = "Error: Could not connect to NamingService. \n(Maybe wrong ip/port or service not started?) ";
        } else {
            errorMsg = "Error: An unexpected error occurred: \nCode: " + status.getCode().name() + "\nDescription: " + status.getDescription();
        }
        return errorMsg;
    }

    public boolean hasErrorOccurred() {
        return status != null;
    }

    public void clearErrors() {
        this.status = null;
    }
}
