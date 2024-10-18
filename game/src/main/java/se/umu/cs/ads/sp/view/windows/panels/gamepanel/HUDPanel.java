package se.umu.cs.ads.sp.view.windows.panels.gamepanel;

import se.umu.cs.ads.sp.view.util.ImageLoader;
import se.umu.cs.ads.sp.view.util.StyleConstants;
import se.umu.cs.ads.sp.view.util.UtilView;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

public class HUDPanel extends JPanel {

    private JLabel timerLabel;
    private JLabel selectedUnitLabel;
    private JLabel selectedUnitHealthLabel;
    private JLabel moneyLabel;
    private JLabel scoreLabel;

    private UpgradePanel upgradePanel;

    private JButton openShopButton;

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

        ContainerPanel timerPanel = new ContainerPanel("wood", 130, 50);
        timerPanel.setBounds(UtilView.screenWidth / 2 - 65, 20, 130, 50);
        timerLabel = new JLabel("01:00");
        timerLabel.setForeground(Color.RED); // White text for HUD
        timerLabel.setFont(StyleConstants.HUD_TEXT); // Customize font and size
        timerLabel.setHorizontalAlignment(JLabel.CENTER);
        timerLabel.setVerticalAlignment(JLabel.CENTER);
        timerPanel.add(timerLabel, BorderLayout.CENTER);

        moneyLabel = createHUDLabel("Money: $0", 20, 20, 150, 30);
        scoreLabel = createHUDLabel("Score: 0", 20, 60, 150, 30);

        // Selected unit information (bottom left corner)
        selectedUnitLabel = createHUDLabel("", 20, UtilView.screenHeight - 100, 300, 30);
        selectedUnitHealthLabel = createHUDLabel("", 20, UtilView.screenHeight - 60, 150, 30);

        openShopButton = createHUDShopButton(UtilView.screenWidth - 100, UtilView.screenHeight - 60, 75, 50);

        upgradePanel = new UpgradePanel(500, 500, 2);
        upgradePanel.setBounds(UtilView.screenWidth / 2 - 250, UtilView.screenHeight / 2 - 250, 500, 500);

        // Add components to HUDPanel
        this.add(timerPanel);
        this.add(moneyLabel);
        this.add(scoreLabel);
        this.add(selectedUnitLabel);
        this.add(selectedUnitHealthLabel);
        this.add(openShopButton);
        this.add(upgradePanel);
    }

    /**
     * Helper method to create HUD labels with consistent style and position.
     */
    private JLabel createHUDLabel(String text, int x, int y, int width, int height) {
        JLabel label = new JLabel(text);
        label.setForeground(Color.RED); // White text for HUD
        label.setFont(StyleConstants.HUD_TEXT); // Customize font and size
        label.setBounds(x, y, width, height); // Set position and size
        return label;
    }

    private JButton createHUDShopButton(int x, int y, int width, int height) {
        Image defaultImage = Objects.requireNonNull(
                        ImageLoader.loadImage("/sprites/hud/buttons/buttonLarge.png"))
                .getScaledInstance(width, height, Image.SCALE_FAST);
        Image pressedImage = Objects.requireNonNull(
                        ImageLoader.loadImage("/sprites/hud/buttons/buttonLargePressed.png"))
                .getScaledInstance(width, height, Image.SCALE_FAST);

        ImageIcon defaultIcon = new ImageIcon(defaultImage);
        ImageIcon pressedIcon = new ImageIcon(pressedImage);
        JButton button = new JButton(defaultIcon);
        button.setBounds(x, y, width, height);

        // Remove any default button decorations like borders
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setContentAreaFilled(false);
        button.addActionListener(new ActionListener() {
            private boolean pressed = false;

            @Override
            public void actionPerformed(ActionEvent e) {
                pressed = !pressed;
                if (pressed) {
                    button.setIcon(pressedIcon);
                    upgradePanel.setVisible(true);
                } else {
                    button.setIcon(defaultIcon);
                    upgradePanel.setVisible(false);
                }
            }
        });
        return button;
    }

    /**
     * Update the player's money display on the HUD.
     *
     * @param money The new money value.
     */
    public void updateMoney(int money) {
        moneyLabel.setText("Money: $" + money);
    }

    /**
     * Update the player's score display on the HUD.
     *
     * @param score The new score value.
     */
    public void updateScore(int score) {
        scoreLabel.setText("Score: " + score);
    }

    public void updateSelectedUnit(ArrayList<String> unitNames) {
        StringBuilder nameBuilder = new StringBuilder();

        if (unitNames.size() == 1) {
            // If only one unit is selected, simply append its name
            nameBuilder.append("Selected: ").append(unitNames.get(0));
        } else if (!unitNames.isEmpty()) {
            nameBuilder.append("Selected: ");

            // Use a LinkedHashMap to count the occurrences of each unit name
            Map<String, Integer> unitCountMap = new LinkedHashMap<>();
            for (String unitName : unitNames) {
                unitCountMap.put(unitName, unitCountMap.getOrDefault(unitName, 0) + 1);
            }
            // Build the names string
            for (Map.Entry<String, Integer> entry : unitCountMap.entrySet()) {
                String unitName = entry.getKey();
                int count = entry.getValue();
                // Append the unit name with its count if greater than 1
                if (count > 1) {
                    nameBuilder.append("[").append(count).append("] ").append(unitName);
                } else {
                    nameBuilder.append(unitName);
                }
                // Append comma if there are more units to process
                nameBuilder.append(", ");
            }

            // Remove trailing comma
            nameBuilder.setLength(nameBuilder.length() - 2);
        }

        // Update the label with the built names string
        selectedUnitLabel.setText(nameBuilder.toString());
    }



    /**
     * Clear the selected unit's information (when no unit is selected).
     */
    public void clearSelectedUnit() {
        selectedUnitLabel.setText("");
        selectedUnitHealthLabel.setText("");
    }

}
