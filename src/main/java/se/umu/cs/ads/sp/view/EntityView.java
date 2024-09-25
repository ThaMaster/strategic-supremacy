package se.umu.cs.ads.sp.view;

import se.umu.cs.ads.sp.model.objects.entities.Entity;
import se.umu.cs.ads.sp.utils.Position;
import se.umu.cs.ads.sp.utils.enums.EntityState;
import se.umu.cs.ads.sp.view.animation.Animator;
import se.umu.cs.ads.sp.view.util.UtilView;

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

    public void draw(Graphics2D g2d, Position cameraWorldPosition) {
        if (selected) {
            g2d.setColor(Color.GREEN);
        } else {
            g2d.setColor(Color.RED);
        }

        int posScreenX = position.getX() - cameraWorldPosition.getX() + UtilView.screenX;
        int posScreenY = position.getY() - cameraWorldPosition.getY() + UtilView.screenY;

        if (state == EntityState.RUNNING) {
            int desScreenX = destination.getX() - cameraWorldPosition.getX() + UtilView.screenX;
            int desScreenY = destination.getY() - cameraWorldPosition.getY() + UtilView.screenY;
            g2d.drawLine(posScreenX, posScreenY, desScreenX, desScreenY);
        }

        // Will be replaced with image later
        g2d.fillRect(posScreenX - 8, posScreenY - 8, 16, 16);
    }
}
