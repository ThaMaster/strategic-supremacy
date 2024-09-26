package se.umu.cs.ads.sp.model.objects.entities;

import se.umu.cs.ads.sp.model.map.Map;
import se.umu.cs.ads.sp.model.objects.GameObject;
import se.umu.cs.ads.sp.model.objects.collectables.Collectable;
import se.umu.cs.ads.sp.utils.Constants;
import se.umu.cs.ads.sp.utils.Position;
import se.umu.cs.ads.sp.utils.enums.EntityState;

import java.util.ArrayList;

public class Entity extends GameObject {
    private final int ID;

    private EntityState state;
    private Position destination;
    private int speed;
    private Map map;
    private ArrayList<Integer> collected;
    public Entity(int ID, Position startPos, Map map) {
        super(startPos);
        this.ID = ID;
        this.position = startPos;
        this.state = EntityState.IDLE;
        this.speed = 2;
        collected = new ArrayList<>();

        // Should entities contain the map and spawn in themselves?
        this.map = map;
        spawn(map);
    }

    public int getID() {
        return ID;
    }

    public void update() {
        switch (state) {
            case IDLE:
                break;
            case RUNNING:
                move();
                break;
            case ATTACKING:
                break;
            case TAKING_DAMAGE:
                break;
            default:
                break;
        }

        // Do other things
    }

    public void move() {

        // Check if the speed is greater than the distance left
        if (Position.distance(this.position, destination) <= speed) {
            this.position = destination;
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
        this.collisionBox.getCollisionShape().setLocation(newX, newY);

        checkCollision();
    }

    public void checkCollision() {
        ArrayList<Position> corners = this.getCollisionBox().getCorners();
        for(Position corner : corners) {
            GameObject coll = map.getInhabitant(corner);
            if(coll instanceof Collectable collectable) {
                this.collected.add(0);
                collectable.getReward();
            }
        }
    }

    public ArrayList<Integer> getCollected(){
        return this.collected;
    }

    public EntityState getState() {
        return this.state;
    }

    public Position getDestination() {
        return this.destination;
    }

    public void setSpeed(int newSpeed) {
        this.speed = newSpeed;
    }

    public int getSpeed() {
        return this.speed;
    }

    public void setDestination(Position newDestination) {
        this.destination = newDestination;
        this.state = EntityState.RUNNING;
    }
}
