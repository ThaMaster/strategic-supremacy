package se.umu.cs.ads.sp.model.map;

import se.umu.cs.ads.sp.model.objects.entities.units.PlayerUnit;
import se.umu.cs.ads.sp.utils.Position;

import java.util.ArrayList;

public class FowModel {

    private ArrayList<PlayerUnit> units;
    private final int fowRange;

    public FowModel(ArrayList<PlayerUnit> units) {
        this.units = units;
        this.fowRange = 250;
    }

    public boolean isInFow(Position posToCheck) {
        for (PlayerUnit unit : units) {
            if (Position.distance(unit.getPosition(), posToCheck) < fowRange) {
                return true;
            }
        }
        return false;
    }

    public void updateUnitPositions(ArrayList<PlayerUnit> unitPositions) {
        this.units = unitPositions;
    }

}
