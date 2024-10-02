package se.umu.cs.ads.sp.view.objects.entities;

import se.umu.cs.ads.sp.utils.Position;
import se.umu.cs.ads.sp.utils.enums.EntityState;
import se.umu.cs.ads.sp.view.animation.Animator;
import se.umu.cs.ads.sp.view.objects.ObjectView;

public abstract class EntityView extends ObjectView {

    protected Position destination;
    protected EntityState state;
    protected int attackRange = 0;
    protected boolean inRange = false;
    protected boolean attacked = false;
    protected boolean selected = false;
    protected boolean hasBeenHit = false;

    public EntityView(long id, Position pos) {
        super(id, pos);
        this.animator = new Animator();
    }

    public void setSelected(boolean b) {
        this.selected = b;
    }

    public void setDestination(Position newDestination) {
        if (newDestination != null && newDestination != position) {
            animator.setFlipped(position.getX() < newDestination.getX());
        }
        this.destination = newDestination;
    }

    public void setEntityState(EntityState newState) {
        this.state = newState;
    }

    public EntityState getEntityState() {
        return this.state;
    }

    public void update() {
        this.animator.update();
    }

    public void setAttackRange(int newRange) {
        this.attackRange = newRange;
    }
    public void setInRange(boolean bool) {
        this.inRange = bool;
    }
    public void setHasAttacked(boolean bool) {
        this.attacked = bool;
    }

    public void setHasBeenHit(boolean bool) {
        this.hasBeenHit = bool;
    }
}