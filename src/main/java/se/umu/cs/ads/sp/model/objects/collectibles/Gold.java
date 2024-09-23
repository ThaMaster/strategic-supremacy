package se.umu.cs.ads.sp.model.objects.collectibles;

public class Gold extends Collectible {
    private int amount;

    public Gold() {
        super();
    }

    @Override
    public void pickUp() {

        destroy();
    }
}
