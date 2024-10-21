package se.umu.cs.ads.sp.model.components;

import se.umu.cs.ads.sp.utils.Position;

import java.awt.*;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

public class CollisionBox {

    private Rectangle collisionBox;
    private int width;
    private int height;

    public CollisionBox(Position pos, int width, int height) {
        this.width = width;
        this.height = height;
        this.collisionBox = new Rectangle(pos.getX()-(width/2), pos.getY()-(height/2), width, height);
    }

    public void setLocation(Position pos) {
        this.collisionBox.setLocation(pos.getX()-(width/2), pos.getY()-(height/2));
    }

    public boolean checkCollision(CollisionBox box) {
        return this.collisionBox.intersects(box.collisionBox);
    }

    public boolean checkCollision(Line2D line) {
        return this.collisionBox.intersectsLine(line);
    }

    public boolean contains(Position position) {
        return this.collisionBox.contains(new Point(position.getX(),position.getY()));
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

    public int getX() {
        return (int)collisionBox.getX();
    }

    public int getY() {
        return (int)collisionBox.getY();
    }

    public int getWidth() {
        return (int)collisionBox.getWidth();
    }

    public int getHeight() {
        return (int)collisionBox.getHeight();
    }

    @Override
    public String toString() {
        return "(x: " + collisionBox.x + " y: " + collisionBox.y + " w:" + width + " h:" + height + ")";
    }
}
