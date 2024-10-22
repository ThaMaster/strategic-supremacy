package se.umu.cs.ads.sp.model.objects.collectables;

import se.umu.cs.ads.sp.model.map.Map;
import se.umu.cs.ads.sp.utils.Position;

public class Flag extends Collectable {

    public Flag(Position pos, Map map) {
        super(pos, map);
    }

    public Flag(Position pos, Map map, long id) {
        super(pos, map);
        this.id = id;
    }

}
