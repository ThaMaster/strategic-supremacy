package se.umu.cs.ads.sp.model.communication;

import org.checkerframework.checker.units.qual.A;
import proto.Environment;
import proto.L3Message;
import se.umu.cs.ads.ns.app.Lobby;
import se.umu.cs.ads.ns.app.User;
import se.umu.cs.ads.sp.model.communication.dto.*;
import se.umu.cs.ads.sp.model.objects.collectables.Reward;
import se.umu.cs.ads.sp.utils.Position;

import java.util.ArrayList;

public class GrpcUtil {


    public static proto.Position toGrpcPosition(int x, int y) {
        return proto.Position.newBuilder()
                .setX(x)
                .setY(y)
                .build();
    }

    public static proto.Position toGrpcPosition(Position position) {
        return proto.Position.newBuilder()
                .setX(position.getX())
                .setY(position.getY())
                .build();
    }

    public static Position fromGrpcPosition(proto.Position pos) {
        return new Position(pos.getX(), pos.getY());
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
                .setUserId(environmentDTO.userId())
                .setPosition(toGrpcPosition(environmentDTO.position().getX(), environmentDTO.position().getY()))
                .setType(environmentDTO.type())
                .setRemainingResource(environmentDTO.remainingResource())
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

    public static proto.StartGameRequest toGrpcStartGameReq(StartGameRequestDTO req) {
        proto.StartGameRequest.Builder builder = proto.StartGameRequest.newBuilder();

        builder.setEntities(toGrpcEntitySkeletons(req.entitySkeletons()));

        for (EnvironmentDTO environment : req.environments()) {
            builder.addEnvironments(toGrpcEnvironment(environment));
        }

        for (CollectableDTO collectable : req.collectables()) {
            builder.addCollectables(toGrpcCollectable(collectable));
        }

        return builder.build();
    }

    public static proto.EntitySkeletons toGrpcEntitySkeletons(ArrayList<EntitySkeletonDTO> entitySkeletons) {
        proto.EntitySkeletons.Builder builder = proto.EntitySkeletons.newBuilder();
        for (EntitySkeletonDTO skeleton : entitySkeletons) {
            builder.addSkeletons(toGrpcEntitySkeleton(skeleton));
        }
        return builder.build();
    }

    public static EntitySkeletonDTO fromGrpcSkeleton(proto.EntitySkeleton skeletons) {
        return new EntitySkeletonDTO(
                skeletons.getId(),
                skeletons.getUserId(),
                fromGrpcPosition(skeletons.getPosition()));
    }

    public static ArrayList<EntitySkeletonDTO> fromGrpcEntitySkeletons(proto.EntitySkeletons grpcSkeletons) {
        ArrayList<EntitySkeletonDTO> skeletons = new ArrayList<>();
        for (int i = 0; i < grpcSkeletons.getSkeletonsCount(); i++) {
            skeletons.add(fromGrpcSkeleton(grpcSkeletons.getSkeletons(i)));
        }
        return skeletons;
    }

    public static EnvironmentDTO fromGrpcEnv(proto.Environment env) {
        return new EnvironmentDTO(env.getId(), env.getUserId(), fromGrpcPosition(env.getPosition()), env.getType(), env.getRemainingResource());
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

    public static StartGameRequestDTO fromGrpcStartGameReq(proto.StartGameRequest req) {
        StartGameRequestDTO data = new StartGameRequestDTO(fromGrpcEntitySkeletons(req.getEntities()), new ArrayList<>(), new ArrayList<>());

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


    public static proto.L3Message toGrpcL3Message(L3UpdateDTO message){
        proto.L3Message.Builder builder = proto.L3Message.newBuilder();

        for(long pickedUpCollectables : message.pickedUpCollectables()){
            builder.addPickedUpCollectables(pickedUpCollectables);
        }
        for(EnvironmentDTO env : message.environments()){
            builder.addEnvironments(toGrpcEnvironment(env));
        }

        builder.setEntities(toGrpcEntitySkeletons(message.entities()))
                .setRemainingTime(message.remainingTime())
                .setCurrentScoreLeader(message.currentScoreLeader())
                .setSeverity(message.msgSeverity());

        return builder.build();
    }

    public static L3UpdateDTO fromGrpcL3Message(proto.L3Message message){
        ArrayList<EntitySkeletonDTO> entities = new ArrayList<>();
        ArrayList<EnvironmentDTO> environments = new ArrayList<>();
        ArrayList<Long> pickedUp = new ArrayList<>();

        entities = fromGrpcEntitySkeletons(message.getEntities());
        for(int i = 0; i < message.getEnvironmentsCount(); i++){
            environments.add(fromGrpcEnv(message.getEnvironments(i)));
        }
        for(int i = 0; i < message.getPickedUpCollectablesCount(); i++){
            pickedUp.add(message.getPickedUpCollectables(i));
        }

        return new L3UpdateDTO(
                entities,
                pickedUp,
                message.getRemainingTime(),
                message.getCurrentScoreLeader(),
                environments,
                message.getSeverity()
        );
    }

    public static Lobby fromGrpcLobby(proto.DetailedLobbyInfo request) {
        Lobby lobby = new Lobby(
                request.getId(),
                request.getLobbyName(),
                fromGrpcUser(request.getLeader()),
                request.getUsersCount(),
                request.getMaxPlayers(),
                request.getSelectedMap());

        ArrayList<User> users = new ArrayList<>();
        for (proto.User user : request.getUsersList()) {
            users.add(fromGrpcUser(user));
        }

        lobby.setUsers(users);
        return lobby;
    }

    public static proto.DetailedLobbyInfo toGrpcLobby(Lobby lobby) {
        proto.DetailedLobbyInfo.Builder builder = proto.DetailedLobbyInfo.newBuilder()
                .setId(lobby.id)
                .setLeader(toGrpcUser(lobby.leader))
                .setSelectedMap(lobby.selectedMap)
                .setMaxPlayers(lobby.maxPlayers)
                .setLobbyName(lobby.name);
        for (User user : lobby.users) {
            builder.addUsers(toGrpcUser(user));
        }
        return builder.build();
    }

    public static proto.PlayerUnit toGrpcPlayerUnit(CompleteUnitInfoDTO unit) {
        return proto.PlayerUnit.newBuilder()
                .setUnitId(unit.unitId())
                .setTargetUnitId(unit.targetUnitId())
                .setPosition(toGrpcPosition(unit.position()))
                .setDestination(toGrpcPosition(unit.destination()))
                .setCurrentHealth(unit.currentHp())
                .setMaxHealth(unit.maxHp())
                .setSpeed(unit.speed())
                .build();
    }

    public static CompleteUnitInfoDTO fromGrpcUnitInfo(proto.PlayerUnit unit) {
        return new CompleteUnitInfoDTO(unit.getUnitId(), unit.getTargetUnitId(), fromGrpcPosition(unit.getPosition()), fromGrpcPosition(unit.getDestination()), unit.getCurrentHealth(), unit.getMaxHealth(), unit.getSpeed());
    }

    public static proto.PlayerUnits toGrpcUpdatePlayerUnits(PlayerUnitUpdateRequestDTO playerUnitUpdateRequest) {
        proto.PlayerUnits.Builder builder = proto.PlayerUnits.newBuilder()
                .setUserId(playerUnitUpdateRequest.playerId());
        for (CompleteUnitInfoDTO unit : playerUnitUpdateRequest.unitUpdates()) {
            builder.addUnits(toGrpcPlayerUnit(unit));
        }
        return builder.build();
    }

    public static PlayerUnitUpdateRequestDTO fromGrpcUpdatePlayerUnits(proto.PlayerUnits updateRequest) {
        ArrayList<CompleteUnitInfoDTO> unitUpdates = new ArrayList<>();
        for (proto.PlayerUnit unit : updateRequest.getUnitsList()) {
            unitUpdates.add(fromGrpcUnitInfo(unit));
        }
        return new PlayerUnitUpdateRequestDTO(unitUpdates, updateRequest.getUserId());
    }
}
