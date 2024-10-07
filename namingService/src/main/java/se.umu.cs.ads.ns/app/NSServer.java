package se.umu.cs.ads.ns.app;

import com.google.protobuf.Empty;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;
import nsProto.*;
import se.umu.cs.ads.ns.util.NsGrpcUtil;
import se.umu.cs.ads.ns.util.Util;

import java.util.ArrayList;
import java.util.HashMap;

public class NSServer {
    private Server server;
    private HashMap<Long, Lobby> lobbies = new HashMap<>();
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
            Lobby lobby = new Lobby(request.getLobbyName(), request.getMaxPlayers());
            lobby.addLeader(NsGrpcUtil.fromGrpc(request.getLobbyCreator()));

            lobbies.put(lobby.id, lobby);

            System.out.println("Responding with id = " + lobby.id);
            responseObserver.onNext(NsGrpcUtil.toGrpc(lobby.id));
            responseObserver.onCompleted();
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
            //super.getLobbies(request, responseObserver);
            responseObserver.onNext(NsGrpcUtil.toGrpc(new ArrayList<>(lobbies.values())));
            responseObserver.onCompleted();
        }

        @Override
        public void joinLobby(JoinRequest request, StreamObserver<DetailedLobbyInfo> responseObserver) {
            Lobby lobby = lobbies.get(NsGrpcUtil.fromGrpc(request.getId()));
            lobby.addLeader(NsGrpcUtil.fromGrpc(request.getUser()));
            lobbies.put(lobby.id, lobby);
            responseObserver.onNext(NsGrpcUtil.toGrpc(lobby, 0));
            responseObserver.onCompleted();
        }

    }
}
