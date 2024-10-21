package se.umu.cs.ads.sp.model.objects.collectables;

import se.umu.cs.ads.sp.model.map.Map;
import se.umu.cs.ads.sp.utils.Position;

public class Chest extends Collectable {

    public Chest(Position pos, Map map) {
        super(pos, map);
    }

    @Override
    public void pickUp(Map map) {
        hasBeenCollected = true;
    }
}
