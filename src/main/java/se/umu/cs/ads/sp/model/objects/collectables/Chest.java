package se.umu.cs.ads.sp.model.objects.collectables;

import se.umu.cs.ads.sp.model.objects.GameObject;
import se.umu.cs.ads.sp.utils.Position;

public class Chest extends Collectable {

    private boolean open = false;
    private final GameObject containingItem;

    public Chest(Position pos, GameObject item) {
        super(pos);
        this.containingItem = item;
    }

    @Override
    public void pickUp() {
        open = true;
    }

    public GameObject collectItem() {
        if(!open) {
            return containingItem;
        }
        return null;
    }


}
