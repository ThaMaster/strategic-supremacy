package se.umu.cs.ads.sp.model.objects.entities.units;

import se.umu.cs.ads.sp.events.GameEvent;
import se.umu.cs.ads.sp.events.GameEvents;
import se.umu.cs.ads.sp.model.components.CollisionBox;
import se.umu.cs.ads.sp.model.map.Map;
import se.umu.cs.ads.sp.model.objects.GameObject;
import se.umu.cs.ads.sp.model.objects.environment.GoldMine;
import se.umu.cs.ads.sp.model.objects.collectables.Collectable;
import se.umu.cs.ads.sp.model.objects.collectables.Gold;
import se.umu.cs.ads.sp.model.objects.collectables.Reward;
import se.umu.cs.ads.sp.model.objects.entities.Entity;
import se.umu.cs.ads.sp.utils.Cooldown;
import se.umu.cs.ads.sp.utils.Position;
import se.umu.cs.ads.sp.utils.enums.EntityState;
import se.umu.cs.ads.sp.utils.enums.EventType;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class PlayerUnit extends Entity {

    private ArrayList<Collectable> collected = new ArrayList<>();
    private Cooldown miningCooldown;
    private Cooldown shootCooldown;
    private GoldMine goldMine;
    private PlayerUnit targetedUnit;
    private final int attack;
    protected int attackRange;
    private int attackBuff;
    private boolean inAttackRange = false;
    private boolean attacked = false;
    private boolean hit = false;

    public PlayerUnit(Position startPos, Map map) {
        super(startPos, map);
        miningCooldown = new Cooldown(3, TimeUnit.SECONDS);
        shootCooldown = new Cooldown(1, TimeUnit.SECONDS);
        hitCooldown = new Cooldown(250, TimeUnit.MILLISECONDS);
        this.baseHp = 100;
        this.maxHp = baseHp;
        this.currentHp = maxHp;
        this.attack = 10;
        this.attackBuff = 0;
        this.attackRange = 150;
        this.attackBox = new CollisionBox(position, attackRange, attackRange);
    }

    public PlayerUnit(long id, Position startPos, Map map) {
        super(startPos, map);
        miningCooldown = new Cooldown(3, TimeUnit.SECONDS);
        shootCooldown = new Cooldown(1, TimeUnit.SECONDS);
        hitCooldown = new Cooldown(250, TimeUnit.MILLISECONDS);
        this.baseHp = 100;
        this.maxHp = baseHp;
        this.currentHp = maxHp;
        this.attack = 10;
        this.attackBuff = 0;
        this.attackRange = 150;
        this.attackBox = new CollisionBox(position, attackRange, attackRange);
        this.id = id;
    }

    @Override
    public void update() {
        switch (state) {
            case IDLE:
                break;
            case RUNNING:
                move();
                checkCollision();
                break;
            case ATTACKING:
                this.destination = targetedUnit.position;
                if (shootCooldown.hasElapsed()) {
                    if ((Position.distance(position, destination) > attackRange)) {
                        inAttackRange = false;
                        attacked = false;
                        move();
                    } else if (targetedUnit.getState() != EntityState.DEAD) {
                        inAttackRange = true;
                        attacked = true;
                        attack();
                        shootCooldown.reset();
                    } else {
                        this.state = EntityState.IDLE;
                    }
                } else {
                    attacked = false;
                }
                break;
            case TAKING_DAMAGE:
                if (hitCooldown.hasElapsed()) {
                    this.state = EntityState.IDLE;
                    hit = false;
                } else {
                    hit = true;
                }
                break;
            case MINING:
                if (!this.goldMine.hasResourceLeft()) {
                    GameEvents.getInstance().addEvent(new GameEvent(goldMine.getId(), "depleted", EventType.MINE_DEPLETED));
                    this.state = EntityState.IDLE;
                    return;
                } else if (miningCooldown.hasElapsed()) {
                    Collectable coin = new Gold(this.position, map);
                    goldMine.harvestGold(1);
                    coin.setReward(new Reward(1, Reward.RewardType.GOLD));
                    this.collected.add(coin);
                    coin.destroy(map); //Remove the coin from the map after adding it to collected, so it cant get picked up
                    miningCooldown.reset();
                    GameEvents.getInstance().addEvent(new GameEvent(coin.getId(), coin.getReward().toString(), EventType.GOLD_PICK_UP));
                }
                break;
            case DEAD:
                break;
            default:
                break;
        }
    }

    public void checkCollision() {
        ArrayList<Position> corners = this.getCollisionBox().getCorners();
        for (Position corner : corners) {
            ArrayList<GameObject> coll = map.getInhabitants(corner);
            for (int i = coll.size() - 1; i >= 0; i--) {
                if (coll.get(i) instanceof Collectable collectable) {
                    if (this.getCollisionBox().checkCollision(coll.get(i).getCollisionBox())) {
                        this.collected.add(collectable);
                        collectable.pickUp(map); //This removes the collectable from the map
                        continue;
                    }
                }
                if (this.position.equals(getDestination()) && coll.get(i) instanceof GoldMine) {
                    //Have reached destination, and I am next to a goldmine, start mining :)
                    goldMine = (GoldMine) coll.get(i);
                    startMining();
                }
            }
        }
    }

    public void setAttackTarget(PlayerUnit target) {
        this.targetedUnit = target;
        this.destination = targetedUnit.position;
        state = EntityState.ATTACKING;
    }

    public ArrayList<Collectable> getCollected() {
        return this.collected;
    }

    public void setAttackBuff(int newBuff) {
        this.attackBuff = newBuff;
    }

    public boolean isInAttackRange() {
        return this.inAttackRange;
    }

    public void attack() {
        GameEvents.getInstance().addEvent(new GameEvent(this.id, "Unit attacking", EventType.ATTACK));
        targetedUnit.takeDamage(attack + attackBuff);
    }

    private void startMining() {
        if (this.state == EntityState.MINING) {
            return;
        }
        GameEvents.getInstance().addEvent(new GameEvent(this.id, "Unit mining", EventType.MINING));
        this.state = EntityState.MINING;
        miningCooldown.start();
    }

    public int getAttackRange() {
        return attackRange;
    }

    public boolean hasAttacked() {
        return attacked;
    }

    public boolean hasBeenHit() {
        return hit;
    }

    public void setPosition(Position newPos) {
        this.position = newPos;
    }

    public void setCurrentHp(int currentHp) {
        this.currentHp = currentHp;
    }
}
