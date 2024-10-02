package se.umu.cs.ads.sp.model.objects.collectables;

import se.umu.cs.ads.sp.model.map.Map;
import se.umu.cs.ads.sp.model.objects.GameObject;
import se.umu.cs.ads.sp.utils.Position;

public class Gold extends Collectable {
    private int amount;
    private boolean open = false;
    public Gold(Position pos, Map map) {
        super(pos);
        this.spawn(map);
    }
}
