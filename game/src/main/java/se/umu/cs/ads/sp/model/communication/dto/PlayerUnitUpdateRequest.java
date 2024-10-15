package se.umu.cs.ads.sp.model.communication.dto;

import java.util.ArrayList;

public record PlayerUnitUpdateRequest(ArrayList<CompleteUnitInfoDTO> unitUpdates, long playerId) {}