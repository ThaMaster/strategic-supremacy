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
            LobbyId lobbyId = blockingStub
                    .withDeadlineAfter(2000, TimeUnit.MILLISECONDS)
                    .createLobby(NsGrpcUtil.toGrpc(user, name, maxPlayers, selectedMap));
            System.out.println("[Client] Created lobby with Id: " + lobbyId);
            return NsGrpcUtil.fromGrpc(lobbyId);
        } catch (Exception e) {
            System.out.println("[Client] Failed to create new lobby using address '" + AppSettings.NAMING_SERVICE_IP + ":" + AppSettings.NAMING_SERVICE_PORT + "'");
            e.printStackTrace();
            return null;
        }
    }

    // Blocking version of fetchLobbies
    public ArrayList<Lobby> fetchLobbies() {
        try {
            Lobbies lobbies = blockingStub
                    .withDeadlineAfter(2000, TimeUnit.MILLISECONDS)
                    .getLobbies(Empty.newBuilder().build());
            if (lobbies == null) {
                System.out.println("[Client] Did not find any lobbies!");
                return new ArrayList<>();
            }
            return NsGrpcUtil.fromGrpc(lobbies);
        } catch (Exception e) {
            System.out.println("[Client] Failed to fetch lobbies from '" + AppSettings.NAMING_SERVICE_IP + ":" + AppSettings.NAMING_SERVICE_PORT + "'");
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    // Blocking version of joinLobby
    public Lobby joinLobby(Long lobbyId, User user) {
        try {
            DetailedLobbyInfo detailedLobbyInfo = blockingStub
                    .withDeadlineAfter(2000, TimeUnit.MILLISECONDS)
                    .joinLobby(NsGrpcUtil.toGrpcJoin(lobbyId, user));
            if (detailedLobbyInfo != null) {
                return NsGrpcUtil.fromGrpc(detailedLobbyInfo);
            } else {
                return null;
            }
        } catch (Exception e) {
            System.out.println("[Client] Failed to join lobby with id: " + lobbyId);
            e.printStackTrace();
            return null;
        }
    }

    // Blocking version of leaveLobby
    public void leaveLobby(Long lobbyId, User user) {
        try {
            System.out.println("[Client] Leaving lobby with id: " + lobbyId);
            blockingStub
                    .withDeadlineAfter(2000, TimeUnit.MILLISECONDS)
                    .leaveLobby(NsGrpcUtil.toGrpcLeave(lobbyId, user));
            System.out.println("[Client] Successfully left the lobby!");
        } catch (Exception e) {
            System.out.println("[Client] Failed to leave lobby from '" + AppSettings.NAMING_SERVICE_IP + ":" + AppSettings.NAMING_SERVICE_PORT + "'");
            e.printStackTrace();
        }
    }
}
