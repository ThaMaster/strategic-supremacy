package se.umu.cs.ads.sp.model.objects.collectables;

import se.umu.cs.ads.sp.model.map.Map;
import se.umu.cs.ads.sp.model.objects.GameObject;
import se.umu.cs.ads.sp.utils.Position;

public abstract class Collectable extends GameObject {
    protected boolean hasBeenCollected;
    protected Reward reward;
    public Collectable(Position pos) {
        super(pos);
        hasBeenCollected = false;
    }

    public void pickUp(Map map){
        this.destroy(map);
    }

    public void setReward(Reward reward){
        this.reward = reward;
    }

    public Reward getReward(){
        return this.reward;
    }
}
