package se.umu.cs.ads.sp.model.communication.dto;

import se.umu.cs.ads.sp.util.enums.EntityState;

public record EntityStateDTO(EntityState state, Long unitId, Long attacker) {}
