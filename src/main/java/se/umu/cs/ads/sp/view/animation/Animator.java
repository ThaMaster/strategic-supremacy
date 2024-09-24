package se.umu.cs.ads.sp.view.animation;

import se.umu.cs.ads.sp.utils.Position;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.HashMap;

public class Animator {

    private HashMap<String, Animation> animations;
    private Animation currentAnimation;

    private boolean flipped = false;

    public Animator() {
        this.animations = new HashMap<>();
    }

    public Animation getCurrentAnimation() {
        return this.currentAnimation;
    }

    public void addAnimation(String animationName, Animation animation) {
        this.animations.put(animationName, animation);
    }

    public void changeAnimation(String animationName) {
        currentAnimation = animations.get(animationName);
    }

    public void update() {
        // Change animation frame
        if(currentAnimation != null) {
            currentAnimation.update();
        }
    }

    public void setFlipped(boolean bool) {
        this.flipped = bool;
    }

    public void draw(Graphics2D g2d, Position pos) {
        // Draw the current frame of animation
        if(currentAnimation != null) {
            BufferedImage frame = currentAnimation.getCurrentFrame();
            if(flipped) {
                g2d.drawImage(frame, frame.getWidth()+pos.getX(), pos.getY(), -frame.getWidth(), frame.getHeight(), null);
            } else {
                g2d.drawImage(frame, pos.getX(), pos.getY(), frame.getWidth(), frame.getHeight(), null);
            }
        }
    }
}