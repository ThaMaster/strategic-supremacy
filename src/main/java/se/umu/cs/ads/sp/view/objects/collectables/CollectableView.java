package se.umu.cs.ads.sp.view.objects.collectables;

import se.umu.cs.ads.sp.utils.Position;
import se.umu.cs.ads.sp.view.objects.ObjectView;

public abstract class CollectableView extends ObjectView {
    public boolean hasPlayedSoundFx = false;
    public CollectableView(long id, Position pos) {
        super(id, pos);
    }
    public abstract void pickup();
}
