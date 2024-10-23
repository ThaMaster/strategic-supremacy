package se.umu.cs.ads.sp.model.objects.collectables;

import se.umu.cs.ads.sp.model.map.Map;
import se.umu.cs.ads.sp.util.Position;
import se.umu.cs.ads.sp.util.enums.CollectableType;

public class Gold extends Collectable {
    public Gold(Position pos, Map map) {
        super(pos, map);
        this.type = CollectableType.GOLD;
    }

    public Gold(Position pos, Map map, Long id) {
        super(pos, map, id);
        this.type = CollectableType.GOLD;
    }
}
