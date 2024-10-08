package se.umu.cs.ads.ns.app;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.protobuf.Empty;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import nsProto.GrpcNamingServiceGrpc;
import org.checkerframework.checker.nullness.qual.Nullable;
import se.umu.cs.ads.ns.util.NsGrpcUtil;

import java.util.concurrent.TimeUnit;

public class NsClient {

    private ManagedChannel channel;
    private GrpcNamingServiceGrpc.GrpcNamingServiceFutureStub stub;

    public NsClient(String ip, int port) {
        channel = ManagedChannelBuilder.forAddress(ip, port)
                .usePlaintext()
                .build();
        stub = GrpcNamingServiceGrpc.newFutureStub(channel);
    }

    public void updateLobby(Lobby updatedLobby, User currentUser) {
        System.out.println("[Server] Trying to update client " + currentUser.ip + ":" + currentUser.port);
        ListenableFuture<Empty> future = stub
                .withDeadlineAfter(2000, TimeUnit.MILLISECONDS)
                .updateLobby(NsGrpcUtil.toGrpc(updatedLobby, updatedLobby.selectedMap));

        Futures.addCallback(future, new FutureCallback<>() {

            @Override
            public void onSuccess(@Nullable Empty result) {
                System.out.println("[Server] Successfully update client " + currentUser.ip + ":" + currentUser.port);
            }

            @Override
            public void onFailure(Throwable t) {

            }
        }, MoreExecutors.directExecutor());
    }
}
