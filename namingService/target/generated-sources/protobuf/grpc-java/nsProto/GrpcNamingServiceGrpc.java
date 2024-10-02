package nsProto;

import static io.grpc.stub.ClientCalls.asyncUnaryCall;
import static io.grpc.stub.ClientCalls.asyncServerStreamingCall;
import static io.grpc.stub.ClientCalls.asyncClientStreamingCall;
import static io.grpc.stub.ClientCalls.asyncBidiStreamingCall;
import static io.grpc.stub.ClientCalls.blockingUnaryCall;
import static io.grpc.stub.ClientCalls.blockingServerStreamingCall;
import static io.grpc.stub.ClientCalls.futureUnaryCall;
import static io.grpc.MethodDescriptor.generateFullMethodName;
import static io.grpc.stub.ServerCalls.asyncUnaryCall;
import static io.grpc.stub.ServerCalls.asyncServerStreamingCall;
import static io.grpc.stub.ServerCalls.asyncClientStreamingCall;
import static io.grpc.stub.ServerCalls.asyncBidiStreamingCall;
import static io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall;
import static io.grpc.stub.ServerCalls.asyncUnimplementedStreamingCall;

/**
 */
@javax.annotation.Generated(
    value = "by gRPC proto compiler (version 1.4.0)",
    comments = "Source: grpc.proto")
public final class GrpcNamingServiceGrpc {

  private GrpcNamingServiceGrpc() {}

  public static final String SERVICE_NAME = "grpc.GrpcNamingService";

  // Static method descriptors that strictly reflect the proto.
  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  public static final io.grpc.MethodDescriptor<nsProto.NewLobby,
      nsProto.LobbyId> METHOD_CREATE_LOBBY =
      io.grpc.MethodDescriptor.<nsProto.NewLobby, nsProto.LobbyId>newBuilder()
          .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
          .setFullMethodName(generateFullMethodName(
              "grpc.GrpcNamingService", "createLobby"))
          .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
              nsProto.NewLobby.getDefaultInstance()))
          .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
              nsProto.LobbyId.getDefaultInstance()))
          .build();
  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  public static final io.grpc.MethodDescriptor<nsProto.LobbyId,
      com.google.protobuf.Empty> METHOD_DELETE_LOBBY =
      io.grpc.MethodDescriptor.<nsProto.LobbyId, com.google.protobuf.Empty>newBuilder()
          .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
          .setFullMethodName(generateFullMethodName(
              "grpc.GrpcNamingService", "deleteLobby"))
          .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
              nsProto.LobbyId.getDefaultInstance()))
          .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
              com.google.protobuf.Empty.getDefaultInstance()))
          .build();
  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  public static final io.grpc.MethodDescriptor<nsProto.LobbyId,
      nsProto.LobbyInfo> METHOD_GET_LOBBY_INFO =
      io.grpc.MethodDescriptor.<nsProto.LobbyId, nsProto.LobbyInfo>newBuilder()
          .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
          .setFullMethodName(generateFullMethodName(
              "grpc.GrpcNamingService", "getLobbyInfo"))
          .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
              nsProto.LobbyId.getDefaultInstance()))
          .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
              nsProto.LobbyInfo.getDefaultInstance()))
          .build();
  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  public static final io.grpc.MethodDescriptor<com.google.protobuf.Empty,
      nsProto.Lobbies> METHOD_GET_LOBBIES =
      io.grpc.MethodDescriptor.<com.google.protobuf.Empty, nsProto.Lobbies>newBuilder()
          .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
          .setFullMethodName(generateFullMethodName(
              "grpc.GrpcNamingService", "getLobbies"))
          .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
              com.google.protobuf.Empty.getDefaultInstance()))
          .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
              nsProto.Lobbies.getDefaultInstance()))
          .build();

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static GrpcNamingServiceStub newStub(io.grpc.Channel channel) {
    return new GrpcNamingServiceStub(channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static GrpcNamingServiceBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    return new GrpcNamingServiceBlockingStub(channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static GrpcNamingServiceFutureStub newFutureStub(
      io.grpc.Channel channel) {
    return new GrpcNamingServiceFutureStub(channel);
  }

  /**
   */
  public static abstract class GrpcNamingServiceImplBase implements io.grpc.BindableService {

    /**
     */
    public void createLobby(nsProto.NewLobby request,
        io.grpc.stub.StreamObserver<nsProto.LobbyId> responseObserver) {
      asyncUnimplementedUnaryCall(METHOD_CREATE_LOBBY, responseObserver);
    }

    /**
     */
    public void deleteLobby(nsProto.LobbyId request,
        io.grpc.stub.StreamObserver<com.google.protobuf.Empty> responseObserver) {
      asyncUnimplementedUnaryCall(METHOD_DELETE_LOBBY, responseObserver);
    }

    /**
     */
    public void getLobbyInfo(nsProto.LobbyId request,
        io.grpc.stub.StreamObserver<nsProto.LobbyInfo> responseObserver) {
      asyncUnimplementedUnaryCall(METHOD_GET_LOBBY_INFO, responseObserver);
    }

    /**
     */
    public void getLobbies(com.google.protobuf.Empty request,
        io.grpc.stub.StreamObserver<nsProto.Lobbies> responseObserver) {
      asyncUnimplementedUnaryCall(METHOD_GET_LOBBIES, responseObserver);
    }

    @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
      return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
          .addMethod(
            METHOD_CREATE_LOBBY,
            asyncUnaryCall(
              new MethodHandlers<
                nsProto.NewLobby,
                nsProto.LobbyId>(
                  this, METHODID_CREATE_LOBBY)))
          .addMethod(
            METHOD_DELETE_LOBBY,
            asyncUnaryCall(
              new MethodHandlers<
                nsProto.LobbyId,
                com.google.protobuf.Empty>(
                  this, METHODID_DELETE_LOBBY)))
          .addMethod(
            METHOD_GET_LOBBY_INFO,
            asyncUnaryCall(
              new MethodHandlers<
                nsProto.LobbyId,
                nsProto.LobbyInfo>(
                  this, METHODID_GET_LOBBY_INFO)))
          .addMethod(
            METHOD_GET_LOBBIES,
            asyncUnaryCall(
              new MethodHandlers<
                com.google.protobuf.Empty,
                nsProto.Lobbies>(
                  this, METHODID_GET_LOBBIES)))
          .build();
    }
  }

  /**
   */
  public static final class GrpcNamingServiceStub extends io.grpc.stub.AbstractStub<GrpcNamingServiceStub> {
    private GrpcNamingServiceStub(io.grpc.Channel channel) {
      super(channel);
    }

    private GrpcNamingServiceStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected GrpcNamingServiceStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new GrpcNamingServiceStub(channel, callOptions);
    }

    /**
     */
    public void createLobby(nsProto.NewLobby request,
        io.grpc.stub.StreamObserver<nsProto.LobbyId> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(METHOD_CREATE_LOBBY, getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void deleteLobby(nsProto.LobbyId request,
        io.grpc.stub.StreamObserver<com.google.protobuf.Empty> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(METHOD_DELETE_LOBBY, getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void getLobbyInfo(nsProto.LobbyId request,
        io.grpc.stub.StreamObserver<nsProto.LobbyInfo> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(METHOD_GET_LOBBY_INFO, getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void getLobbies(com.google.protobuf.Empty request,
        io.grpc.stub.StreamObserver<nsProto.Lobbies> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(METHOD_GET_LOBBIES, getCallOptions()), request, responseObserver);
    }
  }

  /**
   */
  public static final class GrpcNamingServiceBlockingStub extends io.grpc.stub.AbstractStub<GrpcNamingServiceBlockingStub> {
    private GrpcNamingServiceBlockingStub(io.grpc.Channel channel) {
      super(channel);
    }

    private GrpcNamingServiceBlockingStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected GrpcNamingServiceBlockingStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new GrpcNamingServiceBlockingStub(channel, callOptions);
    }

    /**
     */
    public nsProto.LobbyId createLobby(nsProto.NewLobby request) {
      return blockingUnaryCall(
          getChannel(), METHOD_CREATE_LOBBY, getCallOptions(), request);
    }

    /**
     */
    public com.google.protobuf.Empty deleteLobby(nsProto.LobbyId request) {
      return blockingUnaryCall(
          getChannel(), METHOD_DELETE_LOBBY, getCallOptions(), request);
    }

    /**
     */
    public nsProto.LobbyInfo getLobbyInfo(nsProto.LobbyId request) {
      return blockingUnaryCall(
          getChannel(), METHOD_GET_LOBBY_INFO, getCallOptions(), request);
    }

    /**
     */
    public nsProto.Lobbies getLobbies(com.google.protobuf.Empty request) {
      return blockingUnaryCall(
          getChannel(), METHOD_GET_LOBBIES, getCallOptions(), request);
    }
  }

  /**
   */
  public static final class GrpcNamingServiceFutureStub extends io.grpc.stub.AbstractStub<GrpcNamingServiceFutureStub> {
    private GrpcNamingServiceFutureStub(io.grpc.Channel channel) {
      super(channel);
    }

    private GrpcNamingServiceFutureStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected GrpcNamingServiceFutureStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new GrpcNamingServiceFutureStub(channel, callOptions);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<nsProto.LobbyId> createLobby(
        nsProto.NewLobby request) {
      return futureUnaryCall(
          getChannel().newCall(METHOD_CREATE_LOBBY, getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<com.google.protobuf.Empty> deleteLobby(
        nsProto.LobbyId request) {
      return futureUnaryCall(
          getChannel().newCall(METHOD_DELETE_LOBBY, getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<nsProto.LobbyInfo> getLobbyInfo(
        nsProto.LobbyId request) {
      return futureUnaryCall(
          getChannel().newCall(METHOD_GET_LOBBY_INFO, getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<nsProto.Lobbies> getLobbies(
        com.google.protobuf.Empty request) {
      return futureUnaryCall(
          getChannel().newCall(METHOD_GET_LOBBIES, getCallOptions()), request);
    }
  }

  private static final int METHODID_CREATE_LOBBY = 0;
  private static final int METHODID_DELETE_LOBBY = 1;
  private static final int METHODID_GET_LOBBY_INFO = 2;
  private static final int METHODID_GET_LOBBIES = 3;

  private static final class MethodHandlers<Req, Resp> implements
      io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
    private final GrpcNamingServiceImplBase serviceImpl;
    private final int methodId;

    MethodHandlers(GrpcNamingServiceImplBase serviceImpl, int methodId) {
      this.serviceImpl = serviceImpl;
      this.methodId = methodId;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_CREATE_LOBBY:
          serviceImpl.createLobby((nsProto.NewLobby) request,
              (io.grpc.stub.StreamObserver<nsProto.LobbyId>) responseObserver);
          break;
        case METHODID_DELETE_LOBBY:
          serviceImpl.deleteLobby((nsProto.LobbyId) request,
              (io.grpc.stub.StreamObserver<com.google.protobuf.Empty>) responseObserver);
          break;
        case METHODID_GET_LOBBY_INFO:
          serviceImpl.getLobbyInfo((nsProto.LobbyId) request,
              (io.grpc.stub.StreamObserver<nsProto.LobbyInfo>) responseObserver);
          break;
        case METHODID_GET_LOBBIES:
          serviceImpl.getLobbies((com.google.protobuf.Empty) request,
              (io.grpc.stub.StreamObserver<nsProto.Lobbies>) responseObserver);
          break;
        default:
          throw new AssertionError();
      }
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public io.grpc.stub.StreamObserver<Req> invoke(
        io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        default:
          throw new AssertionError();
      }
    }
  }

  private static final class GrpcNamingServiceDescriptorSupplier implements io.grpc.protobuf.ProtoFileDescriptorSupplier {
    @java.lang.Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return nsProto.Proto.getDescriptor();
    }
  }

  private static volatile io.grpc.ServiceDescriptor serviceDescriptor;

  public static io.grpc.ServiceDescriptor getServiceDescriptor() {
    io.grpc.ServiceDescriptor result = serviceDescriptor;
    if (result == null) {
      synchronized (GrpcNamingServiceGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new GrpcNamingServiceDescriptorSupplier())
              .addMethod(METHOD_CREATE_LOBBY)
              .addMethod(METHOD_DELETE_LOBBY)
              .addMethod(METHOD_GET_LOBBY_INFO)
              .addMethod(METHOD_GET_LOBBIES)
              .build();
        }
      }
    }
    return result;
  }
}
