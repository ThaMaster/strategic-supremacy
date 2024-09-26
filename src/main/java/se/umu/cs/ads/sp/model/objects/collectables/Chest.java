package se.umu.cs.ads.sp.model.objects.collectables;

import se.umu.cs.ads.sp.model.map.Map;
import se.umu.cs.ads.sp.model.objects.GameObject;
import se.umu.cs.ads.sp.utils.Position;

public class Chest extends Collectable {

    private boolean open = false;
    private final GameObject containingItem;

    //woifnaoif reward;

    public Chest(Position pos, GameObject item, Map map) {
        super(pos);
        this.containingItem = item;
        this.spawn(map);
    }

    @Override
    public void pickUp() {
        open = true;
    }

    public GameObject collectItem() {
        if(!open) {
            return containingItem;
        }
        return null;
    }

    @Override
    public void getReward(){
        if(!hasBeenCollected){
            hasBeenCollected = true;
            System.out.println("You got 50 G");
        }
    }


}
