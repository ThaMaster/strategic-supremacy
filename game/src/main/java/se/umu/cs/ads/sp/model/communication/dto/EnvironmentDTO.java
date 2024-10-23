package se.umu.cs.ads.sp.model.communication.dto;

import se.umu.cs.ads.sp.util.Position;

public record EnvironmentDTO(
        long id,
        long userId,
        Position position,
        String type,
        int remainingResource) {
}
