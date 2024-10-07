package se.umu.cs.ads.ns.util;

import se.umu.cs.ads.ns.app.Lobby;
import se.umu.cs.ads.ns.app.User;

import java.util.ArrayList;

/**
 * Class that will be used to parse proto classes to java and vise versa.
 */
public class NsGrpcUtil {

    public static User fromGrpc(nsProto.User request){
        return new User(request.getId(), request.getUsername(), request.getIp(), request.getPort());
    }

    public static nsProto.User toGrpc(User user){
        return nsProto.User.newBuilder().setId(user.id).setUsername(user.username).setIp(user.ip).setPort(user.port).build();
    }

    public static nsProto.LobbyId toGrpc(long lobbyId){
        return nsProto.LobbyId.newBuilder().setId(lobbyId).build();
    }

    public static long fromGrpc(nsProto.LobbyId request){
        return request.getId();
    }

    public static nsProto.Lobbies toGrpc(ArrayList<Lobby> lobbies){
        nsProto.Lobbies.Builder builder = nsProto.Lobbies.newBuilder();
        for(Lobby lobby : lobbies){
            builder.addLobbyInfos(toGrpc(lobby));
        }
        return builder.build();
    }

    public static nsProto.LobbyInfo toGrpc(Lobby lobby){
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
    public static nsProto.NewLobby toGrpc(User creator, String name, int maxPlayers){
        return nsProto.NewLobby.newBuilder().
                setLobbyCreator(toGrpc(creator)).
                setLobbyName(name).
                setMaxPlayers(maxPlayers).
                build();
    }


    public static ArrayList<Lobby> fromGrpc(nsProto.Lobbies lobbies){
        ArrayList<Lobby> returnLobbies = new ArrayList<>();
        for(int i = 0; i < lobbies.getLobbyInfosCount(); i++){
            returnLobbies.add((fromGrpc(lobbies.getLobbyInfos(i))));
        }

        return returnLobbies;
    }

    public static Lobby fromGrpc(nsProto.LobbyInfo request){
        Lobby lobby = new Lobby(fromGrpc(request.getId()),request.getLobbyName(), request.getMaxPlayers());
        lobby.currentPlayers = request.getNrPlayers();
        return lobby;
    }

}
