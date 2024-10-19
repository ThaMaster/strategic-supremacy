package se.umu.cs.ads.sp.view.animation;

import se.umu.cs.ads.sp.utils.Position;
import se.umu.cs.ads.sp.view.util.Camera;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.HashMap;

public class Animator {

    private HashMap<String, Animation> animations;
    private Animation currentAnimation;

    private boolean flipped = false;
    private boolean paused = false;

    public Animator() {
        this.animations = new HashMap<>();
    }

    public Animation getCurrentAnimation() {
        return this.currentAnimation;
    }

    public void addAnimation(Animation animation) {
        this.animations.put(animation.getName(), animation);
    }

    public void changeAnimation(String animationName) {
        currentAnimation = animations.get(animationName);
    }

    public void update() {
        // Change animation frame
        if (currentAnimation != null && !paused) {
            currentAnimation.update();
        }
    }

    public void setFlipped(boolean bool) {
        this.flipped = bool;
    }

    public void draw(Graphics2D g2d, Position pos) {
        // Draw the current frame of animation
        if(currentAnimation == null || currentAnimation.getCurrentFrame() == null) {
            System.out.println("TODO View-Animator (row 48), currentFrame is null?");
            return;
        }
        BufferedImage frame = currentAnimation.getCurrentFrame();
        Position screenPos = new Position(
                flipped ? frame.getWidth() + pos.getX() - (frame.getWidth() / 2) : pos.getX() - (frame.getWidth() / 2),
                pos.getY() - (frame.getHeight() / 2));
        int width = flipped ? -frame.getWidth() : frame.getWidth();
        int height = frame.getHeight();

        if (currentAnimation != null && Camera.screenPosInsideScreen(screenPos, frame.getWidth(), frame.getHeight())) {
            g2d.drawImage(frame, screenPos.getX(), screenPos.getY(), width, height, null);
        }
    }

    public void start() {
        this.paused = false;
    }

    public void pause() {
        this.paused = true;
    }
}
