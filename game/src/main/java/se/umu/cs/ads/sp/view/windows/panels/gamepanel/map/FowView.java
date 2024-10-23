package se.umu.cs.ads.sp.view.windows.panels.gamepanel.map;

import se.umu.cs.ads.sp.util.AppSettings;
import se.umu.cs.ads.sp.util.Position;
import se.umu.cs.ads.sp.view.objects.entities.units.PlayerUnitView;

import java.util.ArrayList;

public class FowView {
    private ArrayList<PlayerUnitView> units;
    private final int fowRange;

    public FowView(ArrayList<PlayerUnitView> units) {
        this.units = units;
        this.fowRange = 250;
    }

    public boolean isInFow(Position posToCheck) {
        if(AppSettings.DEBUG){
            return true;
        }
        for (PlayerUnitView unit : units) {
            if (Position.distance(unit.getPosition(), posToCheck) < fowRange) {
                return true;
            }
        }
        return false;
    }

    public void updateUnitPositions(ArrayList<PlayerUnitView> units) {
        this.units = units;
    }
}
