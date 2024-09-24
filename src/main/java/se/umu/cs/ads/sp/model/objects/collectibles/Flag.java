package se.umu.cs.ads.sp.model.objects.collectibles;

public class Flag extends Collectable {
    @Override
    public void pickUp() {

        destroy();
    }
}
