package se.umu.cs.ads.sp.model.communication.dto;

import se.umu.cs.ads.sp.utils.Position;

public record EntitySkeletonDTO(
        long id,
        String unitType,
        Position position
) {
}