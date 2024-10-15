package se.umu.cs.ads.sp.model.communication.gameCom;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.protobuf.Empty;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.checkerframework.checker.nullness.qual.Nullable;
import proto.GameServiceGrpc;
import se.umu.cs.ads.ns.app.Lobby;
import se.umu.cs.ads.ns.app.User;
import se.umu.cs.ads.sp.model.communication.GrpcUtil;
import se.umu.cs.ads.sp.model.communication.dto.EntitySkeletonDTO;
import se.umu.cs.ads.sp.model.communication.dto.PlayerUnitUpdateRequest;
import se.umu.cs.ads.sp.model.communication.dto.StartGameRequest;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class GameClient {

    private ManagedChannel channel = null;
    private GameServiceGrpc.GameServiceFutureStub stub;

    private String ip;
    private int port;
    private String username;

    public GameClient() {
        super();
    }

    public void create(String ip, int port) {
        this.ip = ip;
        this.port = port;
        channel = ManagedChannelBuilder.forAddress(ip, port)
                .usePlaintext()
                .build();
        stub = GameServiceGrpc.newFutureStub(channel);
    }

    public void destroy() {
        // Shut down the channel.
        try {
            channel.shutdown();
            while (!channel.awaitTermination(2000, TimeUnit.MILLISECONDS)) ;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void startGame(StartGameRequest req) {
        ListenableFuture<Empty> future = stub
                .withDeadlineAfter(2000, TimeUnit.MILLISECONDS)
                .startGame(GrpcUtil.toGrpcStartGameReq(req));

        Futures.addCallback(future, new FutureCallback<>() {

            @Override
            public void onSuccess(@Nullable Empty result) {
                System.out.println("[Client] Successfully told a player to start game");
            }

            @Override
            public void onFailure(Throwable t) {
                System.out.println("[Client] Failed to tell player to start ze game");
                System.out.println("\t" + t.getMessage());
            }
        }, MoreExecutors.directExecutor());
    }

    public void updateUnits(PlayerUnitUpdateRequest playerUnitUpdateRequest) {
        System.out.println("[Client] Trying to update enemy units...");
        ListenableFuture<Empty> future = stub
                .withDeadlineAfter(2000, TimeUnit.MILLISECONDS)
                .updatePlayerUnit(GrpcUtil.toGrpcUpdatePlayerUnits(playerUnitUpdateRequest));

        Futures.addCallback(future, new FutureCallback<>() {
            @Override
            public void onSuccess(@Nullable Empty result) {
                System.out.println("\t Successfully updated");

            }

            @Override
            public void onFailure(Throwable t) {
                System.out.println("\t Failed to update enemy units...");
                System.out.println("\t" + t.getMessage());
            }
        }, MoreExecutors.directExecutor());
    }

    public void updateLobby(Lobby updatedLobby, User currentUser) {
        System.out.println("[Client] Trying to update client " + currentUser.ip + ":" + currentUser.port);
        ListenableFuture<Empty> future = stub
                .withDeadlineAfter(2000, TimeUnit.MILLISECONDS)
                .updateLobby(GrpcUtil.toGrpcLobby(updatedLobby, updatedLobby.selectedMap));

        Futures.addCallback(future, new FutureCallback<>() {
            @Override
            public void onSuccess(@Nullable Empty result) {
                System.out.println("\t Successfully update client " + currentUser.ip + ":" + currentUser.port);
            }

            @Override
            public void onFailure(Throwable t) {
                System.out.println("[Client] Failed to update client " + currentUser.ip + ":" + currentUser.port);
                System.out.println("\t" + t.getMessage());
            }
        }, MoreExecutors.directExecutor());
    }

    public void removePlayerUnits(ArrayList<EntitySkeletonDTO> entitySkeletons) {
        System.out.println("[Client] Trying to leave ongoing game...");
        ListenableFuture<Empty> future = stub
                .withDeadlineAfter(2000, TimeUnit.MILLISECONDS)
                .removePlayerUnits(GrpcUtil.toGrpcEntitySkeletons(entitySkeletons));
        Futures.addCallback(future, new FutureCallback<>() {
            @Override
            public void onSuccess(@Nullable Empty result) {
                System.out.println("\t Successfully left the game");

            }

            @Override
            public void onFailure(Throwable t) {
                System.out.println("\t Failed to leave the ongoing game.");
                System.out.println("\t" + t.getMessage());
            }

        }, MoreExecutors.directExecutor());
    }

    public void shutdown() {
        channel.shutdown();
        try {
            while (!channel.awaitTermination(2000, TimeUnit.MILLISECONDS)) ;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
