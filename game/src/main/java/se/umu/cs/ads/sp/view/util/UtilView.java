package se.umu.cs.ads.sp.view.util;

import javax.swing.*;
import java.awt.*;

public class UtilView {
    // SCREEN SETTINGS
    public static final int originalTileSize = 64;
    public static final int originalEntitySize = 32;
    public static final int originalObjectSize = 16;

    public static double scale = 1;

    public static int tileSize = (int) (originalTileSize * scale);
    public static int entitySize = (int) (originalEntitySize * scale);
    public static int objectSize = (int) (originalObjectSize * scale);

    public static int screenWidth = 1280;
    public static int screenHeight = 720;

    public static int screenX = screenWidth / 2 - tileSize / 2;
    public static int screenY = screenHeight / 2 - tileSize / 2;

    public static boolean FULLSCREEN = false;
    public static boolean DARK_MODE = true;

    public static void changeScreenSize(int newWidth, int newHeight) {
        screenWidth = newWidth;
        screenHeight = newHeight;
    }

    public static void changeScale(double amount) {
        double newScale = scale + amount;
        if (newScale > 10 || newScale < 1) {
            return;
        }
        scale = newScale;
        tileSize = (int) (originalTileSize * scale);
        screenX = screenWidth / 2 - tileSize / 2;
        screenY = screenHeight / 2 - tileSize / 2;
        entitySize = (int) (originalEntitySize * scale);
        objectSize = (int) (originalObjectSize * scale);
    }

    public static void displayWarningMessage(Component parentComponent, String message) {
        JOptionPane.showMessageDialog(parentComponent, message, "Warning", JOptionPane.WARNING_MESSAGE);
    }

    public static void displayInfoMessage(Component parentComponent, String message) {
        JOptionPane.showMessageDialog(parentComponent, message, "Alert", JOptionPane.INFORMATION_MESSAGE);
    }

    public static boolean isFullscreenSupported() {
        GraphicsEnvironment graphicsEnvironment = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice graphicsDevice = graphicsEnvironment.getDefaultScreenDevice();
        return graphicsDevice.isFullScreenSupported();
    }

    public static void setDarkMode() {
        try {
            // Set Nimbus LookAndFeel
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
            // Customize colors for a custom light mode
            UIManager.put("control", new Color(50, 50, 50)); // Background color for components
            UIManager.put("info", new Color(50, 50, 50)); // Tooltip background
            UIManager.put("nimbusBase", new Color(18, 30, 49)); // Base color for Nimbus
            UIManager.put("nimbusAlertYellow", new Color(248, 187, 0));
            UIManager.put("nimbusDisabledText", new Color(128, 128, 128));
            UIManager.put("nimbusFocus", new Color(115, 164, 209));
            UIManager.put("nimbusGreen", new Color(176, 179, 50));
            UIManager.put("nimbusInfoBlue", new Color(66, 139, 221));
            UIManager.put("nimbusLightBackground", new Color(18, 30, 49)); // Light backgrounds
            UIManager.put("nimbusOrange", new Color(191, 98, 4));
            UIManager.put("nimbusRed", new Color(169, 46, 34));
            UIManager.put("nimbusSelectedText", new Color(255, 255, 255));
            UIManager.put("nimbusSelectionBackground", new Color(104, 93, 156));
            UIManager.put("text", new Color(230, 230, 230)); // General text color
            refreshUI();

        } catch (UnsupportedLookAndFeelException | ClassNotFoundException |
                 InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public static void setLightMode() {
        try {
            // Set Nimbus LookAndFeel
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");

            // Customize colors for a custom light mode
            UIManager.put("control", new Color(240, 240, 240));  // Background color for components
            UIManager.put("info", new Color(255, 255, 255));     // Tooltip background
            UIManager.put("nimbusBase", new Color(200, 200, 200)); // Base color for Nimbus
            UIManager.put("nimbusAlertYellow", new Color(255, 220, 35));
            UIManager.put("nimbusDisabledText", new Color(160, 160, 160));
            UIManager.put("nimbusFocus", new Color(115, 164, 209));
            UIManager.put("nimbusGreen", new Color(170, 220, 90));
            UIManager.put("nimbusInfoBlue", new Color(85, 150, 230));
            UIManager.put("nimbusLightBackground", new Color(255, 255, 255)); // Light background
            UIManager.put("nimbusOrange", new Color(255, 160, 0));
            UIManager.put("nimbusRed", new Color(255, 80, 70));
            UIManager.put("nimbusSelectedText", new Color(0, 0, 0)); // Text color for selections
            UIManager.put("nimbusSelectionBackground", new Color(180, 200, 240)); // Selection background
            UIManager.put("text", new Color(0, 0, 0));  // General text color
            refreshUI();
        } catch (UnsupportedLookAndFeelException | ClassNotFoundException |
                 InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    // Refresh UI for all open windows
    public static void refreshUI() {
        // Get all open windows (frames, dialogs, etc.)
        for (Window window : Window.getWindows()) {
            SwingUtilities.updateComponentTreeUI(window);
            window.pack();  // Optionally re-pack to adjust layout based on the new LookAndFeel
        }
    }
}
