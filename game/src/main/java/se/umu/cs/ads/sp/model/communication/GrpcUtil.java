package se.umu.cs.ads.sp.model.communication;

import se.umu.cs.ads.ns.app.Lobby;
import se.umu.cs.ads.ns.app.User;
import se.umu.cs.ads.sp.model.communication.dto.*;
import se.umu.cs.ads.sp.model.objects.collectables.Reward;
import se.umu.cs.ads.sp.util.Position;
import se.umu.cs.ads.sp.util.enums.RewardType;

import java.util.ArrayList;

public class GrpcUtil {


    public static proto.Position toGrpcPosition(int x, int y) {
        return proto.Position.newBuilder()
                .setX(x)
                .setY(y)
                .build();
    }

    public static proto.Position toGrpcPosition(Position position) {
        proto.Position.Builder builder = proto.Position.newBuilder();
        if (position != null) {
            builder.setX(position.getX())
                    .setY(position.getY());
        }
        return builder.build();
    }

    public static Position fromGrpcPosition(proto.Position pos) {
        return new Position(pos.getX(), pos.getY());
    }

    public static proto.EntitySkeleton toGrpcEntitySkeleton(EntitySkeletonDTO skeleton) {
        return proto.EntitySkeleton.newBuilder()
                .setId(skeleton.id())
                .setUnitType(skeleton.unitType())
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
                .setReward(reward.getType().label)
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

        for (UserSkeletonsDTO skeletons : req.userSkeletons()) {
            builder.addEntities(toGrpcUserSkeletons(skeletons));
        }

        for (EnvironmentDTO environment : req.environments()) {
            builder.addEnvironments(toGrpcEnvironment(environment));
        }

        for (CollectableDTO collectable : req.collectables()) {
            builder.addCollectables(toGrpcCollectable(collectable));
        }

        return builder.build();
    }

    public static proto.UserSkeletons toGrpcUserSkeletons(UserSkeletonsDTO UserSkeletons) {
        proto.UserSkeletons.Builder builder = proto.UserSkeletons.newBuilder();
        builder.setUserId(UserSkeletons.userId());
        for (EntitySkeletonDTO skeleton : UserSkeletons.entities()) {
            builder.addSkeletons(toGrpcEntitySkeleton(skeleton));
        }
        return builder.build();
    }

    public static EntitySkeletonDTO fromGrpcSkeleton(proto.EntitySkeleton skeletons) {
        return new EntitySkeletonDTO(
                skeletons.getId(),
                skeletons.getUnitType(),
                fromGrpcPosition(skeletons.getPosition()));
    }

    public static UserSkeletonsDTO fromGrpcUserSkeletons(proto.UserSkeletons grpcSkeletons) {
        ArrayList<EntitySkeletonDTO> skeletons = new ArrayList<>();
        for (int i = 0; i < grpcSkeletons.getSkeletonsCount(); i++) {
            skeletons.add(fromGrpcSkeleton(grpcSkeletons.getSkeletons(i)));
        }
        return new UserSkeletonsDTO(grpcSkeletons.getUserId(), skeletons);
    }

    public static EnvironmentDTO fromGrpcEnv(proto.Environment env) {
        return new EnvironmentDTO(env.getId(), env.getUserId(), fromGrpcPosition(env.getPosition()), env.getType(), env.getRemainingResource());
    }

    public static Reward fromGrpcReward(proto.Reward reward) {
        return new Reward(reward.getQuantity(), RewardType.fromLabel(reward.getReward()));
    }

    public static CollectableDTO fromGrpcCollectable(proto.Collectable collectable) {
        return new CollectableDTO(collectable.getId(),
                fromGrpcPosition(collectable.getPosition()),
                collectable.getType(),
                fromGrpcReward(collectable.getReward()));
    }

    public static StartGameRequestDTO fromGrpcStartGameReq(proto.StartGameRequest req) {
        StartGameRequestDTO data = new StartGameRequestDTO(new ArrayList<>(), new ArrayList<>(), new ArrayList<>());

        for (proto.UserSkeletons skeletons : req.getEntitiesList()) {
            data.addSkeleton(fromGrpcUserSkeletons(skeletons));
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

    public static proto.UserId toGrpcUserId(Long userId) {
        return proto.UserId.newBuilder().setUserId(userId).build();
    }

    public static proto.CandidateLeaderRequest toGrpcLeaderRequest(LeaderRequestDto request) {
        return proto.CandidateLeaderRequest.newBuilder()
                .setMsgCount(request.msgCount())
                .setUserId(request.userId())
                .build();
    }

    public static proto.PlayerUnit toGrpcPlayerUnit(UnitDTO unit) {
        return proto.PlayerUnit.newBuilder()
                .setUnitId(unit.unitId())
                .setTargetUnitId(unit.targetUnitId())
                .setFlagId(unit.flagId())
                .setPosition(toGrpcPosition(unit.position()))
                .setDestination(toGrpcPosition(unit.destination()))
                .setMaxHp(unit.maxHp())
                .setCurrentHp(unit.currentHp())
                .setSpeedBuff(unit.speedBuff())
                .setAttackBuff(unit.attackBuff())
                .build();
    }

    public static UnitDTO fromGrpcUnitInfo(proto.PlayerUnit unit) {
        return new UnitDTO(
                unit.getUnitId(),
                unit.getTargetUnitId(),
                unit.getFlagId(),
                unit.getUnitType(),
                fromGrpcPosition(unit.getPosition()),
                fromGrpcPosition(unit.getDestination()),
                unit.getMaxHp(),
                unit.getCurrentHp(),
                unit.getSpeedBuff(),
                unit.getAttackBuff());
    }

    public static proto.UserScore toGrpcUserScore(UserScoreDTO score) {
        return proto.UserScore.newBuilder()
                .setId(score.userId())
                .setScore(score.score())
                .build();
    }

    public static UserScoreDTO fromGrpcUserScore(proto.UserScore score) {
        return new UserScoreDTO(score.getId(), score.getScore());
    }

    public static proto.L3Message toGrpcL3Message(L3UpdateDTO message) {
        proto.L3Message.Builder builder = proto.L3Message.newBuilder();

        builder.setMessageCount(message.msgCount());
        for (long pickedUpCollectables : message.pickedUpCollectables()) {
            builder.addPickedUpCollectables(pickedUpCollectables);
        }
        for (EnvironmentDTO env : message.environments()) {
            builder.addEnvironments(toGrpcEnvironment(env));
        }

        for (UserSkeletonsDTO skeletons : message.entities()) {
            builder.addEntities(toGrpcUserSkeletons(skeletons));
        }

        for (CollectableDTO newColl : message.spawnedCollectables()) {
            builder.addSpawnedCollectables(toGrpcCollectable(newColl));
        }

        builder.setRemainingTime(message.remainingTime())
                .setCurrentScoreLeader(message.currentScoreLeader())
                .setUserScore(toGrpcUserScore(message.scoreInfo()))
                .setSeverity(message.msgSeverity());

        return builder.build();
    }

    public static L3UpdateDTO fromGrpcL3Message(proto.L3Message message) {
        ArrayList<UserSkeletonsDTO> skeletons = new ArrayList<>();
        ArrayList<EnvironmentDTO> environments = new ArrayList<>();
        ArrayList<Long> pickedUp = new ArrayList<>();
        ArrayList<CollectableDTO> spawned = new ArrayList<>();

        for (int i = 0; i < message.getEntitiesCount(); i++) {
            skeletons.add(fromGrpcUserSkeletons(message.getEntities(i)));
        }
        for (int i = 0; i < message.getEnvironmentsCount(); i++) {
            environments.add(fromGrpcEnv(message.getEnvironments(i)));
        }
        for (int i = 0; i < message.getPickedUpCollectablesCount(); i++) {
            pickedUp.add(message.getPickedUpCollectables(i));
        }
        for (int i = 0; i < message.getSpawnedCollectablesCount(); i++) {
            spawned.add(fromGrpcCollectable(message.getSpawnedCollectables(i)));
        }


        return new L3UpdateDTO(
                message.getMessageCount(),
                skeletons,
                pickedUp,
                spawned,
                message.getRemainingTime(),
                message.getCurrentScoreLeader(),
                fromGrpcUserScore(message.getUserScore()),
                environments,
                message.getSeverity()
        );
    }

    public static proto.L2Message toGrpcL2Message(L2UpdateDTO message) {
        proto.L2Message.Builder builder = proto.L2Message.newBuilder();

        for (long pickedUpCollectables : message.pickedUpCollectables()) {
            builder.addPickedUpCollectables(pickedUpCollectables);
        }
        for (EnvironmentDTO env : message.environments()) {
            builder.addEnvironments(toGrpcEnvironment(env));
        }

        for (EntitySkeletonDTO skeletons : message.entities()) {
            builder.addEntities(toGrpcEntitySkeleton(skeletons));
        }

        for (CollectableDTO newColl : message.spawnedCollectables()) {
            builder.addSpawnedCollectables(toGrpcCollectable(newColl));
        }

        builder.setUserId(message.userId())
                .setSeverity(message.msgSeverity());
        return builder.build();
    }

    public static L2UpdateDTO fromGrpcL2Message(proto.L2Message message) {
        ArrayList<EntitySkeletonDTO> skeletons = new ArrayList<>();
        ArrayList<EnvironmentDTO> environments = new ArrayList<>();
        ArrayList<Long> pickedUp = new ArrayList<>();
        ArrayList<CollectableDTO> spawned = new ArrayList<>();

        for (int i = 0; i < message.getEntitiesCount(); i++) {
            skeletons.add(fromGrpcSkeleton(message.getEntities(i)));
        }

        for (int i = 0; i < message.getEnvironmentsCount(); i++) {
            environments.add(fromGrpcEnv(message.getEnvironments(i)));
        }

        for (int i = 0; i < message.getPickedUpCollectablesCount(); i++) {
            pickedUp.add(message.getPickedUpCollectables(i));
        }

        for (int i = 0; i < message.getSpawnedCollectablesCount(); i++) {
            spawned.add(fromGrpcCollectable(message.getSpawnedCollectables(i)));
        }

        return new L2UpdateDTO(
                message.getUserId(),
                skeletons,
                pickedUp,
                spawned,
                environments,
                message.getSeverity()
        );
    }

    public static proto.L1Message toGrpcL1Message(L1UpdateDTO message) {
        proto.L1Message.Builder builder = proto.L1Message.newBuilder()
                .setUserId(message.userId())
                .setSeverity(message.msgSeverity());
        for (UnitDTO unit : message.entities()) {
            builder.addUnits(toGrpcPlayerUnit(unit));
        }
        return builder.build();
    }

    public static L1UpdateDTO fromGrpcL1Message(proto.L1Message message) {
        ArrayList<UnitDTO> units = new ArrayList<>();
        for (proto.PlayerUnit unit : message.getUnitsList()) {
            units.add(fromGrpcUnitInfo(unit));
        }
        return new L1UpdateDTO(message.getUserId(), units, message.getSeverity());
    }
}