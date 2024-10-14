package se.umu.cs.ads.sp.model.communication.gameCom;

import com.google.protobuf.Empty;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;
import nsProto.GrpcNamingServiceGrpc;
import proto.*;
import se.umu.cs.ads.ns.util.NsGrpcUtil;
import se.umu.cs.ads.sp.model.ModelManager;
import se.umu.cs.ads.sp.model.communication.ComHandler;
import se.umu.cs.ads.sp.model.communication.GrpcUtil;

import java.io.IOException;

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
        try {
            server.start();
            System.out.println("Starting up server");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private class GameService extends GameServiceGrpc.GameServiceImplBase {

        @Override
        public void getPlayerUnitInfo(ObjectId request, StreamObserver<PlayerUnit> responseObserver) {
            super.getPlayerUnitInfo(request, responseObserver);
        }

        @Override
        public void startGame(StartGameRequest request, StreamObserver<Empty> responseObserver) {
            comHandler.startGame(GrpcUtil.fromGrpcStartGameReq(request));
            responseObserver.onNext(Empty.newBuilder().build());
            responseObserver.onCompleted();
        }

        @Override
        public void updateLobby(DetailedLobbyInfo request, StreamObserver<Empty> responseObserver) {
            comHandler.updateLobby(GrpcUtil.fromGrpcLobby(request));
            responseObserver.onNext(Empty.newBuilder().build());
            responseObserver.onCompleted();
        }
    }
}
