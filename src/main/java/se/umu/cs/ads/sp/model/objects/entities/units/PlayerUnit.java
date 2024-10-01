package se.umu.cs.ads.sp.model.objects.entities.units;

import org.checkerframework.checker.units.qual.C;
import se.umu.cs.ads.sp.model.components.CollisionBox;
import se.umu.cs.ads.sp.model.components.Cooldown;
import se.umu.cs.ads.sp.model.map.Map;
import se.umu.cs.ads.sp.model.objects.GameObject;
import se.umu.cs.ads.sp.model.objects.GoldMine;
import se.umu.cs.ads.sp.model.objects.collectables.Collectable;
import se.umu.cs.ads.sp.model.objects.collectables.Gold;
import se.umu.cs.ads.sp.model.objects.collectables.Reward;
import se.umu.cs.ads.sp.model.objects.entities.Entity;
import se.umu.cs.ads.sp.utils.Position;
import se.umu.cs.ads.sp.utils.enums.EntityState;

import java.util.ArrayList;

public class PlayerUnit extends Entity {

    private ArrayList<Collectable> collected = new ArrayList<>();
    private Cooldown miningCooldown;
    private Cooldown shootCooldown;
    private GoldMine goldMine;
    private PlayerUnit targetedUnit;
    private final int attack;
    private int attackBuff;

    public PlayerUnit(Position startPos, Map map) {
        super(startPos, map);
        miningCooldown = new Cooldown(3);
        this.baseHp = 100;
        this.maxHp = baseHp;
        this.currentHp = maxHp;
        this.attack = 10;
        this.attackBuff = 0;
        this.attackRange = 250;
        this.attackBox = new CollisionBox(position, attackRange, attackRange);
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
            case MINING:
                if(miningCooldown.hasElapsed() && this.goldMine.hasResourceLeft()){
                    Collectable coin = new Gold(this.position, map);
                    goldMine.harvestGold(1);
                    coin.setReward(new Reward(1, Reward.RewardType.GOLD));
                    this.collected.add(coin);
                    coin.destroy(map); //Remove the coin from the map after adding it to collected, so it cant get picked up
                    miningCooldown.reset();
                }
                break;
            case DEAD:
                System.out.println("Unit is dead!");
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
                        continue;
                    }
                }
                if(this.position.equals(getDestination()) && coll.get(i) instanceof GoldMine) {
                    //Have reached destination and I am next to a goldmine, start mining :)
                    goldMine = (GoldMine) coll.get(i);
                    startMining();
                }
            }
        }
    }

    public ArrayList<Collectable> getCollected() {
        return this.collected;
    }

    public void setAttackBuff(int newBuff) {
        this.attackBuff = newBuff;
    }

    public void attack(Entity e) {
        e.takeDamage(attack + attackBuff);
    }
    private void startMining(){
        if(this.state == EntityState.MINING){
            return;
        }
        this.state = EntityState.MINING;
        miningCooldown.start();
    }

}
