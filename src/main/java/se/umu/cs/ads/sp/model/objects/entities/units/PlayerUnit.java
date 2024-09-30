package se.umu.cs.ads.sp.model.objects.entities.units;

import se.umu.cs.ads.sp.model.components.CollisionBox;
import se.umu.cs.ads.sp.model.components.Cooldown;
import se.umu.cs.ads.sp.model.map.Map;
import se.umu.cs.ads.sp.model.objects.GameObject;
import se.umu.cs.ads.sp.model.objects.collectables.Collectable;
import se.umu.cs.ads.sp.model.objects.entities.Entity;
import se.umu.cs.ads.sp.utils.Position;

import java.util.ArrayList;

public class PlayerUnit extends Entity {

    private ArrayList<Collectable> collected = new ArrayList<>();
    private Cooldown miningCooldown;
    private Cooldown shootCooldown;

    private final int attack;
    private int attackBuff;

    public PlayerUnit(Position startPos, Map map) {
        super(startPos, map);
        this.baseHp = 100;
        this.maxHp = baseHp;
        this.currentHp = maxHp;
        this.attack = 10;
        this.attackBuff = 0;
        this.attackRange = 250;
        this.attackBox = new CollisionBox(position, attackRange, attackRange);
    }

    @Override
    public void update() {
        switch (state) {
            case IDLE:
                break;
            case RUNNING:
                move();
                checkCollision(collisionBox);
                break;
            case ATTACKING:
                if (!checkCollision(attackBox)) {
                    move();
                }
                break;
            case TAKING_DAMAGE:
                break;
            case DEAD:
                System.out.println("Unit is dead!");
                break;
            default:
                break;
        }
    }

    public boolean checkCollision(CollisionBox cBox) {
        ArrayList<Position> corners = cBox.getCorners();
        for (Position corner : corners) {
            ArrayList<GameObject> coll = map.getInhabitants(corner);
            for (int i = coll.size() - 1; i >= 0; i--) {
                if (coll.get(i) instanceof Collectable collectable) {
                    if (cBox.checkCollision(coll.get(i).getCollisionBox())) {
                        this.collected.add(collectable);
                        collectable.pickUp(map); //This removes the collectable from the map
                        return true;
                    } else if (coll.get(i) instanceof Entity entity && entity.getId() == targetId) {
                        attack(entity);
                        return true;
                    }
                }
            }
        }
        return false;
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
}
