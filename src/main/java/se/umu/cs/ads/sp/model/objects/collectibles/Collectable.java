package se.umu.cs.ads.sp.model.objects.collectibles;

import se.umu.cs.ads.sp.model.objects.GameObject;

public abstract class Collectable extends GameObject {

    private String pickupText;

    public abstract void pickUp();
}
