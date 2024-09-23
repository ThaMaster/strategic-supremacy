package se.umu.cs.ads.sp.model.objects.collectibles;

public class Flag extends Collectible {
    @Override
    public void pickUp() {

        destroy();
    }
}
