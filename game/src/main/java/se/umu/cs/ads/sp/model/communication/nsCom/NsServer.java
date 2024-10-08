package se.umu.cs.ads.sp.model.communication.nsCom;

import com.google.protobuf.Empty;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;
import nsProto.DetailedLobbyInfo;
import nsProto.GrpcNamingServiceGrpc;
import se.umu.cs.ads.ns.util.NsGrpcUtil;
import se.umu.cs.ads.sp.model.communication.ComHandler;

public class NsServer {
    private Server server;
    private ComHandler comHandler;

    public NsServer(int port, ComHandler comHandler) {
        this.comHandler = comHandler;
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
            System.out.println("[Client] Shutting down the client server to NS!");
            server.shutdown();
        }
    }

    public class GrpcNamingService extends GrpcNamingServiceGrpc.GrpcNamingServiceImplBase {
        @Override
        public void updateLobby(DetailedLobbyInfo request, StreamObserver<Empty> responseObserver) {
            comHandler.updateLobby(NsGrpcUtil.fromGrpc(request));
            responseObserver.onNext(Empty.newBuilder().build());
            responseObserver.onCompleted();
        }
    }
}
