package se.umu.cs.ads.sp.model.objects.entities;

import se.umu.cs.ads.sp.model.map.Map;
import se.umu.cs.ads.sp.model.objects.GameObject;
import se.umu.cs.ads.sp.utils.Position;
import se.umu.cs.ads.sp.utils.enums.EntityState;

public class Entity extends GameObject {
    private EntityState state;
    private Position destination;
    private int speed;
    private int ID;
    private Map map;

    public Entity(int ID, Position startPos, Map map) {
        this.ID = ID;
        this.position = startPos;
        this.state = EntityState.IDLE;
        this.speed = 2;
        this.map = map;
        spawn(map);
    }

    public int getID() {
        return ID;
    }

    public void setDestination(Position newDestination) {
        if (!position.equals(newDestination)) {
            this.destination = newDestination;
            this.state = EntityState.RUNNING;
        }
    }

    public void setSpeed(int newSpeed) {
        this.speed = newSpeed;
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

        if (Position.distance(this.position, destination) <= speed) {
            this.position = destination;
            this.state = EntityState.IDLE;
            map.setInhabitant(this, position);
            return;
        }

        int deltaX = position.getX() - destination.getX();
        int deltaY = position.getY() - destination.getY();

        int newX = position.getX();
        int newY = position.getY();

        if(Math.abs(deltaX) <= speed) {
            newX = destination.getX();
        } else if (deltaX > 0) {
            // Move Right
            newX -= speed;
        } else if (deltaX < 0) {
            // Move left
            newX += speed;

        }

        if(Math.abs(deltaY) <= speed) {
            newY = destination.getY();
        } else if (deltaY > 0) {
            // Move up
            newY -= speed;
        } else if (deltaY < 0) {
            // Move down
            newY += speed;
        }

        this.position = new Position(newX, newY);
        map.setInhabitant(this, position);
    }
}
