package se.umu.cs.ads.sp.view.objects;

import se.umu.cs.ads.sp.model.components.CollisionBox;
import se.umu.cs.ads.sp.utils.Position;
import se.umu.cs.ads.sp.view.animation.Animator;
import se.umu.cs.ads.sp.view.util.UtilView;

import java.awt.*;

public abstract class ObjectView {

    protected Position position;
    protected Animator animator;
    protected final long id;

    protected CollisionBox collisionBox;


    public ObjectView(long id, Position pos) {
        this.collisionBox = new CollisionBox(pos, 0,0);
        this.id = id;
        position = pos;
        animator = new Animator();
    }

    public Position getPosition() {
        return position;
    }

    public long getId() {
        return this.id;
    }

    public void setPosition(Position newPosition) {
        this.position = newPosition;
    }

    public void setCollisionBox(CollisionBox cBox) {
        this.collisionBox = cBox;
    }

    public void drawCollisionBox(Graphics2D g2d, Position cameraPosition) {
        int colX = collisionBox.getX() - cameraPosition.getX() + UtilView.screenX;
        int colY = collisionBox.getY() - cameraPosition.getY() + UtilView.screenY;
        g2d.drawRect(colX, colY,
                (int)collisionBox.getCollisionShape().getWidth(),
                (int)collisionBox.getCollisionShape().getHeight());
    }
    public abstract void update();

    public abstract void draw(Graphics2D g2d, Position cameraWorldPosition);

    protected abstract void initAnimator();



}
