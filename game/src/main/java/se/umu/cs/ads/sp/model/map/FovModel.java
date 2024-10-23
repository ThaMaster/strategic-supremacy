package se.umu.cs.ads.sp.model.map;

import se.umu.cs.ads.sp.model.objects.entities.units.PlayerUnit;
import se.umu.cs.ads.sp.util.Constants;
import se.umu.cs.ads.sp.util.Position;

import java.util.ArrayList;

public class FovModel {

    private ArrayList<PlayerUnit> units;
    private final int fowRange = Constants.FIELD_OF_VIEW_RADIUS;

    public FovModel(ArrayList<PlayerUnit> units) {
        this.units = units;
    }

    public boolean isInFov(Position posToCheck) {
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

    public int getFowRange() {
        return fowRange;
    }

}
