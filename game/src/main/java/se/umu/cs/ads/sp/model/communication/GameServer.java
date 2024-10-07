package se.umu.cs.ads.sp.model.communication;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;
import proto.GameServiceGrpc;
import proto.ObjectId;
import proto.PlayerUnit;

import java.io.IOException;

public class GameServer {

    private Server server;

    public GameServer(int port) {
        this.server = ServerBuilder.forPort(port)
                .addService(new GameService())
                .build();
    }

    public void start() {
        try {
            server.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private class GameService extends GameServiceGrpc.GameServiceImplBase {

        @Override
        public void getPlayerUnitInfo(ObjectId request, StreamObserver<PlayerUnit> responseObserver) {
            super.getPlayerUnitInfo(request, responseObserver);
        }
    }

}
