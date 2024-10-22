package se.umu.cs.ads.sp.model.communication.dto;

import se.umu.cs.ads.sp.utils.Position;

public record EntityDTO(
        long unitId,
        long targetUnitId,
        Position position,
        Position destination,
        int maxHp,
        int currentHp,
        int speedBuff,
        int attackBuff
) {
}
