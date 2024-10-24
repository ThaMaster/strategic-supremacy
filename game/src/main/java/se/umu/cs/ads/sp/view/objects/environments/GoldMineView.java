package se.umu.cs.ads.sp.view.objects.environments;

import se.umu.cs.ads.sp.util.AppSettings;
import se.umu.cs.ads.sp.util.Position;
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

    public boolean isDepleted(){
        return depleted;
    }

    @Override
    protected void initAnimator() {
        this.animator.addAnimation(
                new Animation(
                        "default",
                        ImageLoader.loadImages("/sprites/environment/goldPile", "goldPile", 1),
                        7));
        this.animator.addAnimation(
                new Animation(
                        "depleted",
                        ImageLoader.loadImages("/sprites/environment/goldPile", "goldPileDepleted", 1),
                        7));
    }

    @Override
    public void draw(Graphics2D g2d) {
        Position screenPos = Camera.worldToScreen(position);
        animator.draw(g2d, new Position(screenPos.getX(), screenPos.getY()));
        if (AppSettings.DEBUG) {
            drawCollisionBox(g2d);
        }
    }

    public void setDepleted(boolean bool) {
        if(bool){
            animator.changeAnimation("depleted");
        }
        this.depleted = bool;
    }
}
