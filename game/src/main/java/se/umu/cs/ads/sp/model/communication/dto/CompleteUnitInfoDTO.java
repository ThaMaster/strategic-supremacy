package se.umu.cs.ads.sp.model.communication.dto;

import se.umu.cs.ads.sp.utils.Position;

public record CompleteUnitInfoDTO(
        long unitId,
        long targetUnitId,
        Position position,
        Position destination,
        int maxHp,
        int currentHp,
        int speed) { }
