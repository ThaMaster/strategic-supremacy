package se.umu.cs.ads.sp.model.objects.collectibles;

import se.umu.cs.ads.sp.model.objects.GameObject;

public class Chest extends Collectable {
    public GameObject containingItem;

    public Chest(GameObject item) {
        this.containingItem = item;
    }

    @Override
    public void pickUp() {

    }
}
