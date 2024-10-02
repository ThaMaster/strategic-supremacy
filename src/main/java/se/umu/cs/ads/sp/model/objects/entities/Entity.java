package se.umu.cs.ads.sp.model.objects.entities;

import se.umu.cs.ads.sp.model.components.CollisionBox;
import se.umu.cs.ads.sp.model.map.Map;
import se.umu.cs.ads.sp.model.objects.GameObject;
import se.umu.cs.ads.sp.utils.Constants;
import se.umu.cs.ads.sp.utils.Cooldown;
import se.umu.cs.ads.sp.utils.Position;
import se.umu.cs.ads.sp.utils.enums.EntityState;

public abstract class Entity extends GameObject {

    protected int maxHp;
    protected int baseHp;
    protected int currentHp;
    protected int speed;
    protected Cooldown hitCooldown;
    protected CollisionBox attackBox;
    protected EntityState state;
    protected Map map;
    protected Position destination;
    private boolean selected = false;

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
        return state != EntityState.DEAD && selected;
    }

    public void setSelected(boolean select) {
        if(state != EntityState.DEAD) {
            this.selected = select;
        }
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

    public void setDestination(Position newDestination) {
        this.destination = newDestination;
        this.state = EntityState.RUNNING;
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
        System.out.println("AHJJJ");
        hitCooldown.start();
        if (currentHp <= 0) {
            this.state = EntityState.DEAD;
        }
    }
}
