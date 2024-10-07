package se.umu.cs.ads.sp.model.communication;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.protobuf.Empty;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import nsProto.GrpcNamingServiceGrpc;
import nsProto.Lobbies;
import nsProto.LobbyId;
import org.checkerframework.checker.nullness.qual.Nullable;
import proto.PlayerUnit;
import se.umu.cs.ads.ns.app.Lobby;
import se.umu.cs.ads.ns.app.User;
import se.umu.cs.ads.ns.util.NsGrpcUtil;
import se.umu.cs.ads.sp.utils.AppSettings;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class NsClient {
    private ManagedChannel channel = null;
    private GrpcNamingServiceGrpc.GrpcNamingServiceFutureStub stub;
    private ComHandler comHandler;
    private ArrayList<Lobby> lobbies;

    public NsClient(ComHandler handler){
        channel = ManagedChannelBuilder.forAddress(AppSettings.NAMING_SERVICE_IP,
                    AppSettings.NAMING_SERVICE_PORT)
                .usePlaintext()
                .build();
        stub = GrpcNamingServiceGrpc.newFutureStub(channel);
        this.comHandler = handler;
    }

    public void createLobby(User user, String name, int maxPlayers){
        // Create request abd call service
        ListenableFuture<LobbyId> future = stub
                .withDeadlineAfter(2000, TimeUnit.MILLISECONDS)
                .createLobby(NsGrpcUtil.toGrpc(user, name, maxPlayers));

        Futures.addCallback(future, new FutureCallback<LobbyId>() {
            @Override
            public void onSuccess(@Nullable LobbyId lobbyId) {
                if(lobbyId == null){
                    System.out.println("Weird");
                }
                System.out.println("We got success yea " + lobbyId);
            }

            @Override
            public void onFailure(Throwable t) {
                System.out.println("Failed to retrieve player info from '" + AppSettings.NAMING_SERVICE_IP + ":" + AppSettings.NAMING_SERVICE_PORT + "'");
            }

        }, MoreExecutors.directExecutor());

        // Await future completion. Note that the callbacks are triggered on completion.
        try {
            while (!future.isDone()) {
                System.out.println("Awaiting future completion...");
                Thread.sleep(250);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void fetchLobbies(){
        ArrayList<Lobby> returnLobbies = new ArrayList<>();
        ListenableFuture<Lobbies> future = stub
                .withDeadlineAfter(2000, TimeUnit.MILLISECONDS)
                .getLobbies(Empty.newBuilder().build());

        Futures.addCallback(future, new FutureCallback<Lobbies>() {
            @Override
            public void onSuccess(@Nullable Lobbies lobbies) {
                if(lobbies == null){
                    System.out.println("Received null lobbies");
                    return;
                }
                comHandler.onFetchLobbiesComplete(NsGrpcUtil.fromGrpc(lobbies));
            }

            @Override
            public void onFailure(Throwable t) {
                System.out.println("Failed to retrieve player info from '" + AppSettings.NAMING_SERVICE_IP + ":" + AppSettings.NAMING_SERVICE_PORT + "'");
            }

        }, MoreExecutors.directExecutor());

        // Await future completion. Note that the callbacks are triggered on completion.
        try {
            while (!future.isDone()) {
                System.out.println("Awaiting future completion...");
                Thread.sleep(250);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
