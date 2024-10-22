package se.umu.cs.ads.sp.model.communication.dto;

import java.util.ArrayList;

public record L2UpdateDTO(
        long userId,
        ArrayList<EntitySkeletonDTO> entities,
        ArrayList<Long> pickedUpCollectables,
        ArrayList<EnvironmentDTO> environments,
        int msgSeverity
) {
}
