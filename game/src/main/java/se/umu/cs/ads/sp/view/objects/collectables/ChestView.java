package se.umu.cs.ads.sp.view.objects.collectables;

import se.umu.cs.ads.sp.util.AppSettings;
import se.umu.cs.ads.sp.util.Position;
import se.umu.cs.ads.sp.view.animation.Animation;
import se.umu.cs.ads.sp.view.util.Camera;
import se.umu.cs.ads.sp.view.util.ImageLoader;

import java.awt.*;

public class ChestView extends CollectableView {

    public ChestView(long id, Position pos) {
        super(id, pos);
        initAnimator();
        this.animator.changeAnimation("opening");
        this.animator.pause();
    }

    @Override
    public void pickup() {
        this.hasBeenPickedUp = true;
        animator.start();
    }

    @Override
    protected void initAnimator() {
        Animation openingAnimation = new Animation("opening", ImageLoader.loadImages("/sprites/collectables/chests", "wodden_chest", 4), 7);
        openingAnimation.setOneShot(true);
        this.animator.addAnimation(openingAnimation);
    }

    @Override
    public void draw(Graphics2D g2d) {
        Position screenPos = Camera.worldToScreen(position);
        this.animator.draw(g2d, new Position(screenPos.getX(), screenPos.getY()));
        if (AppSettings.DEBUG) {
            drawCollisionBox(g2d);
        }
    }
}
