package se.umu.cs.ads.sp.model.communication.dto;

import se.umu.cs.ads.sp.utils.Position;

public record UnitDTO(
        long unitId,
        long targetUnitId,
        String unitType,
        Position position,
        Position destination,
        int maxHp,
        int currentHp,
        int speedBuff,
        int attackBuff
) {
}
