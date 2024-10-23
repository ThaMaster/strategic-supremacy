package se.umu.cs.ads.sp.model.objects.collectables;

import se.umu.cs.ads.sp.model.map.Map;
import se.umu.cs.ads.sp.util.Position;
import se.umu.cs.ads.sp.util.enums.CollectableType;

public class Chest extends Collectable {

    public Chest(Position pos, Map map) {
        super(pos, map);
        this.type = CollectableType.CHEST;
    }

    public Chest(Position pos, Map map, Long id) {
        super(pos, map, id);
        this.type = CollectableType.CHEST;
    }

    @Override
    public void pickUp(Map map) {
        hasBeenCollected = true;
    }
}
