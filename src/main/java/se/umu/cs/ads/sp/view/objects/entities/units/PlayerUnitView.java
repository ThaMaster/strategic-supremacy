package se.umu.cs.ads.sp.view.objects.entities.units;

import se.umu.cs.ads.sp.utils.AppSettings;
import se.umu.cs.ads.sp.utils.Position;
import se.umu.cs.ads.sp.utils.enums.EntityState;
import se.umu.cs.ads.sp.view.animation.Animation;
import se.umu.cs.ads.sp.view.objects.entities.EntityView;
import se.umu.cs.ads.sp.view.util.ImageLoader;
import se.umu.cs.ads.sp.view.util.UtilView;

import java.awt.*;

public class PlayerUnitView extends EntityView {
    public PlayerUnitView(long id, Position pos) {
        super(id, pos);
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
            g2d.fillRect(desScreenX - 4, desScreenY - 4, 8, 8);

        }
        if (AppSettings.DEBUG) {
            drawCollisionBox(g2d, cameraWorldPosition);
            g2d.setColor(Color.RED);
            g2d.drawOval(posScreenX - attackRange, posScreenY - attackRange, attackRange * 2, attackRange * 2);
        }

        this.animator.draw(g2d, new Position(posScreenX, posScreenY));
    }

    @Override
    protected void initAnimator() {
        this.animator.addAnimation(
                new Animation(
                        "idle",
                        ImageLoader.loadImages("/sprites/entities/units/basic/idle", "idle", 3),
                        7));
        this.animator.addAnimation(
                new Animation(
                        "running",
                        ImageLoader.loadImages("/sprites/entities/units/basic/run", "run", 3),
                        7));
        this.animator.addAnimation(
                new Animation(
                        "mining",
                        ImageLoader.loadImages("/sprites/entities/units/basic/mining", "mining", 3),
                        7));
        Animation hitAnimation =
                new Animation(
                        "hit",
                        ImageLoader.loadImages("/sprites/entities/units/basic/hit", "hit", 1),
                        7);
        hitAnimation.setOneShot(true);
        this.animator.addAnimation(hitAnimation);
        Animation deadAnimation =
                new Animation(
                        "dead",
                        ImageLoader.loadImages("/sprites/entities/units/basic/dead", "dead", 1),
                        7);
        deadAnimation.setOneShot(true);
        this.animator.addAnimation(deadAnimation);
        Animation attackAnimation =
                new Animation(
                        "attack",
                        ImageLoader.loadImages("/sprites/entities/units/basic/attack", "attack", 3),
                        7);
        attackAnimation.setOneShot(true);
        this.animator.addAnimation(attackAnimation);
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
            case MINING:
                this.animator.changeAnimation("mining");
            case ATTACKING:
                if (inRange) {
                    this.animator.changeAnimation("attack");
                    if (attacked) {
                        this.animator.getCurrentAnimation().resetAnimation();
                    }
                } else {
                    this.animator.changeAnimation("running");
                }
                break;
            case TAKING_DAMAGE:
                if (hasBeenHit) {
                    this.animator.changeAnimation("hit");
                }
                break;
            case DEAD:
                this.animator.changeAnimation("dead");
                break;
            default:
                break;
        }
        this.state = newState;
    }
}
