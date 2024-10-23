package se.umu.cs.ads.sp.util;

import java.util.concurrent.TimeUnit;

public class Cooldown {

    private final long cooldown;
    private long cooldownStartTime;

    public Cooldown(long cooldown, TimeUnit timeUnit) {
        long multiplier = 1;
        switch (timeUnit){
            case SECONDS -> multiplier = 1000;
            case MILLISECONDS -> multiplier = 1;
        }
        this.cooldown = cooldown * multiplier; // Convert seconds to milliseconds
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