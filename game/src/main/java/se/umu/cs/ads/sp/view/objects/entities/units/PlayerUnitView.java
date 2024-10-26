package se.umu.cs.ads.sp.view.objects.entities.units;

import se.umu.cs.ads.sp.util.AppSettings;
import se.umu.cs.ads.sp.util.Constants;
import se.umu.cs.ads.sp.util.Position;
import se.umu.cs.ads.sp.util.enums.EntityState;
import se.umu.cs.ads.sp.util.enums.UpgradeType;
import se.umu.cs.ads.sp.view.animation.Animation;
import se.umu.cs.ads.sp.view.objects.entities.EntityView;
import se.umu.cs.ads.sp.view.util.Camera;
import se.umu.cs.ads.sp.view.util.ImageLoader;

import java.awt.*;
import java.awt.image.BufferedImage;

public class PlayerUnitView extends EntityView {

    public String unitType;
    private boolean hasFlag = false;
    protected BufferedImage collectedFlagImage;

    private int attackDamage = 0;
    private int speed;

    public PlayerUnitView(long id, String unitType, Position pos) {
        super(id, pos);
        this.unitType = unitType;
        initAnimator();
    }

    @Override
    public void draw(Graphics2D g2d) {
        g2d.setColor(Color.RED);

        Position screenPos = Camera.worldToScreen(position);

        if (hasFlag) {
            Position flagPos = new Position(
                    animator.isFlipped() ? collectedFlagImage.getWidth() + screenPos.getX() - (collectedFlagImage.getWidth() / 2) : screenPos.getX() - (collectedFlagImage.getWidth() / 2),
                    screenPos.getY() - (collectedFlagImage.getHeight() / 2) - 10);
            int width = animator.isFlipped() ? -collectedFlagImage.getWidth() : collectedFlagImage.getWidth();
            int height = collectedFlagImage.getHeight();
            g2d.drawImage(collectedFlagImage, flagPos.getX(), flagPos.getY(), width, height, null);
        }

        if (selected) {
            Position hpBarPosition = new Position(screenPos.getX(), screenPos.getY() - (int) (Constants.ENTITY_HEIGHT * 0.75));
            this.healthBar.draw(g2d, hpBarPosition, Constants.ENTITY_WIDTH, Constants.ENTITY_HEIGHT / 4);

            if (state == EntityState.RUNNING) {
                g2d.setColor(Color.GREEN);
                Position screenDestination = Camera.worldToScreen(destination);

                g2d.drawLine(screenPos.getX(), screenPos.getY(), screenDestination.getX(), screenDestination.getY());
                g2d.fillRect(screenDestination.getX() - 4, screenDestination.getY() - 4, 8, 8);
            }
        }

        if (AppSettings.DEBUG && isMyUnit) {
            if (selected) {
                g2d.setColor(Color.GREEN);
            } else {
                g2d.setColor(Color.RED);
            }
            drawCollisionBox(g2d);
            g2d.drawOval(screenPos.getX() - attackRange, screenPos.getY() - attackRange, attackRange * 2, attackRange * 2);
            g2d.setColor(Color.WHITE);
            g2d.drawOval(screenPos.getX() - Constants.L1_RADIUS, screenPos.getY() - Constants.L1_RADIUS, Constants.L1_RADIUS * 2, Constants.L1_RADIUS * 2);
            g2d.setColor(Color.YELLOW);
            g2d.drawOval(screenPos.getX() - Constants.L2_RADIUS, screenPos.getY() - Constants.L2_RADIUS, Constants.L2_RADIUS * 2, Constants.L2_RADIUS * 2);
        }
        this.animator.draw(g2d, screenPos);
    }

    @Override
    protected void initAnimator() {
        collectedFlagImage = ImageLoader.loadImage("/sprites/collectables/flag/flagCollected.png");
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
        this.animator.changeAnimation("idle");
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
                break;
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
                this.animator.changeAnimation("idle");
                break;
        }
        this.state = newState;
    }

    public void setHasFlag(boolean bool) {
        this.hasFlag = bool;
    }

    public void setStats(int maxHp, int currentHp, int attackDamage, int speed) {
        setHealthBarValues(maxHp, currentHp);
        this.attackDamage = attackDamage;
        this.speed = speed;
    }

    public int getStat(String stat) {
        UpgradeType type = UpgradeType.fromLabel(stat);
        int value;
        switch (type) {
            case MAX_HP -> value = healthBar.getMaxHealth();
            case ATTACK_DMG -> value = attackDamage;
            case MOVEMENT_SPEED -> value = speed;
            default -> value = -1;
        }

        return value;
    }
}
