package se.umu.cs.ads.sp.model.components;

import se.umu.cs.ads.sp.utils.Position;

import java.awt.*;
import java.awt.geom.Line2D;
import java.util.ArrayList;
import java.util.List;

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

    public ArrayList<Position> getCorners(){
        return new ArrayList<>(List.of(
                new Position(collisionBox.x, collisionBox.y),
                new Position(collisionBox.x + collisionBox.width, collisionBox.y),
                new Position(collisionBox.x, collisionBox.y + collisionBox.height),
                new Position(collisionBox.x + collisionBox.width, collisionBox.y + collisionBox.height)
        ));
    }
}
