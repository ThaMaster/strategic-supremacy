package se.umu.cs.ads.sp.view.objects.collectables;

import se.umu.cs.ads.sp.utils.Position;
import se.umu.cs.ads.sp.view.objects.ObjectView;

public abstract class CollectableView extends ObjectView {
    public boolean hasPlayedSoundFx = false;
    public CollectableView(Position pos) {
        super(pos);
    }
    public abstract void pickup();
}
