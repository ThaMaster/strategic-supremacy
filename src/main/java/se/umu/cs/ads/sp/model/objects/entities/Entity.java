package se.umu.cs.ads.sp.model.objects.entities;

import org.apache.commons.lang3.tuple.Pair;
import se.umu.cs.ads.sp.model.components.CollisionBox;
import se.umu.cs.ads.sp.model.map.Map;
import se.umu.cs.ads.sp.model.objects.GameObject;
import se.umu.cs.ads.sp.utils.Constants;
import se.umu.cs.ads.sp.utils.Position;
import se.umu.cs.ads.sp.utils.enums.EntityState;

import java.util.ArrayList;

public abstract class Entity extends GameObject {

    protected int maxHp;
    protected int baseHp;
    protected int currentHp;

    protected int speed;

    protected CollisionBox attackBox;
    protected int attackRange;

    protected EntityState state;
    protected Map map;

    private Position destination;
    private boolean selected = false;

    protected long targetId = -1;

    public Entity(Position startPos, Map map) {
        super(startPos);
        this.position = startPos;
        this.state = EntityState.IDLE;
        this.speed = 2;
        this.baseHp = 100;
        this.map = map;
        // Place the hitbox around the entity rather than
        this.collisionBox = new CollisionBox(position, Constants.ENTITY_WIDTH, Constants.ENTITY_HEIGHT);
        spawn(map);
    }

    public abstract void update();

    public void move() {
        // Check if the speed is greater than the distance left
        if (Position.distance(this.position, destination) <= speed) {
            this.position = destination;
            this.collisionBox.setLocation(destination);
            this.state = EntityState.IDLE;
            map.setInhabitant(this, position);
            return;
        }

        // Get the distance left to the destination
        int deltaX = position.getX() - destination.getX();
        int deltaY = position.getY() - destination.getY();

        // Calculate the new coordinates
        int newX = (Math.abs(deltaX) <= speed) ? destination.getX() : position.getX() - Integer.signum(deltaX) * speed;
        int newY = (Math.abs(deltaY) <= speed) ? destination.getY() : position.getY() - Integer.signum(deltaY) * speed;

        // Check for collisions
        int row = newY / Constants.TILE_WIDTH;
        int col = newX / Constants.TILE_HEIGHT;
        if (map.getModelMap().get(row).get(col).hasCollision()) {
            this.state = EntityState.IDLE;
            map.setInhabitant(this, position);
            return;
        }

        this.position = new Position(newX, newY);
        this.collisionBox.setLocation(position);
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean select) {
        this.selected = select;
    }

    public EntityState getState() {
        return this.state;
    }

    public int getSpeed() {
        return this.speed;
    }

    public void setSpeed(int newSpeed) {
        this.speed = newSpeed;
    }

    public Position getDestination() {
        return this.destination;
    }

    public void setAttackDestination(Position newDestination, long target) {
        this.destination = newDestination;
        this.targetId = target;
        this.state = EntityState.ATTACKING;
    }

    public void setDestination(Position newDestination) {
        long entityHit = checkEntityHit(newDestination);
        if(entityHit != -1) {
            System.out.println("Hit entity with Id: " + entityHit);
        }

        this.destination = newDestination;
        this.state = EntityState.RUNNING;
    }

    private long checkEntityHit(Position position) {
        // Make this smarter in some way?
        int col = position.getX() / Constants.TILE_WIDTH;
        int row = position.getY() / Constants.TILE_HEIGHT;

        int colOffset = (position.getX() % Constants.TILE_WIDTH > Constants.TILE_WIDTH / 2) ? 1 : -1;
        int rowOffset = (position.getY() % Constants.TILE_HEIGHT > Constants.TILE_HEIGHT / 2) ? 1 : -1;

        ArrayList<Pair<Integer, Integer>> pairsToCheck = new ArrayList<>();
        pairsToCheck.add(Pair.of(row, col));
        pairsToCheck.add(Pair.of(row + rowOffset, col));
        pairsToCheck.add(Pair.of(row, col + colOffset));
        pairsToCheck.add(Pair.of(row + rowOffset, col + colOffset));

        for(Pair<Integer, Integer> pair : pairsToCheck) {
            for (GameObject object : map.getInhabitants(pair.getLeft(), pair.getRight())) {
                if (object instanceof Entity entity && entity.getCollisionBox().contains(position)) {
                    return entity.getId();
                }
            }
        }

        return -1;
    }

    public void setMaxHp(int hp) {
        this.maxHp = hp;
    }

    public int getCurrentHp() {
        return this.currentHp;
    }

    public void takeDamage(int damage) {
        this.state = EntityState.TAKING_DAMAGE;
        this.currentHp -= damage;
        if (currentHp <= 0) {
            this.state = EntityState.DEAD;
        }
    }

    public int getAttackRange() {
        return attackRange;
    }
}
