package se.umu.cs.ads.sp.model.objects.entities;

import se.umu.cs.ads.ns.util.Util;
import se.umu.cs.ads.sp.events.GameEvent;
import se.umu.cs.ads.sp.events.GameEvents;
import se.umu.cs.ads.sp.model.components.CollisionBox;
import se.umu.cs.ads.sp.model.map.Map;
import se.umu.cs.ads.sp.model.objects.GameObject;
import se.umu.cs.ads.sp.util.Constants;
import se.umu.cs.ads.sp.util.Cooldown;
import se.umu.cs.ads.sp.util.Position;
import se.umu.cs.ads.sp.util.enums.EntityState;
import se.umu.cs.ads.sp.util.enums.EventType;

public abstract class Entity extends GameObject {

    protected String entityType;
    protected int maxHp;
    protected int baseHp;
    protected int currentHp;

    protected int baseSpeed;
    protected int speedBuff;
    protected Cooldown hitCooldown;
    protected CollisionBox attackBox;
    protected EntityState state;
    protected EntityState previousState;

    protected Map map;
    protected Position destination;
    private boolean selected = false;
    private long userId;

    public Entity(String entityType, Position startPos, Map map) {
        super(startPos, map);
        destination = startPos;
        this.entityType = entityType;
        state = EntityState.IDLE;
        previousState = EntityState.IDLE;
        baseSpeed = 2;
        speedBuff = 0;
        this.baseHp = 100;
        this.maxHp = baseHp;
        this.currentHp = maxHp;
        this.map = map;
        // Place the hitbox around the entity rather than
        collisionBox = new CollisionBox(position, Constants.ENTITY_WIDTH, Constants.ENTITY_HEIGHT);
    }

    public void setUserId(long id) {
        userId = id;
    }

    public long getUserId() {
        return userId;
    }

    public abstract void update();

    public void move() {
        // Check if the speed is greater than the distance left
        Position currentMapPos = map.getMapIndexPos(position);

        if (Position.distance(this.position, destination) <= (baseSpeed + speedBuff)) {
            position = destination;
            collisionBox.setLocation(destination);
            setState(EntityState.IDLE);
            map.setInhabitant(this, position);
            return;
        }

        // Get the distance left to the destination
        int deltaX = position.getX() - destination.getX();
        int deltaY = position.getY() - destination.getY();

        // Calculate the new coordinates
        int newX = (Math.abs(deltaX) <= (baseSpeed + speedBuff)) ? destination.getX() : position.getX() - Integer.signum(deltaX) * (baseSpeed + speedBuff);
        int newY = (Math.abs(deltaY) <= (baseSpeed + speedBuff)) ? destination.getY() : position.getY() - Integer.signum(deltaY) * (baseSpeed + speedBuff);

        // Check for collisions
        int row = newY / Constants.TILE_WIDTH;
        int col = newX / Constants.TILE_HEIGHT;

        if (map.getModelMap().get(row).get(col).hasCollision()) {
            setState(EntityState.IDLE);
            return;
        }
        Position newPos = new Position(newX, newY);

        if (!map.getMapIndexPos(newPos).equals(currentMapPos)) {
            map.removeInhabitant(this, position);
            map.setInhabitant(this, newPos);
        }
        position = newPos;
        collisionBox.setLocation(position);
    }

    public boolean isSelected() {
        return state != EntityState.DEAD && selected;
    }

    public void setSelected(boolean select) {
        if (state != EntityState.DEAD) {
            selected = select;
        }else{
            selected = false;
        }
    }

    public EntityState getState() {
        return state;
    }
    public void setState(EntityState newState){
        GameEvents.getInstance().addEvent(new GameEvent(Util.generateId(), "", EventType.STATE_CHANGE, this.id));
        if(newState == state){
            return;
        }
        previousState = EntityState.fromLabel(state.label);
        state = newState;
        if(state == EntityState.DEAD){
            selected = false;
        }
    }

    public Position getDestination() {
        return this.destination;
    }

    public void setDestination(Position newDestination) {
        if (state == EntityState.DEAD){
            return;
        }
        if (position.equals(newDestination)) {
            setState(EntityState.IDLE);
            return;
        }
        this.destination = newDestination;
        setState(EntityState.RUNNING);
    }

    public String getEntityType() {
        return entityType;
    }

    public void takeDamage(int damage, long attacker) {
        this.currentHp -= damage;
        hitCooldown.start();
        if (currentHp <= 0) {
            GameEvents.getInstance().addEvent(new GameEvent(attacker, "Unit died!", EventType.DEATH, id));
            setState(EntityState.DEAD);
            this.destroy(map);
        } else {
            setState(EntityState.TAKING_DAMAGE);
            GameEvents.getInstance().addEvent(new GameEvent(attacker, "Unit took damage!", EventType.TAKE_DMG, id));
        }
    }

    public void setPosition(Position newPos) {
        map.removeInhabitant(this, position);
        this.position = newPos;
        this.collisionBox.setLocation(newPos);
        map.setInhabitant(this, position);
    }

    public int getMaxHp() {
        return maxHp;
    }

    public void setMaxHp(int hp) {
        this.maxHp = hp;
    }

    public int getCurrentHp() {
        return this.currentHp;
    }

    public void setCurrentHp(int newHp) {
        currentHp = newHp;
        if (currentHp <= 0) {
            setState(EntityState.DEAD);
        }
    }

    public int getBaseHp() {
        return baseHp;
    }

    public int getBaseSpeed() {
        return baseSpeed;
    }

    public int getSpeedBuff() {
        return speedBuff;
    }

    public void setSpeedBuff(int newSpeedBuff) {
        speedBuff = newSpeedBuff;
    }
}
