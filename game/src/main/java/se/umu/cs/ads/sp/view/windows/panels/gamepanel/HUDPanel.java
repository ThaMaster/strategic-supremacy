package se.umu.cs.ads.sp.view.windows.panels.gamepanel;

import se.umu.cs.ads.sp.view.util.UtilView;

import javax.swing.*;
import java.awt.*;

public class HUDPanel extends JPanel {

    private JLabel selectedUnitLabel;
    private JLabel selectedUnitHealthLabel;
    private JLabel moneyLabel;
    private JLabel scoreLabel;

    public HUDPanel() {
        super();
        this.setOpaque(false); // HUD is transparent, overlays game panel
        this.setLayout(null);  // Absolute positioning to place HUD elements freely
        this.setPreferredSize(new Dimension(UtilView.screenWidth, UtilView.screenHeight));
        this.setBounds(0, 0, UtilView.screenWidth, UtilView.screenHeight);

        initHUDComponents();
    }

    /**
     * Initialize the HUD components, including player stats and selected unit info.
     */
    private void initHUDComponents() {
        // Player stats section (top left corner)
        moneyLabel = createHUDLabel("Money: $0", 20, 20, 150, 30);
        scoreLabel = createHUDLabel("Score: 0", 20, 60, 150, 30);

        // Selected unit information (bottom left corner)
        selectedUnitLabel = createHUDLabel("Selected Unit: None", 20, UtilView.screenHeight - 100, 300, 30);
        selectedUnitHealthLabel = createHUDLabel("Health: N/A", 20, UtilView.screenHeight - 60, 150, 30);

        // Add components to HUDPanel
        this.add(moneyLabel);
        this.add(scoreLabel);
        this.add(selectedUnitLabel);
        this.add(selectedUnitHealthLabel);
    }

    /**
     * Helper method to create HUD labels with consistent style and position.
     */
    private JLabel createHUDLabel(String text, int x, int y, int width, int height) {
        JLabel label = new JLabel(text);
        label.setForeground(Color.WHITE); // White text for HUD
        label.setFont(new Font("Arial", Font.BOLD, 16)); // Customize font and size
        label.setBounds(x, y, width, height); // Set position and size
        return label;
    }

    /**
     * Update the player's money display on the HUD.
     * @param money The new money value.
     */
    public void updateMoney(int money) {
        moneyLabel.setText("Money: $" + money);
    }

    /**
     * Update the player's score display on the HUD.
     * @param score The new score value.
     */
    public void updateScore(int score) {
        scoreLabel.setText("Score: " + score);
    }

    /**
     * Update the selected unit's information on the HUD.
     * @param unitName The name of the selected unit.
     * @param health The health of the selected unit.
     */
    public void updateSelectedUnit(String unitName, int health) {
        selectedUnitLabel.setText("Selected Unit: " + unitName);
        selectedUnitHealthLabel.setText("Health: " + health);
    }

    /**
     * Clear the selected unit's information (when no unit is selected).
     */
    public void clearSelectedUnit() {
        selectedUnitLabel.setText("Selected Unit: None");
        selectedUnitHealthLabel.setText("Health: N/A");
    }
}
