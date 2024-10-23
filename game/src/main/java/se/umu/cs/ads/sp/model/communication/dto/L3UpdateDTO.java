package se.umu.cs.ads.sp.model.communication.dto;

import java.util.ArrayList;

public record L3UpdateDTO(
        int msgCount,
        ArrayList<UserSkeletonsDTO> entities,
        ArrayList<Long> pickedUpCollectables,
        Long remainingTime,
        Long currentScoreLeader,
        UserScoreDTO scoreInfo,
        ArrayList<EnvironmentDTO> environments,
        int msgSeverity
) {
}
