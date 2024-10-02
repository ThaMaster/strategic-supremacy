// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: grpc.proto

package nsProto;

/**
 * Protobuf type {@code grpc.User}
 */
public  final class User extends
    com.google.protobuf.GeneratedMessageV3 implements
    // @@protoc_insertion_point(message_implements:grpc.User)
    UserOrBuilder {
  // Use User.newBuilder() to construct.
  private User(com.google.protobuf.GeneratedMessageV3.Builder<?> builder) {
    super(builder);
  }
  private User() {
  }

  @java.lang.Override
  public final com.google.protobuf.UnknownFieldSet
  getUnknownFields() {
    return com.google.protobuf.UnknownFieldSet.getDefaultInstance();
  }
  private User(
      com.google.protobuf.CodedInputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    this();
    int mutable_bitField0_ = 0;
    try {
      boolean done = false;
      while (!done) {
        int tag = input.readTag();
        switch (tag) {
          case 0:
            done = true;
            break;
          default: {
            if (!input.skipField(tag)) {
              done = true;
            }
            break;
          }
          case 10: {
            nsProto.UserId.Builder subBuilder = null;
            if (id_ != null) {
              subBuilder = id_.toBuilder();
            }
            id_ = input.readMessage(nsProto.UserId.parser(), extensionRegistry);
            if (subBuilder != null) {
              subBuilder.mergeFrom(id_);
              id_ = subBuilder.buildPartial();
            }

            break;
          }
          case 18: {
            nsProto.Address.Builder subBuilder = null;
            if (connection_ != null) {
              subBuilder = connection_.toBuilder();
            }
            connection_ = input.readMessage(nsProto.Address.parser(), extensionRegistry);
            if (subBuilder != null) {
              subBuilder.mergeFrom(connection_);
              connection_ = subBuilder.buildPartial();
            }

            break;
          }
        }
      }
    } catch (com.google.protobuf.InvalidProtocolBufferException e) {
      throw e.setUnfinishedMessage(this);
    } catch (java.io.IOException e) {
      throw new com.google.protobuf.InvalidProtocolBufferException(
          e).setUnfinishedMessage(this);
    } finally {
      makeExtensionsImmutable();
    }
  }
  public static final com.google.protobuf.Descriptors.Descriptor
      getDescriptor() {
    return nsProto.Proto.internal_static_grpc_User_descriptor;
  }

  protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internalGetFieldAccessorTable() {
    return nsProto.Proto.internal_static_grpc_User_fieldAccessorTable
        .ensureFieldAccessorsInitialized(
            nsProto.User.class, nsProto.User.Builder.class);
  }

  public static final int ID_FIELD_NUMBER = 1;
  private nsProto.UserId id_;
  /**
   * <code>.grpc.UserId id = 1;</code>
   */
  public boolean hasId() {
    return id_ != null;
  }
  /**
   * <code>.grpc.UserId id = 1;</code>
   */
  public nsProto.UserId getId() {
    return id_ == null ? nsProto.UserId.getDefaultInstance() : id_;
  }
  /**
   * <code>.grpc.UserId id = 1;</code>
   */
  public nsProto.UserIdOrBuilder getIdOrBuilder() {
    return getId();
  }

  public static final int CONNECTION_FIELD_NUMBER = 2;
  private nsProto.Address connection_;
  /**
   * <code>.grpc.Address connection = 2;</code>
   */
  public boolean hasConnection() {
    return connection_ != null;
  }
  /**
   * <code>.grpc.Address connection = 2;</code>
   */
  public nsProto.Address getConnection() {
    return connection_ == null ? nsProto.Address.getDefaultInstance() : connection_;
  }
  /**
   * <code>.grpc.Address connection = 2;</code>
   */
  public nsProto.AddressOrBuilder getConnectionOrBuilder() {
    return getConnection();
  }

  private byte memoizedIsInitialized = -1;
  public final boolean isInitialized() {
    byte isInitialized = memoizedIsInitialized;
    if (isInitialized == 1) return true;
    if (isInitialized == 0) return false;

    memoizedIsInitialized = 1;
    return true;
  }

  public void writeTo(com.google.protobuf.CodedOutputStream output)
                      throws java.io.IOException {
    if (id_ != null) {
      output.writeMessage(1, getId());
    }
    if (connection_ != null) {
      output.writeMessage(2, getConnection());
    }
  }

  public int getSerializedSize() {
    int size = memoizedSize;
    if (size != -1) return size;

    size = 0;
    if (id_ != null) {
      size += com.google.protobuf.CodedOutputStream
        .computeMessageSize(1, getId());
    }
    if (connection_ != null) {
      size += com.google.protobuf.CodedOutputStream
        .computeMessageSize(2, getConnection());
    }
    memoizedSize = size;
    return size;
  }

  private static final long serialVersionUID = 0L;
  @java.lang.Override
  public boolean equals(final java.lang.Object obj) {
    if (obj == this) {
     return true;
    }
    if (!(obj instanceof nsProto.User)) {
      return super.equals(obj);
    }
    nsProto.User other = (nsProto.User) obj;

    boolean result = true;
    result = result && (hasId() == other.hasId());
    if (hasId()) {
      result = result && getId()
          .equals(other.getId());
    }
    result = result && (hasConnection() == other.hasConnection());
    if (hasConnection()) {
      result = result && getConnection()
          .equals(other.getConnection());
    }
    return result;
  }

  @java.lang.Override
  public int hashCode() {
    if (memoizedHashCode != 0) {
      return memoizedHashCode;
    }
    int hash = 41;
    hash = (19 * hash) + getDescriptor().hashCode();
    if (hasId()) {
      hash = (37 * hash) + ID_FIELD_NUMBER;
      hash = (53 * hash) + getId().hashCode();
    }
    if (hasConnection()) {
      hash = (37 * hash) + CONNECTION_FIELD_NUMBER;
      hash = (53 * hash) + getConnection().hashCode();
    }
    hash = (29 * hash) + unknownFields.hashCode();
    memoizedHashCode = hash;
    return hash;
  }

  public static nsProto.User parseFrom(
      java.nio.ByteBuffer data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static nsProto.User parseFrom(
      java.nio.ByteBuffer data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static nsProto.User parseFrom(
      com.google.protobuf.ByteString data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static nsProto.User parseFrom(
      com.google.protobuf.ByteString data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static nsProto.User parseFrom(byte[] data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static nsProto.User parseFrom(
      byte[] data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static nsProto.User parseFrom(java.io.InputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input);
  }
  public static nsProto.User parseFrom(
      java.io.InputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input, extensionRegistry);
  }
  public static nsProto.User parseDelimitedFrom(java.io.InputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseDelimitedWithIOException(PARSER, input);
  }
  public static nsProto.User parseDelimitedFrom(
      java.io.InputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseDelimitedWithIOException(PARSER, input, extensionRegistry);
  }
  public static nsProto.User parseFrom(
      com.google.protobuf.CodedInputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input);
  }
  public static nsProto.User parseFrom(
      com.google.protobuf.CodedInputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input, extensionRegistry);
  }

  public Builder newBuilderForType() { return newBuilder(); }
  public static Builder newBuilder() {
    return DEFAULT_INSTANCE.toBuilder();
  }
  public static Builder newBuilder(nsProto.User prototype) {
    return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
  }
  public Builder toBuilder() {
    return this == DEFAULT_INSTANCE
        ? new Builder() : new Builder().mergeFrom(this);
  }

  @java.lang.Override
  protected Builder newBuilderForType(
      com.google.protobuf.GeneratedMessageV3.BuilderParent parent) {
    Builder builder = new Builder(parent);
    return builder;
  }
  /**
   * Protobuf type {@code grpc.User}
   */
  public static final class Builder extends
      com.google.protobuf.GeneratedMessageV3.Builder<Builder> implements
      // @@protoc_insertion_point(builder_implements:grpc.User)
      nsProto.UserOrBuilder {
    public static final com.google.protobuf.Descriptors.Descriptor
        getDescriptor() {
      return nsProto.Proto.internal_static_grpc_User_descriptor;
    }

    protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
        internalGetFieldAccessorTable() {
      return nsProto.Proto.internal_static_grpc_User_fieldAccessorTable
          .ensureFieldAccessorsInitialized(
              nsProto.User.class, nsProto.User.Builder.class);
    }

    // Construct using nsProto.User.newBuilder()
    private Builder() {
      maybeForceBuilderInitialization();
    }

    private Builder(
        com.google.protobuf.GeneratedMessageV3.BuilderParent parent) {
      super(parent);
      maybeForceBuilderInitialization();
    }
    private void maybeForceBuilderInitialization() {
      if (com.google.protobuf.GeneratedMessageV3
              .alwaysUseFieldBuilders) {
      }
    }
    public Builder clear() {
      super.clear();
      if (idBuilder_ == null) {
        id_ = null;
      } else {
        id_ = null;
        idBuilder_ = null;
      }
      if (connectionBuilder_ == null) {
        connection_ = null;
      } else {
        connection_ = null;
        connectionBuilder_ = null;
      }
      return this;
    }

    public com.google.protobuf.Descriptors.Descriptor
        getDescriptorForType() {
      return nsProto.Proto.internal_static_grpc_User_descriptor;
    }

    public nsProto.User getDefaultInstanceForType() {
      return nsProto.User.getDefaultInstance();
    }

    public nsProto.User build() {
      nsProto.User result = buildPartial();
      if (!result.isInitialized()) {
        throw newUninitializedMessageException(result);
      }
      return result;
    }

    public nsProto.User buildPartial() {
      nsProto.User result = new nsProto.User(this);
      if (idBuilder_ == null) {
        result.id_ = id_;
      } else {
        result.id_ = idBuilder_.build();
      }
      if (connectionBuilder_ == null) {
        result.connection_ = connection_;
      } else {
        result.connection_ = connectionBuilder_.build();
      }
      onBuilt();
      return result;
    }

    public Builder clone() {
      return (Builder) super.clone();
    }
    public Builder setField(
        com.google.protobuf.Descriptors.FieldDescriptor field,
        Object value) {
      return (Builder) super.setField(field, value);
    }
    public Builder clearField(
        com.google.protobuf.Descriptors.FieldDescriptor field) {
      return (Builder) super.clearField(field);
    }
    public Builder clearOneof(
        com.google.protobuf.Descriptors.OneofDescriptor oneof) {
      return (Builder) super.clearOneof(oneof);
    }
    public Builder setRepeatedField(
        com.google.protobuf.Descriptors.FieldDescriptor field,
        int index, Object value) {
      return (Builder) super.setRepeatedField(field, index, value);
    }
    public Builder addRepeatedField(
        com.google.protobuf.Descriptors.FieldDescriptor field,
        Object value) {
      return (Builder) super.addRepeatedField(field, value);
    }
    public Builder mergeFrom(com.google.protobuf.Message other) {
      if (other instanceof nsProto.User) {
        return mergeFrom((nsProto.User)other);
      } else {
        super.mergeFrom(other);
        return this;
      }
    }

    public Builder mergeFrom(nsProto.User other) {
      if (other == nsProto.User.getDefaultInstance()) return this;
      if (other.hasId()) {
        mergeId(other.getId());
      }
      if (other.hasConnection()) {
        mergeConnection(other.getConnection());
      }
      onChanged();
      return this;
    }

    public final boolean isInitialized() {
      return true;
    }

    public Builder mergeFrom(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      nsProto.User parsedMessage = null;
      try {
        parsedMessage = PARSER.parsePartialFrom(input, extensionRegistry);
      } catch (com.google.protobuf.InvalidProtocolBufferException e) {
        parsedMessage = (nsProto.User) e.getUnfinishedMessage();
        throw e.unwrapIOException();
      } finally {
        if (parsedMessage != null) {
          mergeFrom(parsedMessage);
        }
      }
      return this;
    }

    private nsProto.UserId id_ = null;
    private com.google.protobuf.SingleFieldBuilderV3<
        nsProto.UserId, nsProto.UserId.Builder, nsProto.UserIdOrBuilder> idBuilder_;
    /**
     * <code>.grpc.UserId id = 1;</code>
     */
    public boolean hasId() {
      return idBuilder_ != null || id_ != null;
    }
    /**
     * <code>.grpc.UserId id = 1;</code>
     */
    public nsProto.UserId getId() {
      if (idBuilder_ == null) {
        return id_ == null ? nsProto.UserId.getDefaultInstance() : id_;
      } else {
        return idBuilder_.getMessage();
      }
    }
    /**
     * <code>.grpc.UserId id = 1;</code>
     */
    public Builder setId(nsProto.UserId value) {
      if (idBuilder_ == null) {
        if (value == null) {
          throw new NullPointerException();
        }
        id_ = value;
        onChanged();
      } else {
        idBuilder_.setMessage(value);
      }

      return this;
    }
    /**
     * <code>.grpc.UserId id = 1;</code>
     */
    public Builder setId(
        nsProto.UserId.Builder builderForValue) {
      if (idBuilder_ == null) {
        id_ = builderForValue.build();
        onChanged();
      } else {
        idBuilder_.setMessage(builderForValue.build());
      }

      return this;
    }
    /**
     * <code>.grpc.UserId id = 1;</code>
     */
    public Builder mergeId(nsProto.UserId value) {
      if (idBuilder_ == null) {
        if (id_ != null) {
          id_ =
            nsProto.UserId.newBuilder(id_).mergeFrom(value).buildPartial();
        } else {
          id_ = value;
        }
        onChanged();
      } else {
        idBuilder_.mergeFrom(value);
      }

      return this;
    }
    /**
     * <code>.grpc.UserId id = 1;</code>
     */
    public Builder clearId() {
      if (idBuilder_ == null) {
        id_ = null;
        onChanged();
      } else {
        id_ = null;
        idBuilder_ = null;
      }

      return this;
    }
    /**
     * <code>.grpc.UserId id = 1;</code>
     */
    public nsProto.UserId.Builder getIdBuilder() {
      
      onChanged();
      return getIdFieldBuilder().getBuilder();
    }
    /**
     * <code>.grpc.UserId id = 1;</code>
     */
    public nsProto.UserIdOrBuilder getIdOrBuilder() {
      if (idBuilder_ != null) {
        return idBuilder_.getMessageOrBuilder();
      } else {
        return id_ == null ?
            nsProto.UserId.getDefaultInstance() : id_;
      }
    }
    /**
     * <code>.grpc.UserId id = 1;</code>
     */
    private com.google.protobuf.SingleFieldBuilderV3<
        nsProto.UserId, nsProto.UserId.Builder, nsProto.UserIdOrBuilder> 
        getIdFieldBuilder() {
      if (idBuilder_ == null) {
        idBuilder_ = new com.google.protobuf.SingleFieldBuilderV3<
            nsProto.UserId, nsProto.UserId.Builder, nsProto.UserIdOrBuilder>(
                getId(),
                getParentForChildren(),
                isClean());
        id_ = null;
      }
      return idBuilder_;
    }

    private nsProto.Address connection_ = null;
    private com.google.protobuf.SingleFieldBuilderV3<
        nsProto.Address, nsProto.Address.Builder, nsProto.AddressOrBuilder> connectionBuilder_;
    /**
     * <code>.grpc.Address connection = 2;</code>
     */
    public boolean hasConnection() {
      return connectionBuilder_ != null || connection_ != null;
    }
    /**
     * <code>.grpc.Address connection = 2;</code>
     */
    public nsProto.Address getConnection() {
      if (connectionBuilder_ == null) {
        return connection_ == null ? nsProto.Address.getDefaultInstance() : connection_;
      } else {
        return connectionBuilder_.getMessage();
      }
    }
    /**
     * <code>.grpc.Address connection = 2;</code>
     */
    public Builder setConnection(nsProto.Address value) {
      if (connectionBuilder_ == null) {
        if (value == null) {
          throw new NullPointerException();
        }
        connection_ = value;
        onChanged();
      } else {
        connectionBuilder_.setMessage(value);
      }

      return this;
    }
    /**
     * <code>.grpc.Address connection = 2;</code>
     */
    public Builder setConnection(
        nsProto.Address.Builder builderForValue) {
      if (connectionBuilder_ == null) {
        connection_ = builderForValue.build();
        onChanged();
      } else {
        connectionBuilder_.setMessage(builderForValue.build());
      }

      return this;
    }
    /**
     * <code>.grpc.Address connection = 2;</code>
     */
    public Builder mergeConnection(nsProto.Address value) {
      if (connectionBuilder_ == null) {
        if (connection_ != null) {
          connection_ =
            nsProto.Address.newBuilder(connection_).mergeFrom(value).buildPartial();
        } else {
          connection_ = value;
        }
        onChanged();
      } else {
        connectionBuilder_.mergeFrom(value);
      }

      return this;
    }
    /**
     * <code>.grpc.Address connection = 2;</code>
     */
    public Builder clearConnection() {
      if (connectionBuilder_ == null) {
        connection_ = null;
        onChanged();
      } else {
        connection_ = null;
        connectionBuilder_ = null;
      }

      return this;
    }
    /**
     * <code>.grpc.Address connection = 2;</code>
     */
    public nsProto.Address.Builder getConnectionBuilder() {
      
      onChanged();
      return getConnectionFieldBuilder().getBuilder();
    }
    /**
     * <code>.grpc.Address connection = 2;</code>
     */
    public nsProto.AddressOrBuilder getConnectionOrBuilder() {
      if (connectionBuilder_ != null) {
        return connectionBuilder_.getMessageOrBuilder();
      } else {
        return connection_ == null ?
            nsProto.Address.getDefaultInstance() : connection_;
      }
    }
    /**
     * <code>.grpc.Address connection = 2;</code>
     */
    private com.google.protobuf.SingleFieldBuilderV3<
        nsProto.Address, nsProto.Address.Builder, nsProto.AddressOrBuilder> 
        getConnectionFieldBuilder() {
      if (connectionBuilder_ == null) {
        connectionBuilder_ = new com.google.protobuf.SingleFieldBuilderV3<
            nsProto.Address, nsProto.Address.Builder, nsProto.AddressOrBuilder>(
                getConnection(),
                getParentForChildren(),
                isClean());
        connection_ = null;
      }
      return connectionBuilder_;
    }
    public final Builder setUnknownFields(
        final com.google.protobuf.UnknownFieldSet unknownFields) {
      return this;
    }

    public final Builder mergeUnknownFields(
        final com.google.protobuf.UnknownFieldSet unknownFields) {
      return this;
    }


    // @@protoc_insertion_point(builder_scope:grpc.User)
  }

  // @@protoc_insertion_point(class_scope:grpc.User)
  private static final nsProto.User DEFAULT_INSTANCE;
  static {
    DEFAULT_INSTANCE = new nsProto.User();
  }

  public static nsProto.User getDefaultInstance() {
    return DEFAULT_INSTANCE;
  }

  private static final com.google.protobuf.Parser<User>
      PARSER = new com.google.protobuf.AbstractParser<User>() {
    public User parsePartialFrom(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
        return new User(input, extensionRegistry);
    }
  };

  public static com.google.protobuf.Parser<User> parser() {
    return PARSER;
  }

  @java.lang.Override
  public com.google.protobuf.Parser<User> getParserForType() {
    return PARSER;
  }

  public nsProto.User getDefaultInstanceForType() {
    return DEFAULT_INSTANCE;
  }

}

