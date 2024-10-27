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
        return nsProto.User.newBuilder()
                .setId(user.id)
                .setUsername(user.username)
                .setIp(user.ip)
                .setPort(user.port)
                .build();
    }

    public static nsProto.LobbyId toGrpc(Long lobbyId) {
        return nsProto.LobbyId.newBuilder()
                .setId(lobbyId)
                .build();
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
        return nsProto.LobbyInfo.newBuilder()
                .setId(toGrpc(lobby.id))
                .setLobbyLeader(toGrpc(lobby.leader))
                .setLobbyName(lobby.name)
                .setNrPlayers(lobby.currentPlayers)
                .setMaxPlayers(lobby.maxPlayers)
                .setStarted(lobby.started)
                .build();
    }

    public static nsProto.NewLobby toGrpc(User creator, String name, int maxPlayers, String selectedMap) {
        return nsProto.NewLobby.newBuilder()
                .setLobbyCreator(toGrpc(creator))
                .setLobbyName(name)
                .setMaxPlayers(maxPlayers)
                .setSelectedMap(selectedMap)
                .build();
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
        lobby.started = request.getStarted();
        return lobby;
    }

    public static nsProto.JoinRequest toGrpcJoin(Long id, User user) {
        return nsProto.JoinRequest.newBuilder()
                .setId(toGrpc(id))
                .setUser(toGrpc(user))
                .build();
    }

    public static nsProto.LeaveRequest toGrpcLeave(Long id, User user) {
        return nsProto.LeaveRequest.newBuilder()
                .setId(toGrpc(id))
                .setUser(toGrpc(user))
                .build();
    }

    public static nsProto.DetailedLobbyInfo toGrpcDetailedLobby(Lobby lobby) {
        nsProto.DetailedLobbyInfo.Builder builder = nsProto.DetailedLobbyInfo.newBuilder()
                .setId((lobby.id))
                .setLeader(toGrpc(lobby.leader))
                .setSelectedMap(lobby.selectedMap)
                .setMaxPlayers(lobby.maxPlayers)
                .setLobbyName(lobby.name)
                .setMessageCount(lobby.messageCount);
        for (User user : lobby.users) {
            builder.addUsers(toGrpc(user));
        }
        return builder.build();
    }

    public static Lobby fromGrpcDetailedLobby(nsProto.DetailedLobbyInfo request) {
        Lobby lobby = new Lobby(
                request.getId(),
                request.getLobbyName(),
                fromGrpc(request.getLeader()),
                request.getUsersCount(),
                request.getMaxPlayers(),
                request.getSelectedMap());

        ArrayList<User> users = new ArrayList<>();
        for (nsProto.User user : request.getUsersList()) {
            users.add(fromGrpc(user));
        }

        lobby.setMessageCount(request.getMessageCount());
        lobby.setUsers(users);
        return lobby;
    }
}


