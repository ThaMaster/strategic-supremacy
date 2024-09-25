package se.umu.cs.ads.sp.view.objects;

import se.umu.cs.ads.sp.utils.Position;
import se.umu.cs.ads.sp.view.animation.Animator;

import java.awt.*;

public abstract class ObjectView {

    protected Position position;
    protected Animator animator;

    public ObjectView(Position pos) {
        position = pos;
        animator = new Animator();
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position newPosition) {
        this.position = newPosition;
    }

    public abstract void update();
    public abstract void draw(Graphics2D g2d, Position cameraWorldPosition);
    protected abstract void initAnimator();

}
