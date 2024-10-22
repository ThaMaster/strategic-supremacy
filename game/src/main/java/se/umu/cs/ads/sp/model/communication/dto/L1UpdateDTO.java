package se.umu.cs.ads.sp.model.communication.dto;

import java.util.ArrayList;

public record L1UpdateDTO(
        long userId,
        ArrayList<UnitDTO> entities,
        int msgSeverity
) {
}