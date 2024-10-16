package se.umu.cs.ads.sp.model.communication.nsCom;

import com.google.protobuf.Empty;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import nsProto.DetailedLobbyInfo;
import nsProto.GrpcNamingServiceGrpc;
import nsProto.Lobbies;
import nsProto.LobbyId;
import se.umu.cs.ads.ns.app.Lobby;
import se.umu.cs.ads.ns.app.User;
import se.umu.cs.ads.ns.util.NsGrpcUtil;
import se.umu.cs.ads.sp.utils.AppSettings;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class NsClient {
    private ManagedChannel channel;
    private GrpcNamingServiceGrpc.GrpcNamingServiceBlockingStub blockingStub;

    public NsClient() {
        channel = ManagedChannelBuilder.forAddress(AppSettings.NAMING_SERVICE_IP,
                        AppSettings.NAMING_SERVICE_PORT)
                .usePlaintext()
                .build();
        blockingStub = GrpcNamingServiceGrpc.newBlockingStub(channel);
    }

    // Blocking version of createLobby
    public Long createLobby(User user, String name, int maxPlayers, String selectedMap) {
        try {
            System.out.println("[Client] Trying to create new lobby...");
            LobbyId lobbyId = blockingStub
                    .createLobby(NsGrpcUtil.toGrpc(user, name, maxPlayers, selectedMap));
            System.out.println("\t Created lobby with Id: " + lobbyId);
            return NsGrpcUtil.fromGrpc(lobbyId);
        } catch (Exception e) {
            System.out.println("\t Failed to create new lobby.");
            e.printStackTrace();
            return null;
        }
    }

    // Blocking version of fetchLobbies
    public ArrayList<Lobby> fetchLobbies() {
        System.out.println("[Client] Trying to fetch lobbies...");
        try {
            Lobbies lobbies = blockingStub
                    .getLobbies(Empty.newBuilder().build());
            if (lobbies == null) {
                System.out.println("\t Could not find any lobbies.");
                return new ArrayList<>();
            }
            System.out.println("\t Found lobbies.");
            return NsGrpcUtil.fromGrpc(lobbies);
        } catch (Exception e) {
            System.out.println("\t Failed to fetch lobbies.");
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    // Blocking version of joinLobby
    public Lobby joinLobby(Long lobbyId, User user) {
        System.out.println("[Client] Trying to join lobby with id: " + lobbyId);
        try {
            DetailedLobbyInfo detailedLobbyInfo = blockingStub
                    .joinLobby(NsGrpcUtil.toGrpcJoin(lobbyId, user));
            if (detailedLobbyInfo == null) {
                System.out.println("\t No lobby found with given id.");
                return null;
            }
            System.out.println("\t Joined the lobby.");
            return NsGrpcUtil.fromGrpcDetailedLobby(detailedLobbyInfo);
        } catch (Exception e) {
            System.out.println("\t Failed to join lobby.");
            e.printStackTrace();
            return null;
        }
    }

    // Blocking version of leaveLobby
    public User leaveLobby(Long lobbyId, User user) {
        try {
            System.out.println("[Client] Leaving lobby with id: " + lobbyId);
            nsProto.User leader = blockingStub
                    .leaveLobby(NsGrpcUtil.toGrpcLeave(lobbyId, user));
            System.out.println("\t Left the lobby.");

            return NsGrpcUtil.fromGrpc(leader);
        } catch (Exception e) {
            System.out.println("\t Failed to leave lobby.");
            e.printStackTrace();
        }
        return null;
    }
}
