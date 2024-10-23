package se.umu.cs.ads.sp.model.objects.environment;

import se.umu.cs.ads.sp.model.map.Map;
import se.umu.cs.ads.sp.model.objects.GameObject;
import se.umu.cs.ads.sp.util.Position;

public abstract class Environment extends GameObject {

    public Environment(Position pos, Map map) {
        super(pos, map);
    }

    public Environment(Position pos, Map map, long id) {
        super(pos, map);
        this.id = id;
    }

}
