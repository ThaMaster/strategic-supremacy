package se.umu.cs.ads.sp.view.windows.panels.gamepanel.hudPanel;

import se.umu.cs.ads.sp.view.util.ImageLoader;
import se.umu.cs.ads.sp.view.util.StyleConstants;
import se.umu.cs.ads.sp.view.util.UtilView;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class DefeatedPanel extends JPanel {
    BufferedImage containerImage;

    public DefeatedPanel(int width, int height) {
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        this.setOpaque(false);

        containerImage = ImageLoader.loadImage("/sprites/hud/containers/woodenContainer.png");

        // Create the "YOU ARE DEAD" label
        JLabel gameOverLabel = new JLabel("YOU ARE DEFEATED");
        gameOverLabel.setFont(StyleConstants.HUD_TITLE_TEXT);
        gameOverLabel.setForeground(Color.RED.darker());
        gameOverLabel.setAlignmentX(CENTER_ALIGNMENT);

        // Create the message label beneath it
        JLabel firstLabel = new JLabel("All your units are dead!");
        firstLabel.setFont(StyleConstants.HUD_TEXT);
        firstLabel.setForeground(Color.BLACK);
        firstLabel.setAlignmentX(CENTER_ALIGNMENT);

        // Create the message label beneath it
        JLabel secondLabel = new JLabel("Waiting for next round...");
        secondLabel.setFont(StyleConstants.HUD_SMALL_TEXT);
        secondLabel.setForeground(Color.BLACK.brighter());
        secondLabel.setAlignmentX(CENTER_ALIGNMENT);

        // Add labels to the panel
        this.add(Box.createVerticalGlue()); // Spacer to center the text vertically
        this.add(gameOverLabel);
        this.add(Box.createRigidArea(new Dimension(0, 10))); // Space between labels
        this.add(firstLabel);
        this.add(Box.createRigidArea(new Dimension(0, 10))); // Space between labels
        this.add(secondLabel);
        this.add(Box.createVerticalGlue()); // Spacer to center the text vertically

        // Set the bounds and add the panel to the HUD
        this.setBounds(UtilView.screenWidth / 2 - width / 2, UtilView.screenHeight / 2 - height / 2, width, height);
        this.setVisible(false); // Initially hidden
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (containerImage != null) {
            g.drawImage(containerImage, 0, 0, getWidth(), getHeight(), this);
        }
    }
}
