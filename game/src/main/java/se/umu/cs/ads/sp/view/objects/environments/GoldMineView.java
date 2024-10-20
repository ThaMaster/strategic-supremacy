package se.umu.cs.ads.sp.view.objects.environments;

import se.umu.cs.ads.sp.utils.AppSettings;
import se.umu.cs.ads.sp.utils.Position;
import se.umu.cs.ads.sp.view.animation.Animation;
import se.umu.cs.ads.sp.view.util.Camera;
import se.umu.cs.ads.sp.view.util.ImageLoader;

import java.awt.*;

public class GoldMineView extends EnvironmentView {

    private boolean depleted;

    public GoldMineView(long id, Position position) {
        super(id, position);
        initAnimator();
        animator.changeAnimation("default");
        depleted = false;
        this.animator.pause();
    }

    @Override
    protected void initAnimator() {
        this.animator.addAnimation(
                new Animation(
                        "default",
                        ImageLoader.loadImages("/sprites/environment/goldPile", "goldPile", 1),
                        7));
    }

    @Override
    public void draw(Graphics2D g2d) {
        if (!depleted) {
            Position screenPos = Camera.worldToScreen(position);
            animator.draw(g2d, new Position(screenPos.getX(), screenPos.getY()));
            if (AppSettings.DEBUG) {
                drawCollisionBox(g2d);
            }
        }
    }

    public void setDepleted(boolean bool) {
        this.depleted = bool;
    }
}
