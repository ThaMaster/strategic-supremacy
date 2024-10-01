package se.umu.cs.ads.sp.model.communication;

import se.umu.cs.ads.sp.utils.Position;

public class GrpcUtil {

    public static proto.Position toProto(Position javaPos) {
        return proto.Position.newBuilder().setX(javaPos.getX()).setY(javaPos.getY()).build();
    }

    public static Position fromProto(proto.Position protoPos) {
        return new Position(protoPos.getX(), protoPos.getY());
    }

    public static proto.ObjectId toProto(long javaId) {
        return proto.ObjectId.newBuilder().setId(javaId).build();
    }

    public static long fromProto(proto.ObjectId protoId) {
        return protoId.getId();
    }
}
