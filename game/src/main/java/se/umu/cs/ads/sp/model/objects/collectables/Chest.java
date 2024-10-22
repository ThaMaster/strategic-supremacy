package se.umu.cs.ads.sp.model.objects.collectables;

import se.umu.cs.ads.sp.model.map.Map;
import se.umu.cs.ads.sp.utils.Position;

public class Chest extends Collectable {

    public Chest(Position pos, Map map) {
        super(pos, map);
    }

    public Chest(Position pos, Map map, Long id) {
        super(pos, map, id);
    }

    @Override
    public void pickUp(Map map) {
        hasBeenCollected = true;
    }
}
