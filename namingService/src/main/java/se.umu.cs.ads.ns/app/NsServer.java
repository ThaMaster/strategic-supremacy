package se.umu.cs.ads.ns.app;

import com.google.protobuf.Empty;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;
import nsProto.*;
import se.umu.cs.ads.ns.util.NsGrpcUtil;

import java.util.ArrayList;
import java.util.HashMap;

public class NsServer {
    private Server server;
    private HashMap<Long, Lobby> lobbies = new HashMap<>();

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
            User leader = NsGrpcUtil.fromGrpc(request.getLobbyCreator());
            Lobby lobby = new Lobby(leader, request.getLobbyName(), request.getMaxPlayers());
            lobby.selectedMap = request.getSelectedMap();
            lobby.started = false;
            lobby.messageCount = 0;
            lobbies.put(lobby.id, lobby);

            System.out.println("[Server] New lobby created, responding with id: " + lobby.id);
            responseObserver.onNext(NsGrpcUtil.toGrpc(lobby.id));
            responseObserver.onCompleted();
        }

        @Override
        public void getLobbies(Empty request, StreamObserver<Lobbies> responseObserver) {
            //super.getLobbies(request, responseObserver);
            System.out.println("[Server] Fetching all lobbies...");
            System.out.println("\t Found " + lobbies.values().size() + " lobbies");
            responseObserver.onNext(NsGrpcUtil.toGrpc(new ArrayList<>(lobbies.values())));
            responseObserver.onCompleted();
        }

        @Override
        public void joinLobby(JoinRequest request, StreamObserver<DetailedLobbyInfo> responseObserver) {
            Lobby lobby = lobbies.get(NsGrpcUtil.fromGrpc(request.getId()));
            User joiningUser = NsGrpcUtil.fromGrpc(request.getUser());
            System.out.println("[Server] User " + joiningUser.id + " joining lobby " + NsGrpcUtil.fromGrpc(request.getId()) + "...");

            if (lobby.started) {
                System.out.println("\t Lobby has already started! Denying user...");
                StatusRuntimeException exception = Status.PERMISSION_DENIED
                        .withDescription("Lobby already started, cannot accept new players.")
                        .asRuntimeException();
                responseObserver.onError(exception);
                return;
            }

            if (lobby.currentPlayers >= lobby.maxPlayers) {
                System.out.println("\t Lobby is full! Denying user...");
                StatusRuntimeException exception = Status.RESOURCE_EXHAUSTED
                        .withDescription("Lobby is full, cannot accept more players.")
                        .asRuntimeException();
                responseObserver.onError(exception);
                return;
            }
            if (!lobby.users.contains(joiningUser)) {
                System.out.println("\t Successfully joined!");
                lobby.addUser(NsGrpcUtil.fromGrpc(request.getUser()));
                lobby.messageCount++;
            } else {
                System.out.println("\t User already exists!");
            }
            responseObserver.onNext(NsGrpcUtil.toGrpcDetailedLobby(lobby));
            responseObserver.onCompleted();
        }

        @Override
        public void leaveLobby(LeaveRequest request, StreamObserver<nsProto.User> responseObserver) {
            Lobby lobby = lobbies.get(NsGrpcUtil.fromGrpc(request.getId()));
            User leavingUser = NsGrpcUtil.fromGrpc(request.getUser());
            System.out.println("[Server] User " + leavingUser.id + " leaving lobby " + NsGrpcUtil.fromGrpc(request.getId()) + "...");
            if (leavingUser.id == lobby.leader.id) {
                // Change the leader of the lobby!
                System.out.println("\t User was the leader, selecting new leader.");
                lobby.removeUser(leavingUser);
                if (lobby.currentPlayers != 0) {
                    lobby.leader = lobby.users.get(0);
                }
            } else if (lobby.hasUser(leavingUser)) {
                System.out.println("\t Removed the user.");
                lobby.removeUser(leavingUser);
            }

            if (lobby.currentPlayers == 0) {
                System.out.println("\t Lobby became empty, removing.");
                lobbies.remove(lobby.id);
            } else {
                lobby.messageCount++;
            }
            responseObserver.onNext(NsGrpcUtil.toGrpc(lobby.leader));
            responseObserver.onCompleted();
        }

        @Override
        public void startLobby(LobbyId request, StreamObserver<Empty> responseObserver) {
            Lobby lobby = lobbies.get(request.getId());
            lobby.started = true;
            lobby.messageCount++;
            responseObserver.onNext(Empty.newBuilder().build());
            responseObserver.onCompleted();
        }
    }
}
