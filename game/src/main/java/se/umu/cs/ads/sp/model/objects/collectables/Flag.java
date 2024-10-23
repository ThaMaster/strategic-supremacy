package se.umu.cs.ads.sp.model.objects.collectables;

import se.umu.cs.ads.sp.model.map.Map;
import se.umu.cs.ads.sp.utils.Position;
import se.umu.cs.ads.sp.utils.enums.CollectableType;

public class Flag extends Collectable {

    public Flag(Position pos, Map map) {
        super(pos, map);
        this.type = CollectableType.FLAG;
    }

    public Flag(Position pos, Map map, long id) {
        super(pos, map, id);
        this.type = CollectableType.FLAG;
    }

}
