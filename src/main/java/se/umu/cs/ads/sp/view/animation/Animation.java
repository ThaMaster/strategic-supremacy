package se.umu.cs.ads.sp.view.animation;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class Animation {

    private final String name;
    private final ArrayList<BufferedImage> frames;

    private int currentFrame;
    private final int totalFrames;

    private int frameDelay;
    private int frameTimer;

    private boolean oneShot = false;

    public Animation(String name, ArrayList<BufferedImage> frames, int frameDelay) {
        this.name = name;
        this.frames = frames;
        this.frameDelay = frameDelay;
        this.totalFrames = frames.size();
        this.currentFrame = 0;
        this.frameTimer = 0;
    }

    public void update() {
        if ((oneShot && currentFrame == totalFrames - 1)) {
            return;
        }

        frameTimer++;
        if (frameTimer >= frameDelay) {
            frameTimer = 0;
            currentFrame = (currentFrame + 1) % totalFrames;
        }
    }

    public void setOneShot(boolean bool) {
        this.oneShot = bool;
    }

    public String getName() {
        return this.name;
    }

    public BufferedImage getCurrentFrame() {
        return this.frames.get(currentFrame);
    }
}
