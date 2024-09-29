package se.umu.cs.ads.sp.model.objects.entities.units;

import se.umu.cs.ads.sp.model.Cooldown;
import se.umu.cs.ads.sp.model.map.Map;
import se.umu.cs.ads.sp.model.objects.GameObject;
import se.umu.cs.ads.sp.model.objects.collectables.Collectable;
import se.umu.cs.ads.sp.model.objects.collectables.Gold;
import se.umu.cs.ads.sp.model.objects.collectables.Reward;
import se.umu.cs.ads.sp.model.objects.entities.Entity;
import se.umu.cs.ads.sp.utils.Position;

import java.util.ArrayList;

public class PlayerUnit extends Entity {

    private ArrayList<Collectable> collected;
    private Cooldown miningCooldown;
    private Cooldown shootCooldown;

    public PlayerUnit(Position startPos, Map map) {
        super(startPos, map);
        collected = new ArrayList<>();
    }

    @Override
    public void update(){
        switch (state) {
            case IDLE:
                break;
            case RUNNING:
                move();
                checkCollision();
                break;
            case ATTACKING:
                break;
            case TAKING_DAMAGE:
                break;
            default:
                break;
        }
    }

    public void checkCollision() {
        ArrayList<Position> corners = this.getCollisionBox().getCorners();
        for (Position corner : corners) {
            ArrayList<GameObject> coll = map.getInhabitants(corner);
            for(int i = coll.size()-1; i >= 0; i--) {
                if (coll.get(i) instanceof Collectable collectable) {
                    if(this.getCollisionBox().checkCollision(coll.get(i).getCollisionBox())){
                        this.collected.add(collectable);
                        collectable.pickUp(map); //This removes the collectable from the map
                    }
                }
            }
        }
    }

    public ArrayList<Collectable> getCollected() {
        return this.collected;
    }

}
