package se.umu.cs.ads.sp.model.objects;

import se.umu.cs.ads.sp.model.components.CollisionBox;
import se.umu.cs.ads.sp.model.map.Map;
import se.umu.cs.ads.sp.utils.Constants;
import se.umu.cs.ads.sp.utils.Position;

public abstract class GameObject {
    protected Integer id;
    protected Position position;
    protected CollisionBox collisionBox;


    public Integer getId(){
        return id;
    }
    public GameObject(Position pos) {
        this.position = pos;
        collisionBox = new CollisionBox(pos.getX(), pos.getY(), Constants.OBJECT_WIDTH, Constants.OBJECT_HEIGHT);
    }

    public void spawn(Map map) {
        map.setInhabitant(this, position);
        System.out.println("I spawned");
    }

    public void destroy() {
        // Remove the object from game.
    }

    public CollisionBox getCollisionBox() {
        return this.collisionBox;
    }

    public Position getPosition() {
        return this.position;
    }
}
