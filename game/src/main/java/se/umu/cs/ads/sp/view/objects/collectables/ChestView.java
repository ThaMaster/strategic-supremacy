package se.umu.cs.ads.sp.view.objects.collectables;

import se.umu.cs.ads.sp.utils.Constants;
import se.umu.cs.ads.sp.utils.Position;
import se.umu.cs.ads.sp.view.animation.Animation;
import se.umu.cs.ads.sp.view.util.Camera;
import se.umu.cs.ads.sp.view.util.ImageLoader;
import se.umu.cs.ads.sp.view.util.UtilView;

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
        this.animator.draw(g2d, new Position(screenPos.getX() - Constants.ENTITY_WIDTH / 2, screenPos.getY() - Constants.ENTITY_HEIGHT / 2));
    }
}
