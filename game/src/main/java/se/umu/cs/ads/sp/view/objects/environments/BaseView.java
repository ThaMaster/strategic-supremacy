package se.umu.cs.ads.sp.view.objects.environments;

import se.umu.cs.ads.sp.utils.Constants;
import se.umu.cs.ads.sp.utils.Position;
import se.umu.cs.ads.sp.view.animation.Animation;
import se.umu.cs.ads.sp.view.util.ImageLoader;
import se.umu.cs.ads.sp.view.util.UtilView;

import java.awt.*;

public class BaseView extends EnvironmentView {

    public BaseView(long id, Position position) {
        super(id, position);
        initAnimator();
        this.animator.changeAnimation("default");
    }

    @Override
    public void draw(Graphics2D g2d, Position cameraWorldPosition) {
        int posScreenX = position.getX() - cameraWorldPosition.getX() + UtilView.screenX;
        int posScreenY = position.getY() - cameraWorldPosition.getY() + UtilView.screenY;
        this.animator.draw(g2d, new Position(posScreenX - Constants.ENTITY_WIDTH / 2, posScreenY - Constants.ENTITY_HEIGHT / 2));
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
