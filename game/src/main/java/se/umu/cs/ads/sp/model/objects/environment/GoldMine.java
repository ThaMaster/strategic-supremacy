package se.umu.cs.ads.sp.model.objects.environment;

import se.umu.cs.ads.sp.events.GameEvents;
import se.umu.cs.ads.sp.model.map.Map;
import se.umu.cs.ads.sp.util.Position;

public class GoldMine extends Environment {

    private int goldReserve;

    public GoldMine(Position pos, Map map, int goldReserve) {
        super(pos, map);
        this.goldReserve = goldReserve;
    }

    public GoldMine(Position pos, Map map, int goldReserve, long id) {
        super(pos, map);
        this.goldReserve = goldReserve;
        this.id = id;
    }

    public void harvestGold(int amount) {
        goldReserve -= amount;
    }

    public boolean hasResourceLeft() {
        return goldReserve > 0;
    }

    public int getRemainingResource() {
        return goldReserve;
    }

    public void setResource(int amount) {
        this.goldReserve = amount;
    }

}
