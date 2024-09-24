package se.umu.cs.ads.sp.model.objects;

import se.umu.cs.ads.sp.model.components.CollisionBox;
import se.umu.cs.ads.sp.model.map.Map;
import se.umu.cs.ads.sp.utils.Position;

public abstract class GameObject {

    protected Position position;
    private CollisionBox collisionBox;

    public void spawn(Map map) {
        map.setInhabitant(this, position);
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
