package se.umu.cs.ads.sp.model.objects.collectables;

import se.umu.cs.ads.sp.model.map.Map;
import se.umu.cs.ads.sp.model.objects.GameObject;
import se.umu.cs.ads.sp.utils.Position;

public class Chest extends Collectable {

    private boolean open = false;

    public Chest(Position pos, Map map) {
        super(pos);
        this.spawn(map);
    }

    @Override
    public void pickUp(Map map) {
        open = true;
    }
}
