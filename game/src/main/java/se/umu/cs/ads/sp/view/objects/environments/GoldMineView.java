package se.umu.cs.ads.sp.view.objects.environments;

import se.umu.cs.ads.sp.utils.Position;
import se.umu.cs.ads.sp.view.animation.Animation;
import se.umu.cs.ads.sp.view.util.ImageLoader;
import se.umu.cs.ads.sp.view.util.UtilView;

import java.awt.*;

public class GoldMineView extends EnvironmentView {

    private boolean depleted;

    public GoldMineView(long id, Position position) {
        super(id, position);
        initAnimator();
        animator.changeAnimation("default");
        depleted = false;
    }

    @Override
    protected void initAnimator() {
        this.animator.addAnimation(
                new Animation(
                        "default",
                        ImageLoader.loadImages("/sprites/environment", "goldPile.png", 1),
                        7));
    }

    @Override
    public void draw(Graphics2D g2d, Position cameraPosition) {
        int screenX = this.position.getX() - cameraPosition.getX() + UtilView.screenX;
        int screenY = this.position.getY() - cameraPosition.getY() + UtilView.screenY;
        if(!depleted) {
            g2d.drawImage(this.animator.getCurrentAnimation().getCurrentFrame(), screenX, screenY, UtilView.tileSize, UtilView.tileSize, null);
        }
    }

    public void setDepleted(boolean bool) {
        this.depleted = bool;
    }
}
