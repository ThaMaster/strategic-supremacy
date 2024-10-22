package se.umu.cs.ads.sp.model.objects.environment;

import se.umu.cs.ads.sp.model.map.Map;
import se.umu.cs.ads.sp.utils.Position;

public class Base extends Environment {

    public Base(Position pos, Map map) {
        super(pos, map);
    }

    public Base(Position pos, Map map, long id) {
        super(pos, map);
        this.id = id;
    }

}
