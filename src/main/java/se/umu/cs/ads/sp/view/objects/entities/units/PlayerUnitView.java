package se.umu.cs.ads.sp.view.objects.entities.units;

import se.umu.cs.ads.sp.utils.Constants;
import se.umu.cs.ads.sp.utils.Position;
import se.umu.cs.ads.sp.utils.enums.EntityState;
import se.umu.cs.ads.sp.view.animation.Animation;
import se.umu.cs.ads.sp.view.objects.entities.EntityView;
import se.umu.cs.ads.sp.view.util.ImageLoader;
import se.umu.cs.ads.sp.view.util.UtilView;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class PlayerUnitView extends EntityView {

    public PlayerUnitView(Position pos) {
        super(pos);
        initAnimator();
    }

    @Override
    public void draw(Graphics2D g2d, Position cameraWorldPosition) {
        int posScreenX = position.getX() - cameraWorldPosition.getX() + UtilView.screenX;
        int posScreenY = position.getY() - cameraWorldPosition.getY() + UtilView.screenY;

        if (state == EntityState.RUNNING && selected) {
            g2d.setColor(Color.GREEN);
            int desScreenX = destination.getX() - cameraWorldPosition.getX() + UtilView.screenX;
            int desScreenY = destination.getY() - cameraWorldPosition.getY() + UtilView.screenY;
            g2d.drawLine(posScreenX, posScreenY, desScreenX, desScreenY);
            g2d.fillRect(desScreenX-4, desScreenY -4, 8, 8);
        }

        this.animator.draw(g2d, new Position(posScreenX - Constants.ENTITY_WIDTH / 2, posScreenY - Constants.ENTITY_HEIGHT / 2));
    }

    @Override
    protected void initAnimator() {
        this.animator.addAnimation(new Animation("idle", getIdleImages(), 7));
        this.animator.addAnimation(new Animation("running", getRunningImages(), 7));
    }

    @Override
    public void setEntityState(EntityState newState) {
        switch (newState) {
            case IDLE:
                this.animator.changeAnimation("idle");
                break;
            case RUNNING:
                this.animator.changeAnimation("running");
                break;
            default:
                break;
        }
        this.state = newState;
    }

    private ArrayList<BufferedImage> getIdleImages() {
        ArrayList<BufferedImage> images = new ArrayList<>();
        for (int i = 1; i <= 3; i++) {
            images.add(ImageLoader.loadImage("/sprites/entities/units/basic/idle/idle" + i + ".png"));
        }
        return images;
    }

    private ArrayList<BufferedImage> getRunningImages() {
        ArrayList<BufferedImage> images = new ArrayList<>();
        for (int i = 1; i <= 3; i++) {
            images.add(ImageLoader.loadImage("/sprites/entities/units/basic/run/run" + i + ".png"));
        }
        return images;
    }

}
