package se.umu.cs.ads.sp.view.objects.collectables;

import se.umu.cs.ads.sp.utils.Position;
import se.umu.cs.ads.sp.view.objects.ObjectView;

public abstract class CollectableView extends ObjectView {
    protected boolean hasBeenPickedUp = false;
    public CollectableView(long id, Position pos) {
        super(id, pos);
    }
    public abstract void pickup();
    public boolean hasBeenCollected() {
        return hasBeenPickedUp;
    }
}
