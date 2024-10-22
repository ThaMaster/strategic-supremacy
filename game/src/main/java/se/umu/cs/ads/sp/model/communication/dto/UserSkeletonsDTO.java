package se.umu.cs.ads.sp.model.communication.dto;

import java.util.ArrayList;

public record UserSkeletonsDTO(
        long userId,
        ArrayList<EntitySkeletonDTO> entities
) {

    public void addSkeleton(EntitySkeletonDTO skeleton) {
        entities.add(skeleton);
    }
}
