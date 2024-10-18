package se.umu.cs.ads.sp.view.objects.entities;

import se.umu.cs.ads.sp.utils.Position;
import se.umu.cs.ads.sp.view.util.ImageLoader;

import java.awt.*;
import java.awt.image.BufferedImage;

public class HealthBar {

    private BufferedImage healthContainer;  // The container image (e.g., a frame with background)
    private BufferedImage healthBar;        // The red health bar image
    private int maxHealth = 100;
    private int currentHealth = 100;

    public HealthBar() {
        healthContainer = ImageLoader.loadImage("/sprites/hud/healthContainer.png");
        healthBar = ImageLoader.loadImage("/sprites/hud/healthBar.png");  // Load the red health bar image
    }

    public void setMaxHealth(int maxHealth) {
        this.maxHealth = maxHealth;
    }

    public void setCurrentHealth(int currentHealth) {
        this.currentHealth = Math.max(0, Math.min(currentHealth, maxHealth));  // Ensure health is within bounds
    }

    public void draw(Graphics2D g2d, Position pos, int containerWidth, int containerHeight) {
        int xPos = pos.getX() - containerWidth / 2;
        int yPos = pos.getY() - containerHeight / 2;
        // Draw the health container first (so it acts as the background)
        g2d.drawImage(healthContainer, xPos, yPos, containerWidth, containerHeight, null);

        // Calculate the health percentage
        double healthPercent = (double) currentHealth / maxHealth;

        // Compute health bar width relative to the container's inner space (considering padding)
        int padding = (int) (containerWidth * 0.05);  // Assume 5% padding, adjust as necessary
        int innerWidth = containerWidth - 2 * padding;  // Width inside the container after padding
        int innerHeight = containerHeight - 2 * padding; // Height inside the container after padding

        // Calculate the width of the health bar based on health percentage
        int barWidth = (int) (innerWidth * healthPercent);

        // Draw the red health bar within the container, aligned with padding
        if (barWidth > 0) {
            g2d.drawImage(healthBar, xPos + padding, yPos + padding,
                    barWidth, innerHeight, null);
        }
    }
}
