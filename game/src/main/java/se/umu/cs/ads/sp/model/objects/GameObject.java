package se.umu.cs.ads.sp.model.objects;

import se.umu.cs.ads.sp.model.components.CollisionBox;
import se.umu.cs.ads.sp.model.map.Map;
import se.umu.cs.ads.sp.utils.Constants;
import se.umu.cs.ads.sp.utils.Position;
import se.umu.cs.ads.sp.utils.Utils;

public abstract class GameObject {

    protected long id;
    protected Position position;
    protected CollisionBox collisionBox;

    public GameObject(Position pos, Map map) {
        // Should the object generate it or should you need to input it to the constructor?
        this.id = Utils.generateId();
        this.position = pos;
        collisionBox = new CollisionBox(position, Constants.OBJECT_WIDTH, Constants.OBJECT_HEIGHT);
        this.spawn(map);
    }

    private void spawn(Map map) {
        map.setInhabitant(this, position);
    }

    public void setCollisionBox(CollisionBox collisionBox){
        this.collisionBox = collisionBox;
    }

    public void destroy(Map map) {
        map.removeInhabitant(this, position);
    }

    public long getId() {
        return this.id;
    }

    public CollisionBox getCollisionBox() {
        return this.collisionBox;
    }

    public Position getPosition() {
        return this.position;
    }
}
