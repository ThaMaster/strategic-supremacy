package se.umu.cs.ads.sp.model.communication.gameCom;

import com.google.protobuf.Empty;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;
import proto.*;
import se.umu.cs.ads.sp.model.communication.ComHandler;
import se.umu.cs.ads.sp.model.communication.GrpcUtil;
import se.umu.cs.ads.sp.model.communication.dto.EntitySkeletonDTO;

import java.io.IOException;
import java.util.ArrayList;

public class GameServer {

    private Server server;
    private ComHandler comHandler;

    public GameServer(int port, ComHandler comHandler) {
        this.server = ServerBuilder.forPort(port)
                .addService(new GameService())
                .build();
        this.comHandler = comHandler;
    }

    public void start() {
        Runtime.getRuntime().addShutdownHook(new Thread(this::stop));
        try {
            server.start();
            System.out.println("[Server] Starting up game server");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private class GameService extends GameServiceGrpc.GameServiceImplBase {

        @Override
        public void startGame(StartGameRequest request, StreamObserver<Empty> responseObserver) {
            comHandler.startGame(GrpcUtil.fromGrpcStartGameReq(request));
            System.out.println("[Server] Received start game request.");
            responseObserver.onNext(Empty.newBuilder().build());
            responseObserver.onCompleted();
        }

        @Override
        public void updateLobby(DetailedLobbyInfo request, StreamObserver<Empty> responseObserver) {
            comHandler.updateLobby(GrpcUtil.fromGrpcLobby(request));
            System.out.println("[Server] Updating the lobby.");
            responseObserver.onNext(Empty.newBuilder().build());
            responseObserver.onCompleted();
        }

        @Override
        public void updatePlayerUnit(PlayerUnits request, StreamObserver<Empty> responseObserver) {
            comHandler.updateEnemyUnits(GrpcUtil.fromGrpcUpdatePlayerUnits(request));
            System.out.println("[Server] Updating enemy units.");
            responseObserver.onNext(Empty.newBuilder().build());
            responseObserver.onCompleted();
        }

        @Override
        public void l3Update(L3Message request, StreamObserver<Empty> responseObserver) {
            System.out.println("[Server] Received L3 update");
            responseObserver.onNext(Empty.newBuilder().build());
            responseObserver.onCompleted();
            comHandler.handleReceiveL3Msg(GrpcUtil.fromGrpcL3Message(request));
        }

        @Override
        public void removePlayerUnits(EntitySkeletons request, StreamObserver<Empty> responseObserver) {
            ArrayList<EntitySkeletonDTO> skeletons = GrpcUtil.fromGrpcEntitySkeletons(request);
            long userId = skeletons.get(0).userId();
            comHandler.removePlayer(userId, new ArrayList<>(skeletons.stream().map(EntitySkeletonDTO::id).toList()));
            System.out.println("[Server] Got request to remove " + skeletons.size() + " units from player with id: " + userId);

            responseObserver.onNext(Empty.newBuilder().build());
            responseObserver.onCompleted();
        }
    }

    private void stop() {
        System.out.println("[Server] Shutting down...");
        if (server != null) {
            server.shutdown();
        }
    }
}
