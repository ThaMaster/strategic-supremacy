package se.umu.cs.ads.sp.model.communication;

import proto.Position;
import se.umu.cs.ads.ns.app.Lobby;
import se.umu.cs.ads.ns.app.User;
import se.umu.cs.ads.sp.model.communication.dto.CollectableDTO;
import se.umu.cs.ads.sp.model.communication.dto.EntitySkeletonDTO;
import se.umu.cs.ads.sp.model.communication.dto.EnvironmentDTO;
import se.umu.cs.ads.sp.model.communication.dto.StartGameRequest;
import se.umu.cs.ads.sp.model.objects.collectables.Reward;

import java.util.ArrayList;

public class GrpcUtil {


    public static proto.Position toGrpcPosition(int x, int y) {
        return proto.Position.newBuilder()
                .setX(x)
                .setY(y)
                .build();
    }

    public static se.umu.cs.ads.sp.utils.Position fromGrpcPosition(Position pos) {
        return new se.umu.cs.ads.sp.utils.Position(pos.getX(), pos.getY());
    }

    public static proto.EntitySkeleton toGrpcEntitySkeleton(EntitySkeletonDTO skeleton) {
        return proto.EntitySkeleton.newBuilder()
                .setId(skeleton.id())
                .setUserId(skeleton.userId())
                .setPosition(toGrpcPosition(skeleton.position().getX(), skeleton.position().getY()))
                .build();
    }

    public static proto.Environment toGrpcEnvironment(EnvironmentDTO environmentDTO) {
        return proto.Environment.newBuilder()
                .setId(environmentDTO.id())
                .setPosition(toGrpcPosition(environmentDTO.position().getX(), environmentDTO.position().getY()))
                .setType(environmentDTO.type())
                .build();
    }

    public static proto.Reward toGrpcReward(Reward reward) {
        return proto.Reward.newBuilder()
                .setQuantity(reward.getQuantity())
                .setReward(reward.toString())
                .build();
    }

    public static proto.Collectable toGrpcCollectable(CollectableDTO collectable) {
        return proto.Collectable.newBuilder()
                .setId(collectable.id())
                .setPosition(toGrpcPosition(collectable.position().getX(), collectable.position().getY()))
                .setType(collectable.type())
                .setReward(toGrpcReward(collectable.reward()))
                .build();
    }

    public static proto.StartGameRequest toGrpcStartGameReq(StartGameRequest req) {
        proto.StartGameRequest.Builder builder = proto.StartGameRequest.newBuilder();

        for (EntitySkeletonDTO skeleton : req.getEntitySkeletons()) {
            builder.addEntities(toGrpcEntitySkeleton(skeleton));
        }

        for (EnvironmentDTO environment : req.getEnvironments()) {
            builder.addEnvironments(toGrpcEnvironment(environment));
        }

        for (CollectableDTO collectable : req.getCollectables()) {
            builder.addCollectables(toGrpcCollectable(collectable));
        }

        return builder.build();
    }

    public static EntitySkeletonDTO fromGrpcSkeletons(proto.EntitySkeleton skeletons) {
        return new EntitySkeletonDTO(
                skeletons.getId(),
                skeletons.getUserId(),
                fromGrpcPosition(skeletons.getPosition()));
    }

    public static EnvironmentDTO fromGrpcEnv(proto.Environment env) {
        return new EnvironmentDTO(env.getId(), env.getUserId(), fromGrpcPosition(env.getPosition()), env.getType());
    }

    public static Reward fromGrpcReward(proto.Reward reward) {
        return new Reward(reward.getQuantity(), reward.getReward());
    }

    public static CollectableDTO fromGrpcCollectable(proto.Collectable collectable) {
        return new CollectableDTO(collectable.getId(),
                fromGrpcPosition(collectable.getPosition()),
                collectable.getType(),
                fromGrpcReward(collectable.getReward()));
    }

    public static StartGameRequest fromGrpcStartGameReq(proto.StartGameRequest req) {
        StartGameRequest data = new StartGameRequest(new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
        for (proto.EntitySkeleton skeleton : req.getEntitiesList()) {
            data.addEntitySkeleton(fromGrpcSkeletons(skeleton));
        }

        for (proto.Environment env : req.getEnvironmentsList()) {
            data.addEnvironment(fromGrpcEnv(env));
        }

        for (proto.Collectable collectable : req.getCollectablesList()) {
            data.addCollectable(fromGrpcCollectable(collectable));
        }


        return data;
    }

    public static User fromGrpcUser(proto.User request) {
        return new User(request.getId(), request.getUsername(), request.getIp(), request.getPort());
    }

    public static proto.User toGrpcUser(User user) {
        return proto.User.newBuilder()
                .setId(user.id)
                .setPort(user.port)
                .setIp(user.ip)
                .setUsername(user.username)
                .build();
    }

    public static Lobby fromGrpcLobby(proto.DetailedLobbyInfo request) {
        Lobby lobby = new Lobby(request.getId(), request.getLobbyName(), request.getMaxPlayers());
        ArrayList<User> users = new ArrayList<>();
        for (proto.User user : request.getUsersList()) {
            users.add(fromGrpcUser(user));
        }
        lobby.setUsers(users);
        lobby.selectedMap = request.getSelectedMap();
        return lobby;
    }

    public static proto.DetailedLobbyInfo toGrpcLobby(Lobby lobby, String selectedMap) {
        proto.DetailedLobbyInfo.Builder builder = proto.DetailedLobbyInfo.newBuilder()
                .setId(lobby.id)
                .setLeader(toGrpcUser(lobby.leader))
                .setSelectedMap(selectedMap)
                .setMaxPlayers(lobby.maxPlayers)
                .setLobbyName(lobby.name);
        for (User user : lobby.users) {
            builder.addUsers(toGrpcUser(user));
        }
        return builder.build();
    }
}
