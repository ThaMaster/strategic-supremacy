package se.umu.cs.ads.sp.model.map;

import se.umu.cs.ads.sp.model.components.CollisionBox;
import se.umu.cs.ads.sp.utils.enums.TileType;

public class TileModel {

    private final TileType type;

    private CollisionBox collisionBox;
    private boolean hasCollision = false;

    public TileModel(TileType type, int width, int height) {
        this.type = type;
    }

    public void enableCollision(boolean enable) {
        this.hasCollision = enable;
    }

    public boolean hasCollision() {
        return this.hasCollision;
    }


    public TileType getType() {
        return this.type;
    }

}
