package se.umu.cs.ads.sp.view.windows.panels.lobbypanel;

import se.umu.cs.ads.sp.view.util.StyleConstants;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.image.BufferedImage;

public class MapPanel extends JPanel {

    private JLabel mapLabel; // JLabel to display the map
    private JLabel mapName;

    public MapPanel() {
        this.setLayout(new BorderLayout());
        this.setPreferredSize(new Dimension(300, 300));
        // Set an empty border with 10px padding
        this.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Create and configure the title label
        mapName = new JLabel("");
        mapName.setFont(StyleConstants.LABEL_FONT);
        mapName.setHorizontalAlignment(SwingConstants.CENTER); // Center the text
        mapName.setBorder(new EmptyBorder(0, 0, 5, 0)); // Add some space below the label

        // Create the JLabel to display the map
        mapLabel = new JLabel();
        mapLabel.setHorizontalAlignment(SwingConstants.CENTER); // Center the image
        mapLabel.setVerticalAlignment(SwingConstants.CENTER); // Center the image vertically

        this.add(mapName, BorderLayout.NORTH);
        this.add(mapLabel, BorderLayout.CENTER); // Add the map label to the center of the panel
    }

    public void setMapPreview(BufferedImage mapImage, String mapName) {
        this.mapName.setText(mapName);
        BufferedImage scaledImage = scaleImage(mapImage, mapLabel.getWidth(), mapLabel.getHeight());
        this.mapLabel.setIcon(new ImageIcon(scaledImage));
        this.revalidate();
        this.repaint();
    }

    // Scaling method that maintains the aspect ratio
    private BufferedImage scaleImage(BufferedImage originalImage, int targetWidth, int targetHeight) {
        if (originalImage == null) {
            return null; // Return null if the original image is null
        }

        // Get original dimensions
        int originalWidth = originalImage.getWidth();
        int originalHeight = originalImage.getHeight();

        // Calculate the aspect ratios
        double aspectRatio = (double) originalWidth / originalHeight;

        // Determine new dimensions while preserving the aspect ratio
        int newWidth, newHeight;
        if (targetWidth / aspectRatio <= targetHeight) {
            newWidth = targetWidth;
            newHeight = (int) (targetWidth / aspectRatio);
        } else {
            newHeight = targetHeight;
            newWidth = (int) (targetHeight * aspectRatio);
        }

        // Create a new scaled image
        BufferedImage scaledImage = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = scaledImage.createGraphics();
        g.drawImage(originalImage, 0, 0, newWidth, newHeight, null);
        g.dispose();

        return scaledImage;
    }
}
