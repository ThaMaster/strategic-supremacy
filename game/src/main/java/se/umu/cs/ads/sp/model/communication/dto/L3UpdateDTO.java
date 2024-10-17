package se.umu.cs.ads.sp.model.communication.dto;

import java.util.ArrayList;

public record L3UpdateDTO(
        ArrayList<EntitySkeletonDTO> entities,
        ArrayList<Long> pickedUpCollectables,
        Long remainingTime,
        Long currentScoreLeader,
        ArrayList<EnvironmentDTO> environments,
        int msgSeverity
)
{}
/*

  EntitySkeletons entities = 1;
  repeated int64 pickedUpCollectables = 2;
  int64 remainingTime = 3;
  int64 currentScoreLeader = 4;
  repeated Environment environments = 5;
  int32 severity = 6;
 */