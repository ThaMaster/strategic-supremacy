package se.umu.cs.ads.sp.model.communication.dto;

import java.util.ArrayList;

public record PlayerUnitUpdateRequestDTO(ArrayList<CompleteUnitInfoDTO> unitUpdates, long playerId) {}