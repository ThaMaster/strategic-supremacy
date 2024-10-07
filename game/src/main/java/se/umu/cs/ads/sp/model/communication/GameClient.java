package se.umu.cs.ads.sp.model.communication;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.MoreExecutors;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.checkerframework.checker.nullness.qual.Nullable;
import proto.GameServiceGrpc;
import proto.PlayerUnit;

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
}
