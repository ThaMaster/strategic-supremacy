package se.umu.cs.ads.sp.utils;

public class Cooldown {

    private final long cooldown;
    private long cooldownStartTime;

    public Cooldown(long cooldown) {
        this.cooldown = cooldown * 1000; // Convert seconds to milliseconds
    }

    public void start() {
        cooldownStartTime = System.currentTimeMillis();
    }

    public void reset() {
        start();
    }

    public boolean hasElapsed() {
        return System.currentTimeMillis() - cooldownStartTime >= cooldown;
    }
}