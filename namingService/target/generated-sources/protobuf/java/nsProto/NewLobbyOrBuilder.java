// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: grpc.proto

package nsProto;

public interface NewLobbyOrBuilder extends
    // @@protoc_insertion_point(interface_extends:grpc.NewLobby)
    com.google.protobuf.MessageOrBuilder {

  /**
   * <code>.grpc.User lobbyCreator = 1;</code>
   */
  boolean hasLobbyCreator();
  /**
   * <code>.grpc.User lobbyCreator = 1;</code>
   */
  nsProto.User getLobbyCreator();
  /**
   * <code>.grpc.User lobbyCreator = 1;</code>
   */
  nsProto.UserOrBuilder getLobbyCreatorOrBuilder();

  /**
   * <code>string lobbyName = 2;</code>
   */
  java.lang.String getLobbyName();
  /**
   * <code>string lobbyName = 2;</code>
   */
  com.google.protobuf.ByteString
      getLobbyNameBytes();

  /**
   * <code>int32 maxPlayers = 3;</code>
   */
  int getMaxPlayers();
}
