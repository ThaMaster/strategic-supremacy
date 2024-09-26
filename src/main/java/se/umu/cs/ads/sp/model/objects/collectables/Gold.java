package se.umu.cs.ads.sp.model.objects.collectables;

import se.umu.cs.ads.sp.model.objects.GameObject;
import se.umu.cs.ads.sp.utils.Position;

public class Gold extends Collectable {
    private int amount;

    public Gold(Position pos) {
        super(pos);
    }

    @Override
    public void pickUp() {
        destroy();
    }

    @Override
    public void getReward() {

    }
}
