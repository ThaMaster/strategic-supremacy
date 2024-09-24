package se.umu.cs.ads.sp.view;

import se.umu.cs.ads.sp.utils.Position;
import se.umu.cs.ads.sp.view.animation.Animator;

import java.awt.*;


public class EntityView {

    private Position position;
    private Animator animator;

    public EntityView() {
        this.animator = new Animator();
    }

    public void setPosition(Position newPosition) {
        this.position = newPosition;
    }

    public void draw(Graphics2D g2d) {
        g2d.setColor(new Color(128, 128, 128, 127));
        g2d.fillRect(position.getX(), position.getY(), 10, 10);
    }
}
