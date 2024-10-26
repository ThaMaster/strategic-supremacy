package se.umu.cs.ads.sp.model.objects.entities.units;

import se.umu.cs.ads.sp.events.GameEvent;
import se.umu.cs.ads.sp.events.GameEvents;
import se.umu.cs.ads.sp.model.components.CollisionBox;
import se.umu.cs.ads.sp.model.map.Map;
import se.umu.cs.ads.sp.model.objects.GameObject;
import se.umu.cs.ads.sp.model.objects.collectables.Collectable;
import se.umu.cs.ads.sp.model.objects.collectables.Gold;
import se.umu.cs.ads.sp.model.objects.collectables.Reward;
import se.umu.cs.ads.sp.model.objects.entities.Entity;
import se.umu.cs.ads.sp.model.objects.environment.Base;
import se.umu.cs.ads.sp.model.objects.environment.GoldMine;
import se.umu.cs.ads.sp.util.Cooldown;
import se.umu.cs.ads.sp.util.Position;
import se.umu.cs.ads.sp.util.UtilModel;
import se.umu.cs.ads.sp.util.enums.EntityState;
import se.umu.cs.ads.sp.util.enums.EventType;
import se.umu.cs.ads.sp.util.enums.RewardType;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class PlayerUnit extends Entity {

    private ArrayList<Collectable> collected = new ArrayList<>();
    private Cooldown miningCooldown;
    private Cooldown shootCooldown;
    private GoldMine goldMine;
    private long myBaseId;

    // Attack variables
    private PlayerUnit targetedUnit;
    private final int baseAttack;
    private int attackBuff;
    protected int attackRange;
    private boolean inAttackRange = false;
    private boolean attacked = false;
    private boolean hit = false;

    private boolean hasFlag;
    private Long flagId = null;

    public PlayerUnit(String name, Position startPos, Map map, long baseId) {
        super(name, startPos, map);
        miningCooldown = new Cooldown(3, TimeUnit.SECONDS);
        shootCooldown = new Cooldown(1, TimeUnit.SECONDS);
        hitCooldown = new Cooldown(250, TimeUnit.MILLISECONDS);
        this.baseHp = 100;
        this.maxHp = baseHp;
        this.currentHp = maxHp;
        this.baseAttack = 10;
        this.attackBuff = 0;
        this.attackRange = 150;
        this.attackBox = new CollisionBox(position, attackRange, attackRange);
    }

    public PlayerUnit(long id, String name, Position startPos, Map map) {
        super(name, startPos, map);
        miningCooldown = new Cooldown(3, TimeUnit.SECONDS);
        shootCooldown = new Cooldown(1, TimeUnit.SECONDS);
        hitCooldown = new Cooldown(250, TimeUnit.MILLISECONDS);
        this.baseHp = 100;
        this.maxHp = baseHp;
        this.currentHp = maxHp;
        this.baseAttack = 10;
        this.attackBuff = 0;
        this.attackRange = 150;
        this.attackBox = new CollisionBox(position, attackRange, attackRange);
        this.id = id;
    }

    public void setBase(long baseId) {
        myBaseId = baseId;
    }

    public void setHasFlag(boolean hasFlag, Long flagId) {
        this.hasFlag = hasFlag;
        this.flagId = flagId;
    }

    public boolean hasFlag() {
        return hasFlag;
    }

    public Long getFlagId() {
        if(flagId == null) {
            return -1L;
        }
        return flagId;
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
                        setState(EntityState.IDLE);
                        targetedUnit = null;
                    }
                } else {
                    attacked = false;
                }
                break;
            case TAKING_DAMAGE:
                if (hitCooldown.hasElapsed()) {
                    setState(EntityState.fromLabel(previousState.label));
                    hit = false;
                } else {
                    hit = true;
                }
                break;
            case MINING:
                if (!this.goldMine.hasResourceLeft()) {
                    GameEvents.getInstance().addEvent(new GameEvent(goldMine.getId(), "depleted", EventType.MINE_DEPLETED, id));
                    setState(EntityState.IDLE);
                    return;
                } else if (miningCooldown.hasElapsed()) {
                    Collectable coin = new Gold(this.position, map);
                    goldMine.harvestGold(10);
                    coin.setReward(new Reward(10, RewardType.GOLD));
                    coin.destroy(map); //Remove the coin from the map after adding it to collected, so it cant get picked up
                    miningCooldown.reset();
                    GameEvents.getInstance().addEvent(new GameEvent(coin.getId(), coin.getReward().toString(), EventType.GOLD_PICK_UP, id));
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
                        if (collectable.hasBeenCollected()) {
                            continue;
                        }
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
                if (this.position.equals(getDestination()) && coll.get(i) instanceof Base base) {
                    if (hasFlag && base.getId() == myBaseId) {
                        GameEvents.getInstance().addEvent(new GameEvent(UtilModel.generateId(), "+10 Points", EventType.FLAG_TO_BASE, id));
                        hasFlag = false;
                        flagId = null;
                    }
                }
            }
        }
    }

    public void setAttackTarget(PlayerUnit target) {
        this.targetedUnit = target;
        this.targetedUnit.setSelected(true);
        this.destination = targetedUnit.position;
        setState(EntityState.ATTACKING);
    }

    public long getTargetId() {
        return targetedUnit == null ? -1 : targetedUnit.getId();
    }

    @Override
    public void setDestination(Position newDestination) {
        if (this.state == EntityState.ATTACKING) {
            this.targetedUnit.setSelected(false);
            this.targetedUnit = null;
        }
        super.setDestination(newDestination);

    }

    public ArrayList<Collectable> getCollected() {
        return this.collected;
    }

    public boolean isInAttackRange() {
        return inAttackRange;
    }

    public void attack() {
        GameEvents.getInstance().addEvent(new GameEvent(targetedUnit.id, "Unit attacking", EventType.ATTACK, id));
        targetedUnit.takeDamage(baseAttack + attackBuff, id);
    }

    private void startMining() {
        if (state == EntityState.MINING) {
            return;
        }
        GameEvents.getInstance().addEvent(new GameEvent(id, "Unit mining", EventType.MINING, id));
        setState(EntityState.MINING);
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

    public void setAttackBuff(int newAttackBuff) {
        attackBuff = newAttackBuff;
    }

    public int getAttackBuff() {
        return attackBuff;
    }

    public int getBaseAttack() {
        return baseAttack;
    }

    public void reset(Position newPosition) {
        collected.clear();
        setState(EntityState.IDLE);
        currentHp = maxHp;
        hasFlag = false;
        flagId = null;
        if (targetedUnit != null) {
            targetedUnit.setSelected(false);
            targetedUnit = null;
        }
        setSelected(false);
        position = newPosition;
        collisionBox.setLocation(newPosition);
        destination = newPosition;
        spawn(map);
    }
}
