package se.umu.cs.ads.sp.model.objects;

import se.umu.cs.ads.sp.model.CollisionBox;
import se.umu.cs.ads.sp.utils.Position;

public abstract class GameObject {

    private Position position;
    private CollisionBox collisionBox;

    public void spawn() {
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
