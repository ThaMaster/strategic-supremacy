package se.umu.cs.ads.sp.model.communication.gameCom;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.protobuf.Empty;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.checkerframework.checker.nullness.qual.Nullable;
import proto.CandidateLeaderResponse;
import proto.ErrorOccurred;
import proto.GameServiceGrpc;
import se.umu.cs.ads.ns.app.Lobby;
import se.umu.cs.ads.ns.app.User;
import se.umu.cs.ads.sp.model.ModelManager;
import se.umu.cs.ads.sp.model.communication.GrpcUtil;
import se.umu.cs.ads.sp.model.communication.dto.*;
import se.umu.cs.ads.sp.performance.ConsistencyTest;
import se.umu.cs.ads.sp.performance.TestLogger;
import se.umu.cs.ads.sp.util.AppSettings;

import java.util.concurrent.TimeUnit;

public class GameClient {

    private ManagedChannel channel = null;
    private GameServiceGrpc.GameServiceFutureStub stub;

    private String ip;
    private int port;
    private String username;
    private ModelManager modelManager;

    public GameClient() {
        super();
    }

    public String getIp() {
        return ip;
    }

    public int getPort() {
        return port;
    }

    public String getUsername() {
        return username;
    }

    public void create(String ip, int port, String username, ModelManager modelManager) {
        this.ip = ip;
        this.port = port;
        this.username = username;
        channel = ManagedChannelBuilder.forAddress(ip, port)
                .usePlaintext()
                .build();
        stub = GameServiceGrpc.newFutureStub(channel);
        this.modelManager = modelManager;
    }

    @Override
    public String toString() {
        return ip + ":" + port;
    }

    public void startGame(StartGameRequestDTO req) {
        ListenableFuture<Empty> future = stub
                .withDeadlineAfter(4000, TimeUnit.MILLISECONDS)
                .startGame(GrpcUtil.toGrpcStartGameReq(req));

        Futures.addCallback(future, new FutureCallback<>() {

            @Override
            public void onSuccess(@Nullable Empty result) {
                System.out.println("\t Told a player to start game");
            }

            @Override
            public void onFailure(Throwable t) {
                System.out.println("\t Failed to tell player to start the game");
                System.out.println("\t" + t.getMessage());
            }
        }, MoreExecutors.directExecutor());
    }

    public void sendDefeatUpdate(long userId) {
        ListenableFuture<Empty> future = stub
                .withDeadlineAfter(4000, TimeUnit.MILLISECONDS)
                .defeatUpdate(GrpcUtil.toGrpcUserId(userId));

        Futures.addCallback(future, new FutureCallback<>() {
            @Override
            public void onSuccess(@Nullable Empty result) {
            }

            @Override
            public void onFailure(Throwable t) {
                System.out.println("\t Failed to send defeat update to client " + ip + ":" + port);
                System.out.println("\t" + t.getMessage());
            }
        }, MoreExecutors.directExecutor());
    }

    public void sendL3Message(L3UpdateDTO msg, long perfId) {
        ListenableFuture<Empty> future = stub
                .withDeadlineAfter(4000, TimeUnit.MILLISECONDS)
                .l3Update(GrpcUtil.toGrpcL3Message(msg));
        Futures.addCallback(future, new FutureCallback<>() {
            @Override
            public void onSuccess(@Nullable Empty result) {
                if (AppSettings.RUN_PERFORMANCE_TEST && perfId != -1) {
                    TestLogger.setFinished(perfId);
                }
            }

            @Override
            public void onFailure(Throwable t) {
                System.out.println("\t Failed to send L3 update to client " + ip + ":" + port);
                System.out.println("\t" + t.getMessage());
            }
        }, MoreExecutors.directExecutor());
    }

    public void updateEntityState(EntityStateDTO dto) {
        ListenableFuture<Empty> future = stub
                .withDeadlineAfter(4000, TimeUnit.MILLISECONDS)
                .updateEntityState(GrpcUtil.toGrpcEntityState(dto));
        Futures.addCallback(future, new FutureCallback<>() {
            @Override
            public void onSuccess(@Nullable Empty result) {

            }

            @Override
            public void onFailure(Throwable t) {
                System.out.println("\t Failed to send L3 update to client " + ip + ":" + port);
                System.out.println("\t" + t.getMessage());
            }
        }, MoreExecutors.directExecutor());
    }

    public void sendL2Message(L2UpdateDTO msg, Long perfId) {
        ListenableFuture<Empty> future = stub
                .withDeadlineAfter(4000, TimeUnit.MILLISECONDS)
                .l2Update(GrpcUtil.toGrpcL2Message(msg));
        Futures.addCallback(future, new FutureCallback<>() {
            @Override
            public void onSuccess(@Nullable Empty result) {
                if (AppSettings.RUN_PERFORMANCE_TEST && perfId != -1) {
                    TestLogger.setFinished(perfId);
                }
            }

            @Override
            public void onFailure(Throwable t) {
                System.out.println("\t Failed to send L2 update to client " + ip + ":" + port);
                System.out.println("\t" + t.getMessage());
            }
        }, MoreExecutors.directExecutor());
    }

    public void sendL1Message(L1UpdateDTO msg, Long perfId, Long cId) {
        ListenableFuture<ErrorOccurred> future = stub
                .withDeadlineAfter(4000, TimeUnit.MILLISECONDS)
                .l1Update(GrpcUtil.toGrpcL1Message(msg));

        Futures.addCallback(future, new FutureCallback<>() {
            @Override
            public void onSuccess(@Nullable ErrorOccurred error) {
                if (AppSettings.RUN_PERFORMANCE_TEST && perfId != -1) {
                    TestLogger.setFinished(perfId);
                }

                if (AppSettings.RUN_PERFORMANCE_TEST && cId != -1) {
                    ConsistencyTest cTest = (ConsistencyTest) TestLogger.getTest(cId);
                    if (error != null && error.getError()) {
                        cTest.addError();
                    }
                }

            }

            @Override
            public void onFailure(Throwable t) {
                System.out.println("\t Failed to send L1 update to client " + ip + ":" + port);
                System.out.println("\t" + t.getMessage());
            }
        }, MoreExecutors.directExecutor());
    }

    public void updateLobby(Lobby updatedLobby, User currentUser) {
        System.out.println("[Client] Trying to update client " + currentUser.ip + ":" + currentUser.port);
        ListenableFuture<Empty> future = stub
                .withDeadlineAfter(4000, TimeUnit.MILLISECONDS)
                .updateLobby(GrpcUtil.toGrpcLobby(updatedLobby));

        Futures.addCallback(future, new FutureCallback<>() {
            @Override
            public void onSuccess(@Nullable Empty result) {
                System.out.println("\t Updated client " + currentUser.ip + ":" + currentUser.port);
            }

            @Override
            public void onFailure(Throwable t) {
                System.out.println("\t Failed to update client " + currentUser.ip + ":" + currentUser.port);
                System.out.println("\t" + t.getMessage());
            }
        }, MoreExecutors.directExecutor());
    }

    public void removePlayerUnits(UserSkeletonsDTO entitySkeletons) {
        ListenableFuture<Empty> future = stub
                .withDeadlineAfter(4000, TimeUnit.MILLISECONDS)
                .removePlayerUnits(GrpcUtil.toGrpcUserSkeletons(entitySkeletons));
        Futures.addCallback(future, new FutureCallback<>() {
            @Override
            public void onSuccess(@Nullable Empty result) {
                System.out.println("\t Successfully left the game");

            }

            @Override
            public void onFailure(Throwable t) {
                System.out.println("\t Failed to leave the ongoing game.");
                System.out.println("\t" + t.getMessage());
            }

        }, MoreExecutors.directExecutor());
    }

    public void requestVote(LeaderRequestDto request) {
        ListenableFuture<CandidateLeaderResponse> future = stub
                .withDeadlineAfter(4000, TimeUnit.MILLISECONDS)
                .requestVote(GrpcUtil.toGrpcLeaderRequest(request));
        Futures.addCallback(future, new FutureCallback<CandidateLeaderResponse>() {

            @Override
            public void onSuccess(@Nullable CandidateLeaderResponse candidateLeaderResponse) {
                assert candidateLeaderResponse != null;
                modelManager.getRaft().receiveVote(candidateLeaderResponse.getAcknowledgement());
            }

            @Override
            public void onFailure(Throwable throwable) {
                System.out.println("\tFailed to receive a vote from " + ip + ":" + port);
                System.out.println("\t" + throwable.getMessage());
            }
        }, MoreExecutors.directExecutor());
    }

    public void notifyNewLeader(proto.UserId user) {
        ListenableFuture<Empty> future = stub
                .withDeadlineAfter(4000, TimeUnit.MILLISECONDS)
                .notifyNewLeader(user);
        Futures.addCallback(future, new FutureCallback<>() {
            @Override
            public void onSuccess(@Nullable Empty empty) {
            }

            @Override
            public void onFailure(Throwable throwable) {
                System.out.println("\tFailed to receive a vote from " + ip + ":" + port);
                System.out.println("\t" + throwable.getMessage());
            }
        }, MoreExecutors.directExecutor());
    }

    public void sendNextRoundRequest(StartGameRequestDTO message) {
        ListenableFuture<Empty> future = stub
                .withDeadlineAfter(4000, TimeUnit.MILLISECONDS)
                .nextRound(GrpcUtil.toGrpcStartGameReq(message));
        Futures.addCallback(future, new FutureCallback<>() {
            @Override
            public void onSuccess(@Nullable Empty empty) {
            }

            @Override
            public void onFailure(Throwable throwable) {
                System.out.println("\tFailed to send next round request to " + ip + ":" + port);
                System.out.println("\t" + throwable.getMessage());
            }
        }, MoreExecutors.directExecutor());
    }

    public void sendEndGameMessage(UserScoreDTO message) {
        ListenableFuture<Empty> future = stub
                .withDeadlineAfter(4000, TimeUnit.MILLISECONDS)
                .endGameMessage(GrpcUtil.toGrpcUserScore(message));
        Futures.addCallback(future, new FutureCallback<>() {
            @Override
            public void onSuccess(@Nullable Empty empty) {
            }

            @Override
            public void onFailure(Throwable throwable) {
                System.out.println("\tFailed to receive a vote from " + ip + ":" + port);
                System.out.println("\t" + throwable.getMessage());
            }
        }, MoreExecutors.directExecutor());
    }

    public void destroy() {
        channel.shutdown();
        try {
            // Wait for the channel to terminate
            if (!channel.awaitTermination(2, TimeUnit.SECONDS)) {
                channel.shutdownNow(); // Force shutdown if not terminated
            }
        } catch (InterruptedException e) {
            channel.shutdownNow(); // Force shutdown on interruption
            Thread.currentThread().interrupt(); // Restore interrupted status
        }
    }
}
