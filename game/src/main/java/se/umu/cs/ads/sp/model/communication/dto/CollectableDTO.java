package se.umu.cs.ads.sp.model.communication.dto;

import se.umu.cs.ads.sp.model.objects.collectables.Reward;
import se.umu.cs.ads.sp.utils.Position;

public record CollectableDTO(
        long id,
        Position position,
        String type,
        Reward reward
) {
}