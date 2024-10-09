package se.umu.cs.ads.sp.view.windows.panels.lobbypanel;

import se.umu.cs.ads.sp.view.util.StyleConstants;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.image.BufferedImage;

public class MapPanel extends JPanel {

    private BufferedImage map;

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

        this.add(mapName, BorderLayout.NORTH);
    }

    public void setMapPreview(BufferedImage mapImage, String mapName) {
        this.mapName.setText(mapName);
        this.map = mapImage;
        this.revalidate();
        this.repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (map != null) {
            // Calculate the available drawing area excluding the border
            Insets insets = getInsets();
            int x = insets.left;
            int y = insets.top + mapName.getHeight();
            int width = getWidth() - insets.left - insets.right;
            int height = getHeight() - insets.top - insets.bottom - mapName.getHeight();

            // Draw the image within the available space
            g.drawImage(map, x, y, width, height, null);
        }
    }
}