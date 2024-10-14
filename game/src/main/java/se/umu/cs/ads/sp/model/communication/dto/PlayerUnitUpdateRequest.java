package se.umu.cs.ads.sp.model.communication.dto;

import se.umu.cs.ads.sp.model.objects.entities.units.PlayerUnit;

import java.util.ArrayList;

public record PlayerUnitUpdateRequest(ArrayList<UnitInfoDTO> unitUpdates, long playerId) {}