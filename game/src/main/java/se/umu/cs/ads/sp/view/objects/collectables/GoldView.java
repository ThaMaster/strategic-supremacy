package se.umu.cs.ads.sp.view.objects.collectables;

import se.umu.cs.ads.sp.util.AppSettings;
import se.umu.cs.ads.sp.util.Position;
import se.umu.cs.ads.sp.view.animation.Animation;
import se.umu.cs.ads.sp.view.util.Camera;
import se.umu.cs.ads.sp.view.util.ImageLoader;

import java.awt.*;

public class GoldView extends CollectableView {

    public GoldView(long id, Position pos) {
        super(id, pos);
        initAnimator();
        this.animator.changeAnimation("idle");
    }

    @Override
    public void pickup() {
        this.hasBeenPickedUp = true;
    }

    @Override
    protected void initAnimator() {
        Animation openingAnimation = new Animation("idle", ImageLoader.loadImages("/sprites/collectables/coins", "coin", 5), 3);
        this.animator.addAnimation(openingAnimation);
    }

    @Override
    public void draw(Graphics2D g2d) {
        if(AppSettings.DEBUG) {
            g2d.setColor(Color.BLUE);
            drawCollisionBox(g2d);
        }
        Position screenPos = Camera.worldToScreen(position);
        this.animator.draw(g2d, screenPos);
    }
}
