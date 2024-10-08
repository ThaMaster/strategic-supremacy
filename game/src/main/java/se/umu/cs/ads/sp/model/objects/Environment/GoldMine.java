package se.umu.cs.ads.sp.model.objects.Environment;
import se.umu.cs.ads.sp.utils.Position;

public class GoldMine extends Environment {

    private int goldReserve;

    public GoldMine(Position pos, int goldReserve) {
        super(pos);
        this.goldReserve = goldReserve;
    }

    public void harvestGold(int amount) {
        goldReserve -= amount;
    }

    public boolean hasResourceLeft() {
        return goldReserve >= 0;
    }

}
