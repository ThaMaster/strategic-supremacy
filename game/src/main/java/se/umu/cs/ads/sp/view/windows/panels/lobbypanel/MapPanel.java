package se.umu.cs.ads.sp.view.windows.panels.lobbypanel;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import javax.swing.border.EmptyBorder;

public class MapPanel extends JPanel {

    private BufferedImage map;

    public MapPanel() {
        this.setLayout(new BorderLayout());
        this.setPreferredSize(new Dimension(300, 300));
        // Set an empty border with 10px padding
        this.setBorder(new EmptyBorder(10, 10, 10, 10));
    }

    public void setMapPreview(BufferedImage mapImage) {
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
            int y = insets.top;
            int width = getWidth() - insets.left - insets.right;
            int height = getHeight() - insets.top - insets.bottom;

            // Draw the image within the available space
            g.drawImage(map, x, y, width, height, null);
        }
    }
}