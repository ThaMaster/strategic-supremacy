package se.umu.cs.ads.sp.view.objects.entities;

import se.umu.cs.ads.sp.utils.Position;
import se.umu.cs.ads.sp.utils.enums.EntityState;
import se.umu.cs.ads.sp.view.animation.Animator;

import java.awt.*;


public abstract class EntityView {

    protected Position position;
    protected Position destination;

    protected EntityState state;
    protected Animator animator;

    protected boolean selected = false;

    public EntityView() {
        this.animator = new Animator();
    }

    public void setSelected(boolean b) {
        this.selected = b;
    }

    public void setPosition(Position newPosition) {
        this.position = newPosition;
    }

    public void setDestination(Position newDestination) {
        if(newDestination != null && newDestination != position) {
            animator.setFlipped(position.getX() < newDestination.getX());
        }
        this.destination = newDestination;
    }

    public void setEntityState(EntityState newState) {
        this.state = newState;
    }

    public void update() {
        this.animator.update();
    }

    protected abstract void initAnimator();
    public abstract void draw(Graphics2D g2d, Position cameraWorldPosition);
}
