package se.umu.cs.ads.sp.model.communication.nsCom;

import com.google.protobuf.Empty;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;
import nsProto.DetailedLobbyInfo;
import nsProto.GrpcNamingServiceGrpc;
import se.umu.cs.ads.ns.app.Lobby;
import se.umu.cs.ads.ns.util.NsGrpcUtil;

public class NsServer {
    private Server server;

    public NsServer(int port) {
        this.server = ServerBuilder
                .forPort(port)
                .addService(new GrpcNamingService())
                .build();
    }

    public void start() {
        Runtime.getRuntime().addShutdownHook(new Thread(this::stop));
        try {
            server.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void stop() {
        if (server != null) {
            server.shutdown();
        }
    }

    public class GrpcNamingService extends GrpcNamingServiceGrpc.GrpcNamingServiceImplBase {
        @Override
        public void updateLobby(DetailedLobbyInfo request, StreamObserver<Empty> responseObserver) {
            System.out.println("[Client] Received to update the lobby!");
            Lobby updateLobby = NsGrpcUtil.fromGrpc(request);
            System.out.println("\t Number of clients: " + updateLobby.users.size());
        }
    }
}
