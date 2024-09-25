package se.umu.cs.ads.sp.model.components;

import java.awt.*;
import java.awt.geom.Line2D;

public class CollisionBox {
    private Rectangle collisionBox;

    public CollisionBox(int x, int y, int width, int height) {
        this.collisionBox = new Rectangle(x, y, width, height);
    }

    public boolean checkCollision(CollisionBox box) {
        return this.collisionBox.intersects(box.collisionBox);
    }

    public boolean checkCollision(Line2D line) {
        return this.collisionBox.intersectsLine(line);
    }

    public Rectangle getCollisionShape() {
        return this.collisionBox;
    }
}
