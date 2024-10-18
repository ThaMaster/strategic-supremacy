package se.umu.cs.ads.sp.view.windows.panels.gamepanel;

import se.umu.cs.ads.sp.view.util.ImageLoader;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class UpgradePanel extends JPanel {

    private BufferedImage panelImage;

    public UpgradePanel(int width, int height) {
        this.setLayout(new BorderLayout());
        this.setOpaque(false); // HUD is transparent, overlays game panel
        setPreferredSize(new Dimension(width, height)); // Set a preferred size
        panelImage = ImageLoader.loadImage("/sprites/hud/containers/upgradeContainer.png");
        this.setVisible(false);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (panelImage != null) {
            g.drawImage(panelImage, 0, 0, getWidth(), getHeight(), this);
        }
    }
}
