package se.umu.cs.ads.sp.view.util;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class StyleConstants {

    // Default font definitions
    public static final Font TITLE_FONT = new Font("serif", Font.BOLD, 24);
    public static final Font LABEL_FONT = new Font("serif", Font.BOLD, 18);
    public static final Font BUTTON_FONT = new Font("serif", Font.BOLD, 16);
    public static final Font PLAIN_TEXT_FONT = new Font("serif", Font.PLAIN, 14);
    public static final Font TEXT_FIELD_FONT = new Font("serif", Font.PLAIN, 14);

    public static final Font HUD_TITLE_TEXT = new Font("Monospaced", Font.BOLD, 30);
    public static final Font HUD_TEXT = new Font("Monospaced", Font.BOLD, 16);
    public static final Font HUD_SMALL_TEXT = new Font("Monospaced", Font.BOLD, 10);


    // Default color definitions
    public static final Color PRIMARY_COLOR = new Color(0, 122, 255); // Blue
    public static final Color SECONDARY_COLOR = new Color(255, 255, 255); // White
    public static final Color BACKGROUND_COLOR = new Color(150, 150, 150); // Light gray
    public static final Color ERROR_COLOR = new Color(255, 0, 0); // Red

    public static final Color ALLY_COLOR = Color.BLUE;
    public static final Color ENEMY_COLOR = Color.RED;
    public static final Color GOLD_COLOR = Color.YELLOW;

    // Add additional styles as needed
    public static final Border DEFAULT_BORDER = BorderFactory.createLineBorder(Color.GRAY, 1);
    public static final EmptyBorder DEFAULT_EMPTY_BORDER = new EmptyBorder(10, 10, 10, 10);

    public static void applyLabelStyle(JLabel label) {
        label.setFont(LABEL_FONT);
        label.setForeground(Color.BLACK);
    }

    public static void applyButtonStyle(JButton button) {
        button.setFont(BUTTON_FONT);
        button.setBackground(PRIMARY_COLOR);
        button.setForeground(SECONDARY_COLOR);
        button.setBorder(DEFAULT_BORDER);
    }

    public static void applyTextFieldStyle(JTextField textField) {
        textField.setFont(TEXT_FIELD_FONT);
        textField.setForeground(Color.BLACK);
        textField.setBackground(SECONDARY_COLOR);
        textField.setBorder(DEFAULT_BORDER);
    }
}
