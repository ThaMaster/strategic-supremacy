package se.umu.cs.ads.sp.view.windows.panels.gamepanel;

import se.umu.cs.ads.sp.view.util.ImageLoader;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class ContainerPanel extends JPanel {
    private final BufferedImage containerImage;

    public ContainerPanel(String containerType, int width, int height) {
        this.setLayout(new BorderLayout());
        this.setOpaque(false); // HUD is transparent, overlays game panel
        setPreferredSize(new Dimension(width, height)); // Set a preferred size

        String fileToLoad = switch (containerType) {
            case "wood" -> "woodenContainer.png";
            case "stone" -> "stoneContainer.png";
            default -> "";
        };

        containerImage = ImageLoader.loadImage("/sprites/hud/containers/" + fileToLoad);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (containerImage != null) {
            g.drawImage(containerImage, 0, 0, getWidth(), getHeight(), this);
        }
    }
}
