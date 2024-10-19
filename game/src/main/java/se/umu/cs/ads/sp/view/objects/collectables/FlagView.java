package se.umu.cs.ads.sp.view.objects.collectables;

import se.umu.cs.ads.sp.utils.AppSettings;
import se.umu.cs.ads.sp.utils.Constants;
import se.umu.cs.ads.sp.utils.Position;
import se.umu.cs.ads.sp.view.animation.Animation;
import se.umu.cs.ads.sp.view.util.Camera;
import se.umu.cs.ads.sp.view.util.ImageLoader;
import se.umu.cs.ads.sp.view.util.UtilView;

import java.awt.*;

public class FlagView extends CollectableView {

    public FlagView(long id, Position pos) {
        super(id, pos);
        initAnimator();
        this.animator.changeAnimation("idle");
    }

    @Override
    public void pickup() {
        this.hasBeenPickedUp = true;
        animator.start();
    }

    @Override
    protected void initAnimator() {
        Animation openingAnimation = new Animation("idle",
                ImageLoader.loadImages("/sprites/collectables/flag",
                        "flag", 5),
                        3);
        this.animator.addAnimation(openingAnimation);
    }

    @Override
    public void draw(Graphics2D g2d) {
        if(AppSettings.DEBUG) {
            g2d.setColor(Color.BLUE);
            drawCollisionBox(g2d);
        }
        Position screenPos = Camera.worldToScreen(position);
        this.animator.draw(g2d, new Position(screenPos.getX() - Constants.ENTITY_WIDTH / 2, screenPos.getY() - Constants.ENTITY_HEIGHT / 2));
    }
}
