package se.umu.cs.ads.sp.model.components;

public class Cooldown {

    private final long cooldown; // Cooldown duration in milliseconds
    private long cooldownStartTime;

    public Cooldown(long cooldown) {
        this.cooldown = cooldown * 1000; // Convert seconds to milliseconds
    }

    public void start() {
        cooldownStartTime = System.currentTimeMillis(); // Set the start time
    }

    public void reset() {
        start(); // Reset the cooldown by starting it again
    }

    public boolean hasElapsed() {
        return System.currentTimeMillis() - cooldownStartTime >= cooldown;
    }
}