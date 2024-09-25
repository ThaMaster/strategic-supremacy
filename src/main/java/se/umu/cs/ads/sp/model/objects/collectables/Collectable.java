package se.umu.cs.ads.sp.model.objects.collectables;

import se.umu.cs.ads.sp.model.objects.GameObject;
import se.umu.cs.ads.sp.utils.Position;

public abstract class Collectable extends GameObject {

    public Collectable(Position pos) {
        super(pos);
    }
    private String pickupText;

    public abstract void pickUp();
}
