package se.umu.cs.ads.ns.app;

import com.google.protobuf.Empty;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;
import nsProto.*;

public class NSServer {
    private Server server;

    public NSServer(int port) {
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

    // Blocking call to keep the server running
    public void blockUntilShutdown() {
        if (server != null) {
            try {
                server.awaitTermination();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public class GrpcNamingService extends GrpcNamingServiceGrpc.GrpcNamingServiceImplBase {
        @Override
        public void createLobby(NewLobby request, StreamObserver<LobbyId> responseObserver) {
            super.createLobby(request, responseObserver);
        }

        @Override
        public void deleteLobby(LobbyId request, StreamObserver<Empty> responseObserver) {
            super.deleteLobby(request, responseObserver);
        }

        @Override
        public void getLobbyInfo(LobbyId request, StreamObserver<LobbyInfo> responseObserver) {
            super.getLobbyInfo(request, responseObserver);
        }

        @Override
        public void getLobbies(Empty request, StreamObserver<Lobbies> responseObserver) {
            super.getLobbies(request, responseObserver);
        }
    }
}