package se.umu.cs.ads.sp.view.objects;

import se.umu.cs.ads.sp.utils.Position;
import se.umu.cs.ads.sp.view.animation.Animator;

import java.awt.*;

public abstract class ObjectView {

    protected Position position;
    protected Animator animator;

    protected final long id;

    public ObjectView(long id, Position pos) {
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

    public abstract void update();

    public abstract void draw(Graphics2D g2d, Position cameraWorldPosition);

    protected abstract void initAnimator();

}
