package se.umu.cs.ads.sp.view.objects.collectables;

import se.umu.cs.ads.sp.utils.AppSettings;
import se.umu.cs.ads.sp.utils.Position;
import se.umu.cs.ads.sp.view.animation.Animation;
import se.umu.cs.ads.sp.view.util.ImageLoader;
import se.umu.cs.ads.sp.view.util.UtilView;

import java.awt.*;

public class GoldView extends CollectableView {

    public GoldView(long id, Position pos) {
        super(id, pos);
        initAnimator();
        this.animator.changeAnimation("opening");
    }

    @Override
    public void update() {
        this.animator.update();
    }

    @Override
    public void pickup() {
        this.hasBeenPickedUp = true;
    }

    @Override
    protected void initAnimator() {
        Animation openingAnimation = new Animation("opening", ImageLoader.loadImages("/sprites/collectables/coins", "coin", 5), 7);
        this.animator.addAnimation(openingAnimation);
    }

    @Override
    public void draw(Graphics2D g2d, Position cameraWorldPosition) {
        if(AppSettings.DEBUG) {
            g2d.setColor(Color.BLUE);
            drawCollisionBox(g2d, cameraWorldPosition);
        }
        int posScreenX = position.getX() - cameraWorldPosition.getX() + UtilView.screenX;
        int posScreenY = position.getY() - cameraWorldPosition.getY() + UtilView.screenY;
        this.animator.draw(g2d, new Position(posScreenX, posScreenY));
    }
}
