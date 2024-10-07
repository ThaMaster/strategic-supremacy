package se.umu.cs.ads.ns.util;

import se.umu.cs.ads.ns.app.Lobby;
import se.umu.cs.ads.ns.app.User;

import java.util.ArrayList;

/**
 * Class that will be used to parse proto classes to java and vise versa.
 */
public class NsGrpcUtil {

    public static User fromGrpc(nsProto.User request) {
        return new User(request.getId(), request.getUsername(), request.getIp(), request.getPort());
    }

    public static nsProto.User toGrpc(User user) {
        return nsProto.User.newBuilder().setId(user.id).setUsername(user.username).setIp(user.ip).setPort(user.port).build();
    }

    public static nsProto.LobbyId toGrpc(Long lobbyId) {
        return nsProto.LobbyId.newBuilder().setId(lobbyId).build();
    }

    public static Long fromGrpc(nsProto.LobbyId request) {
        return request.getId();
    }

    public static nsProto.Lobbies toGrpc(ArrayList<Lobby> lobbies) {
        nsProto.Lobbies.Builder builder = nsProto.Lobbies.newBuilder();
        for (Lobby lobby : lobbies) {
            builder.addLobbyInfos(toGrpc(lobby));
        }
        return builder.build();
    }

    public static nsProto.LobbyInfo toGrpc(Lobby lobby) {
        return nsProto.LobbyInfo.newBuilder().
                setId(toGrpc(lobby.id)).
                setLobbyLeader(toGrpc(lobby.leader)).
                setLobbyName(lobby.name).
                setNrPlayers(lobby.currentPlayers).
                setMaxPlayers(lobby.maxPlayers).build();
    }

    /*
    message NewLobby {
  User lobbyCreator = 1;
  string lobbyName = 2;
  int32 maxPlayers = 3;
}

     */
    public static nsProto.NewLobby toGrpc(User creator, String name, int maxPlayers, int selectedMap) {
        return nsProto.NewLobby.newBuilder().
                setLobbyCreator(toGrpc(creator)).
                setLobbyName(name).
                setMaxPlayers(maxPlayers).
                setSelectedMap(selectedMap).
                build();
    }


    public static ArrayList<Lobby> fromGrpc(nsProto.Lobbies lobbies) {
        ArrayList<Lobby> returnLobbies = new ArrayList<>();
        for (int i = 0; i < lobbies.getLobbyInfosCount(); i++) {
            returnLobbies.add((fromGrpc(lobbies.getLobbyInfos(i))));
        }

        return returnLobbies;
    }

    public static Lobby fromGrpc(nsProto.LobbyInfo request) {
        Lobby lobby = new Lobby(fromGrpc(request.getId()), request.getLobbyName(), request.getMaxPlayers());
        lobby.currentPlayers = request.getNrPlayers();
        return lobby;
    }

    public static nsProto.DetailedLobbyInfo toGrpc(Lobby lobby, int selectedMap) {
        nsProto.DetailedLobbyInfo.Builder builder = nsProto.DetailedLobbyInfo.newBuilder();
        for (User user : lobby.users) {
            builder.addUsers(toGrpc(user));
        }

        builder.setLeader(toGrpc(lobby.leader));
        builder.setSelectedMap(selectedMap);
        builder.setMaxPlayers(lobby.maxPlayers);
        builder.setLobbyName(lobby.name);
        return builder.build();
    }

    public static nsProto.JoinRequest toGrpcJoin(Long id, User user) {
        nsProto.JoinRequest.Builder builder = nsProto.JoinRequest.newBuilder();
        builder.setId(toGrpc(id));
        builder.setUser(toGrpc(user));
        return builder.build();
    }

    public static nsProto.LeaveRequest toGrpcLeave(Long id, User user) {
        nsProto.LeaveRequest.Builder builder = nsProto.LeaveRequest.newBuilder();
        builder.setId(toGrpc(id));
        builder.setUser(toGrpc(user));
        return builder.build();
    }

    public static Lobby fromGrpc(nsProto.DetailedLobbyInfo request) {
        Lobby lobby = new Lobby(request.getLobbyName(), request.getMaxPlayers());
        ArrayList<User> users = new ArrayList<>();
        lobby.leader = fromGrpc(request.getLeader());
        for (nsProto.User user : request.getUsersList()) {
            users.add(fromGrpc(user));
        }
        lobby.setUsers(users);
        lobby.selectedMap = request.getSelectedMap();
        return lobby;
    }
}


