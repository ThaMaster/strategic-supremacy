package se.umu.cs.ads.sp.view;

import se.umu.cs.ads.sp.model.objects.entities.Entity;
import se.umu.cs.ads.sp.utils.Position;
import se.umu.cs.ads.sp.utils.enums.EntityState;
import se.umu.cs.ads.sp.view.animation.Animator;

import java.awt.*;


public class EntityView {

    private boolean selected = false;
    private Position position;
    private Position destination;

    private EntityState state;
    private Animator animator;

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
        this.destination = newDestination;
    }

    public void setEntityState(EntityState newState) {
        this.state = newState;
    }

    public void draw(Graphics2D g2d) {
        if (selected) {
            g2d.setColor(Color.GREEN);
        } else {
            g2d.setColor(Color.RED);
        }

        if (state == EntityState.RUNNING) {
            g2d.drawLine(position.getX(), position.getY(), destination.getX(), destination.getY());
        }

        // Will be replaced with image later
        g2d.fillRect(position.getX() - 8, position.getY() - 8, 16, 16);
    }
}
