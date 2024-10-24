package se.umu.cs.ads.sp.view.windows.panels.gamepanel.hudPanel;

import se.umu.cs.ads.sp.view.objects.entities.units.PlayerUnitView;
import se.umu.cs.ads.sp.view.soundmanager.SoundFX;
import se.umu.cs.ads.sp.view.soundmanager.SoundManager;
import se.umu.cs.ads.sp.view.util.ImageLoader;
import se.umu.cs.ads.sp.view.util.StyleConstants;
import se.umu.cs.ads.sp.view.util.UtilView;
import se.umu.cs.ads.sp.view.windows.panels.gamepanel.GamePanel;

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
    private JLabel moneyLabel;
    private JLabel scoreLabel;

    private UpgradePanel upgradePanel;
    private ContainerPanel selectedUnitPanel;
    private DefeatedPanel defeatPanel;

    private JButton openShopButton;

    private ImageIcon defaultIcon;
    private ImageIcon pressedIcon;

    private final GamePanel gamePanel;

    public HUDPanel(GamePanel gp) {
        super();
        this.gamePanel = gp;
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
        timerLabel = new JLabel("");
        moneyLabel = new JLabel("Money: $0");
        scoreLabel = new JLabel("Score: 0");
        selectedUnitLabel = new JLabel("");

        ContainerPanel timerPanel = createHUDContainerLabel(timerLabel, UtilView.screenWidth / 2 - 65, 20, 100, 40);
        ContainerPanel scorePanel = createHUDContainerLabel(scoreLabel, 20, 20, 170, 50);
        ContainerPanel moneyPanel = createHUDContainerLabel(moneyLabel, 20, 70, 170, 50);
        selectedUnitPanel = createHUDContainerLabel(selectedUnitLabel, 20, UtilView.screenHeight - 100, 310, 100);

        openShopButton = createHUDShopButton(UtilView.screenWidth / 2 - 35, UtilView.screenHeight - 80);
        upgradePanel = new UpgradePanel(gamePanel, 900, 550);
        defeatPanel = new DefeatedPanel(400, 250);
        // Add components to HUDPanel
        this.add(timerPanel);
        this.add(moneyPanel);
        this.add(scorePanel);
        this.add(selectedUnitPanel);
        this.add(openShopButton);
        this.add(upgradePanel);
        this.add(defeatPanel);
    }

    private ContainerPanel createHUDContainerLabel(JLabel label, int x, int y, int width, int height) {
        ContainerPanel cPanel = new ContainerPanel("wood", width, height);
        cPanel.setBounds(x, y, width, height);
        label.setForeground(Color.BLACK); // White text for HUD
        label.setFont(StyleConstants.HUD_TEXT); // Customize font and size
        label.setVerticalAlignment(JLabel.CENTER);
        label.setHorizontalAlignment(JLabel.CENTER);
        cPanel.add(label, BorderLayout.CENTER);
        return cPanel;
    }

    private JButton createHUDShopButton(int x, int y) {
        Image defaultImage = Objects.requireNonNull(
                        ImageLoader.loadImage("/sprites/hud/buttons/shopButton.png"))
                .getScaledInstance(70, 50, Image.SCALE_FAST);
        Image pressedImage = Objects.requireNonNull(
                        ImageLoader.loadImage("/sprites/hud/buttons/shopButtonPressed.png"))
                .getScaledInstance(70, 50, Image.SCALE_FAST);

        defaultIcon = new ImageIcon(defaultImage);
        pressedIcon = new ImageIcon(pressedImage);
        JButton button = new JButton(defaultIcon);
        button.setBounds(x, y, 70, 50);

        // Remove any default button decorations like borders
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setContentAreaFilled(false);
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                toggleUpgradeMenu();
            }
        });
        return button;
    }

    public void updateMoney(int money) {
        moneyLabel.setText("Money: $" + money);
        upgradePanel.updateMoney(money);
    }

    public void updateScore(int score) {
        scoreLabel.setText("Score: " + score);
    }

    public void updateTimer(int minutes, int seconds) {
        if (minutes == 0 && seconds < 30) {
            timerLabel.setForeground(Color.RED.darker().darker());
        } else {
            timerLabel.setForeground(Color.BLACK);
        }
        timerLabel.setText(String.format("%02d:%02d", minutes, seconds));
    }

    public void updateSelectedUnit(ArrayList<String> unitNames) {
        StringBuilder nameBuilder = new StringBuilder();

        if (unitNames.size() == 1) {
            // If only one unit is selected, simply append its name
            nameBuilder.append("Selected: ").append(unitNames.get(0));
            selectedUnitLabel.setText(nameBuilder.toString());
            selectedUnitPanel.setVisible(true);
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
            // Update the label with the built names string
            selectedUnitLabel.setText(nameBuilder.toString());
            selectedUnitPanel.setVisible(true);
        } else {
            selectedUnitLabel.setText(nameBuilder.toString());
            selectedUnitPanel.setVisible(false);
        }


    }

    public void toggleUpgradeMenu() {
        if (defeatPanel.isVisible()) {
            return;
        }
        upgradePanel.setVisible(!upgradePanel.isVisible());
        if (!upgradePanel.isVisible()) {
            openShopButton.setIcon(defaultIcon);
            gamePanel.requestFocusInWindow();
        } else {
            openShopButton.setIcon(pressedIcon);
        }
        SoundManager.getInstance().play(SoundFX.OPEN_SHOP);
    }

    public void setUpgradeMenu(ArrayList<PlayerUnitView> unitIds) {
        upgradePanel.setUpgradeMenu(unitIds);
    }

    public void updateStat(long unitId, String upgradeName) {
        upgradePanel.updateStat(unitId, upgradeName);
    }

    public void showDefeatPanel(boolean bool) {
        openShopButton.setVisible(!bool);
        selectedUnitPanel.setVisible(!bool);
        defeatPanel.setVisible(bool);
    }
}
