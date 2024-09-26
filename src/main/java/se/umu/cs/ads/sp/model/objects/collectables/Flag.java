package se.umu.cs.ads.sp.model.objects.collectables;

import se.umu.cs.ads.sp.utils.Position;

public class Flag extends Collectable {

    public Flag(Position pos) {
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
