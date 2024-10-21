package se.umu.cs.ads.sp.view.objects.entities.units;

import se.umu.cs.ads.sp.utils.Position;
import se.umu.cs.ads.sp.view.animation.Animation;
import se.umu.cs.ads.sp.view.util.ImageLoader;

public class EnemyUnitView extends PlayerUnitView {
    public EnemyUnitView(long id, String unitType, Position pos) {
        super(id, unitType, pos);
    }

    @Override
    protected void initAnimator() {
        this.animator.addAnimation(
                new Animation(
                        "idle",
                        ImageLoader.loadImages("/sprites/entities/units/enemy/idle", "idle", 3),
                        7));
        this.animator.addAnimation(
                new Animation(
                        "running",
                        ImageLoader.loadImages("/sprites/entities/units/enemy/run", "run", 3),
                        7));
        this.animator.addAnimation(
                new Animation(
                        "mining",
                        ImageLoader.loadImages("/sprites/entities/units/enemy/mining", "mining", 3),
                        7));
        Animation hitAnimation =
                new Animation(
                        "hit",
                        ImageLoader.loadImages("/sprites/entities/units/enemy/hit", "hit", 1),
                        7);
        hitAnimation.setOneShot(true);
        this.animator.addAnimation(hitAnimation);
        Animation deadAnimation =
                new Animation(
                        "dead",
                        ImageLoader.loadImages("/sprites/entities/units/enemy/dead", "dead", 1),
                        7);
        deadAnimation.setOneShot(true);
        this.animator.addAnimation(deadAnimation);
        Animation attackAnimation =
                new Animation(
                        "attack",
                        ImageLoader.loadImages("/sprites/entities/units/enemy/attack", "attack", 3),
                        7);
        attackAnimation.setOneShot(true);
        this.animator.addAnimation(attackAnimation);
    }
}
