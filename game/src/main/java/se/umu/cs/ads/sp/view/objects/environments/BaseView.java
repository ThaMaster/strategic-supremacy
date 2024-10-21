package se.umu.cs.ads.sp.view.objects.environments;

import se.umu.cs.ads.sp.utils.AppSettings;
import se.umu.cs.ads.sp.utils.Position;
import se.umu.cs.ads.sp.view.animation.Animation;
import se.umu.cs.ads.sp.view.util.Camera;
import se.umu.cs.ads.sp.view.util.ImageLoader;

import java.awt.*;

public class BaseView extends EnvironmentView {

    public BaseView(long id, Position position) {
        super(id, position);
        initAnimator();
        this.animator.changeAnimation("default");
    }

    @Override
    public void draw(Graphics2D g2d) {
        Position screenPos = Camera.worldToScreen(position);
        this.animator.draw(g2d, new Position(screenPos.getX(), screenPos.getY()));
        if (AppSettings.DEBUG) {
            drawCollisionBox(g2d);
        }
    }

    @Override
    protected void initAnimator() {
        this.animator.addAnimation(
                new Animation(
                        "default",
                        ImageLoader.loadImages("/sprites/environment/base", "base", 4),
                        7));
    }
}
