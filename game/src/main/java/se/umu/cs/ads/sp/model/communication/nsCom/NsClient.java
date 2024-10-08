package se.umu.cs.ads.sp.model.communication.nsCom;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.protobuf.Empty;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import nsProto.DetailedLobbyInfo;
import nsProto.GrpcNamingServiceGrpc;
import nsProto.Lobbies;
import nsProto.LobbyId;
import org.checkerframework.checker.nullness.qual.Nullable;
import se.umu.cs.ads.ns.app.Lobby;
import se.umu.cs.ads.ns.app.User;
import se.umu.cs.ads.ns.util.NsGrpcUtil;
import se.umu.cs.ads.sp.utils.AppSettings;

import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class NsClient {
    private ManagedChannel channel;
    private GrpcNamingServiceGrpc.GrpcNamingServiceFutureStub stub;

    public NsClient() {
        channel = ManagedChannelBuilder.forAddress(AppSettings.NAMING_SERVICE_IP,
                        AppSettings.NAMING_SERVICE_PORT)
                .usePlaintext()
                .build();
        stub = GrpcNamingServiceGrpc.newFutureStub(channel);
    }

    public CompletableFuture<Long> createLobby(User user, String name, int maxPlayers, String selectedMap) {
        CompletableFuture<Long> onComplete = new CompletableFuture<>();
        // Create request abd call service
        ListenableFuture<LobbyId> future = stub
                .withDeadlineAfter(2000, TimeUnit.MILLISECONDS)
                .createLobby(NsGrpcUtil.toGrpc(user, name, maxPlayers, selectedMap));

        Futures.addCallback(future, new FutureCallback<>() {
            @Override
            public void onSuccess(@Nullable LobbyId lobbyId) {
                if (lobbyId == null) {
                    System.out.println("[Client] No Lobby Id Found!");
                } else {
                    onComplete.complete(NsGrpcUtil.fromGrpc(lobbyId));
                    System.out.println("[Client] Created lobby with Id: " + lobbyId);
                }
            }

            @Override
            public void onFailure(Throwable t) {
                System.out.println("[Client] Failed to create new lobby using address '" + AppSettings.NAMING_SERVICE_IP + ":" + AppSettings.NAMING_SERVICE_PORT + "'");
            }

        }, MoreExecutors.directExecutor());

        return onComplete;
    }

    public CompletableFuture<ArrayList<Lobby>> fetchLobbies() {
        CompletableFuture<ArrayList<Lobby>> onComplete = new CompletableFuture<>();
        ListenableFuture<Lobbies> future = stub
                .withDeadlineAfter(2000, TimeUnit.MILLISECONDS)
                .getLobbies(Empty.newBuilder().build());

        Futures.addCallback(future, new FutureCallback<>() {
            @Override
            public void onSuccess(@Nullable Lobbies lobbies) {
                if (lobbies == null) {
                    System.out.println("[Client] Did not find any lobbies!");
                } else {
                    onComplete.complete(NsGrpcUtil.fromGrpc(lobbies));
                }
            }

            @Override
            public void onFailure(Throwable t) {
                System.out.println("[Client] Failed to fetch lobbies from '" + AppSettings.NAMING_SERVICE_IP + ":" + AppSettings.NAMING_SERVICE_PORT + "'");
            }

        }, MoreExecutors.directExecutor());

        return onComplete;
    }

    public CompletableFuture<Lobby> fetchDetailedLobbyInfo(Long lobbyId, User user) {
        CompletableFuture<Lobby> onComplete = new CompletableFuture<>();

        ListenableFuture<DetailedLobbyInfo> future = stub
                .withDeadlineAfter(2000, TimeUnit.MILLISECONDS)
                .joinLobby(NsGrpcUtil.toGrpcJoin(lobbyId, user));

        Futures.addCallback(future, new FutureCallback<>() {
            @Override
            public void onSuccess(@Nullable DetailedLobbyInfo detailedLobbyInfo) {
                if (detailedLobbyInfo != null) {
                    onComplete.complete(NsGrpcUtil.fromGrpc(detailedLobbyInfo));
                }
            }

            @Override
            public void onFailure(Throwable t) {
                System.out.println("[Client] Failed to fetch detailed lobby info from '" + AppSettings.NAMING_SERVICE_IP + ":" + AppSettings.NAMING_SERVICE_PORT + "'");
            }

        }, MoreExecutors.directExecutor());

        return onComplete;
    }

    public void leaveLobby(Long lobbyId, User user) {
        ListenableFuture<Empty> future = stub
                .withDeadlineAfter(2000, TimeUnit.MILLISECONDS)
                .leaveLobby(NsGrpcUtil.toGrpcLeave(lobbyId, user));

        Futures.addCallback(future, new FutureCallback<>() {
            @Override
            public void onSuccess(@Nullable Empty empty) {
                System.out.println("[Client] Successfully left the lobby!");
            }

            @Override
            public void onFailure(Throwable t) {
                System.out.println("[Client] Failed to leave lobby from '" + AppSettings.NAMING_SERVICE_IP + ":" + AppSettings.NAMING_SERVICE_PORT + "'");
            }

        }, MoreExecutors.directExecutor());
    }
}
