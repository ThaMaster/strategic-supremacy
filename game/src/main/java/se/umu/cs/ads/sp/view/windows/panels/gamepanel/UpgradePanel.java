package se.umu.cs.ads.sp.view.windows.panels.gamepanel;

import se.umu.cs.ads.sp.utils.enums.RewardType;
import se.umu.cs.ads.sp.utils.enums.UnitType;
import se.umu.cs.ads.sp.view.util.ImageLoader;
import se.umu.cs.ads.sp.view.util.UtilView;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Objects;

public class UpgradePanel extends JPanel {

    private BufferedImage panelImage;
    private ImageIcon defaultIcon;
    private ImageIcon pressedIcon;
    private ImageIcon disabledIcon;

    private final ArrayList<JButton> buyButtons = new ArrayList<>();

    private int money = 0; // Initial money, you can modify this externally

    private GamePanel gamePanel;

    private final int panelWidth;
    private final int panelHeight;

    public UpgradePanel(GamePanel gp, int width, int height) {
        this.setLayout(null); // No layout manager, manually setting bounds
        this.setOpaque(false); // HUD is transparent, overlays game panel

        initImages();

        panelWidth = width;
        panelHeight = height;
        gamePanel = gp;

        this.add(createTitle("UNIT UPGRADES"));

        this.setVisible(false);
        this.setBounds(UtilView.screenWidth / 2 - width / 2, UtilView.screenHeight / 2 - height / 2, width, height);
    }

    private JLabel createTitle(String title) {
        JLabel upgradeLabel = new JLabel(title);
        upgradeLabel.setFont(new Font("Monospaced", Font.BOLD, 35));
        upgradeLabel.setForeground(Color.BLACK);
        upgradeLabel.setHorizontalAlignment(JLabel.CENTER);
        upgradeLabel.setBounds(panelWidth / 2 - (int) (panelWidth * 0.75) / 2, 20, (int) (panelWidth * 0.75), 70);
        return upgradeLabel;
    }

    public void setUpgradeMenu(ArrayList<Long> unitIds, ArrayList<UnitType> types) {
        JPanel upgradeList = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        upgradeList.setOpaque(false);
        int rowOffset = 0;

        JPanel topPanel = new JPanel(new GridBagLayout());
        topPanel.setOpaque(false);
        GridBagConstraints topGbc = new GridBagConstraints();
        // Add unit icons in the first row
        for (int i = 0; i < types.size(); i++) {
            ImageIcon unitIcon = new ImageIcon(ImageLoader.loadUnitIcon(types.get(i)));
            JLabel unitImage = new JLabel(unitIcon);
            topGbc.gridx = i;
            topGbc.gridy = 0;
            topGbc.gridwidth = 1;  // Reset grid width for the upgrade row
            topGbc.insets = new Insets(0, 70, 0, 70);
            topPanel.add(unitImage, topGbc);
        }

        // Add the upgrade type panel to the main upgrade list
        gbc.gridx = 0; // Add all upgrade type panels in the first column
        gbc.gridy = rowOffset;
        gbc.gridwidth = unitIds.size(); // Span the upgrade type panel across all unit columns
        gbc.insets = new Insets(25, 0, 5, 0); // Add space above and below the upgrade type panel
        upgradeList.add(topPanel, gbc);
        rowOffset++;

        // Create a panel for each upgrade type
        for (String upgradeType : RewardType.getUpgradeTypes()) {
            // Create a panel for the upgrade type
            JPanel upgradeTypePanel = new JPanel(new GridBagLayout());
            upgradeTypePanel.setBorder(new LineBorder(Color.BLACK, 3, true));
            upgradeTypePanel.setOpaque(false);
            GridBagConstraints typeGbc = new GridBagConstraints();
            typeGbc.weightx = 1;
            typeGbc.weighty = 0;

            // Add the upgrade type label to the upgrade type panel
            typeGbc.gridx = 0;
            typeGbc.gridy = 0;
            typeGbc.gridwidth = unitIds.size();  // Span the label across all unit columns
            JLabel upgradeLabel = new JLabel(upgradeType);
            upgradeTypePanel.add(upgradeLabel, typeGbc);

            // Add upgrade rows for each unit in this type panel
            for (int t = 0; t < unitIds.size(); t++) {
                typeGbc.gridx = t;
                typeGbc.gridy = 1;  // Place upgrade rows below the label
                typeGbc.gridwidth = 1;  // Reset grid width for the upgrade row

                JPanel upgradeRow = createUpgradeRow(upgradeType, 100, 10, unitIds.get(t), 150);
                upgradeRow.setOpaque(false);
                upgradeTypePanel.add(upgradeRow, typeGbc);
            }

            // Add the upgrade type panel to the main upgrade list
            gbc.gridx = 0; // Add all upgrade type panels in the first column
            gbc.gridy = rowOffset;
            gbc.gridwidth = unitIds.size(); // Span the upgrade type panel across all unit columns
            gbc.insets = new Insets(5, 0, 5, 0); // Add space above and below the upgrade type panel
            upgradeList.add(upgradeTypePanel, gbc);

            rowOffset++;  // Increment to the next row for the next upgrade type
        }

        // Set size and add to the main container
        upgradeList.setBounds(0, 0, panelWidth, panelHeight);
        upgradeList.setOpaque(false);
        this.add(upgradeList);
    }

    private JPanel createUpgradeRow(String upgradeName, int currentStat, int upgradeAmount, long unitId, int initialPrice) {
        JPanel upgradeRowPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        upgradeRowPanel.setOpaque(false);

        // Setting common constraints for all components
        gbc.fill = GridBagConstraints.HORIZONTAL; // Fill the available space horizontally
        gbc.weightx = 1; // Allow components to grow horizontally

        // Upgrade amount label (number of upgrades)
        JLabel currentLevelLabel = new JLabel("Level: " + 0);
        gbc.gridx = 0; // First column
        gbc.gridy = 0; // First row
        gbc.weightx = 0; // Don't allow the number label to grow
        gbc.insets = new Insets(0, 15, 0, 0); // Add padding around components
        upgradeRowPanel.add(currentLevelLabel, gbc);

        // Stat panel to display the current stat and upgrade amount
        JPanel statPanel = new JPanel(new GridBagLayout());
        statPanel.setOpaque(false);
        statPanel.setBorder(new LineBorder(Color.BLACK, 2, true));

        // Adding current stat label
        JLabel currentStatLabel = new JLabel(String.valueOf(currentStat));
        gbc.gridx = 0; // First column
        gbc.gridy = 0; // First row in the stat panel
        gbc.weightx = 0; // Allow it to grow
        gbc.insets = new Insets(0, 3, 0, 0); // Add padding around components
        statPanel.add(currentStatLabel, gbc);

        JLabel arrowLabel = new JLabel("->");
        gbc.gridx = 1; // First column
        gbc.gridy = 0; // First row in the stat panel
        gbc.weightx = 0; // Allow it to grow
        statPanel.add(arrowLabel, gbc);

        // Adding upgrade amount label
        JLabel upgradedStatLabel = new JLabel(String.valueOf(currentStat + upgradeAmount));
        upgradedStatLabel.setForeground(Color.GREEN.darker().darker());
        gbc.gridx = 2;
        gbc.gridy = 0;
        gbc.weightx = 0;
        gbc.insets = new Insets(0, 0, 0, 3); // Add padding around components

        statPanel.add(upgradedStatLabel, gbc);

        // Add stat panel to the upgrade row panel
        gbc.gridx = 1; // Second column in the upgrade row
        gbc.gridy = 0; // First row
        gbc.weightx = 1; // Allow the stat panel to grow
        gbc.insets = new Insets(0, 0, 0, 15); // Add padding around components
        upgradeRowPanel.add(statPanel, gbc);

        // Create and configure the buy button
        JButton buyButton = createBuyButton();

        // Create separate labels for "Price:" and the actual price
        JPanel pricePanel = new JPanel(new GridBagLayout());
        pricePanel.setOpaque(false);

        JLabel priceLabelText = new JLabel("Price:");
        // Set constraints for the "Price:" label
        gbc.gridx = 0; // Third column for "Price:" label
        gbc.gridy = 0; // First row
        gbc.weightx = 0; // Don't allow the label to grow
        gbc.insets = new Insets(0, 0, 0, 5); // Add padding around components
        pricePanel.add(priceLabelText, gbc);

        JLabel currentPriceLabel = new JLabel("$" + initialPrice);
        // Set constraints for the "Price:" label
        gbc.gridx = 1; // Third column for "Price:" label
        gbc.gridy = 0; // First row
        gbc.weightx = 1; // Don't allow the label to grow
        pricePanel.add(currentPriceLabel, gbc);

        gbc.gridx = 0; // Second column in the upgrade row
        gbc.gridy = 1; // First row
        gbc.weightx = 0; // Allow the stat panel to grow
        gbc.insets = new Insets(0, 15, 0, 15); // Add padding around components
        upgradeRowPanel.add(pricePanel, gbc);

        buyButton.addActionListener(new BuyButtonListener(initialPrice, currentStat, upgradeAmount,
                currentStatLabel, upgradedStatLabel, currentLevelLabel, currentPriceLabel, upgradeName, unitId));

        // Initial button state check
        updateButtonState(buyButton, currentPriceLabel, initialPrice);

        buyButtons.add(buyButton);

        // Add the buy button to the upgrade row panel
        gbc.gridx = 1; // Fourth column for buy button
        gbc.gridy = 1;
        gbc.weightx = 0; // Don't allow the button to grow
        gbc.insets = new Insets(0, 0, 0, 15); // Add padding around components
        upgradeRowPanel.add(buyButton, gbc);

        return upgradeRowPanel;
    }

    // Update the button state based on available money
    private void updateButtonState(JButton button, JLabel currentPriceLabel, int currentPrice) {
        if (money < currentPrice) {
            button.setEnabled(false);
            button.setIcon(disabledIcon);
            currentPriceLabel.setForeground(Color.RED.darker().darker());
        } else {
            button.setEnabled(true);
            button.setIcon(defaultIcon);
            currentPriceLabel.setForeground(Color.YELLOW.darker());
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        // Draw the panel background image
        if (panelImage != null) {
            g.drawImage(panelImage, 0, 0, getWidth(), getHeight(), this);
        }

        super.paintComponent(g); // Call this after custom drawing to paint components on top
    }

    // Create a custom buy button with mouse press/release effects
    private JButton createBuyButton() {
        JButton button = new JButton(defaultIcon);
        button.setPreferredSize(new Dimension(50, 50));
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setContentAreaFilled(false);
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (button.isEnabled()) {
                    button.setIcon(pressedIcon);
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (button.isEnabled()) {
                    button.setIcon(defaultIcon);
                }
            }
        });

        return button;
    }

    // Update the money (from external source)
    public void updateMoney(int newMoney) {
        this.money = newMoney;
        // Refresh the upgrade buttons to reflect the new money amount
        for (JButton button : buyButtons) {
            // Retrieve the current price stored in the button's action listener
            ActionListener[] listeners = button.getActionListeners();
            if (listeners.length > 0 && listeners[0] != null) {
                ActionListener listener = listeners[0];
                // Assuming listener is an inner class that has access to `currentPrice`
                if (listener instanceof BuyButtonListener buyListener) {
                    updateButtonState(button, buyListener.getCurrentPriceLabel(), buyListener.getCurrentPrice());
                }
            }
        }
    }

    // Define a custom ActionListener to store the current price
    private class BuyButtonListener implements ActionListener {
        private final int initialPrice;

        private int currentStat;
        private final JLabel currentStatLabel;

        private int upgradeAmount;
        private final JLabel upgradedStatLabel;

        private int currentLevel = 1;
        private final JLabel currentLevelLabel;

        private int currentPrice;
        private final JLabel currentPriceLabel;

        private final String upgradeName;
        private final long unitId;

        public BuyButtonListener(int initialPrice, int currentStat, int upgradeAmount,
                                 JLabel currentStatLabel, JLabel upgradedStatLabel, JLabel currentLevelLabel,
                                 JLabel currentPriceLabel, String upgradeName, long unitId) {
            this.currentPrice = initialPrice;
            this.currentPriceLabel = currentPriceLabel;
            this.initialPrice = initialPrice;
            this.currentLevelLabel = currentLevelLabel;
            this.upgradeName = upgradeName;
            this.unitId = unitId;
            this.currentStat = currentStat;
            this.currentStatLabel = currentStatLabel;
            this.upgradeAmount = upgradeAmount;
            this.upgradedStatLabel = upgradedStatLabel;
        }

        public JLabel getCurrentPriceLabel() {
            return currentPriceLabel;
        }

        public int getCurrentPrice() {
            return currentPrice;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (money >= currentPrice) {
                currentLevel++;
                currentLevelLabel.setText("Level: " + currentLevel);

                currentStat += upgradeAmount;
                currentStatLabel.setText(String.valueOf(currentStat));
                upgradedStatLabel.setText(String.valueOf(currentStat + upgradeAmount));

                gamePanel.getController().buyUpgrade(unitId, upgradeName, upgradeAmount, currentPrice);

                currentPrice += (int) (initialPrice * 0.25); // Increase price for next upgrade
                currentPriceLabel.setText("$" + currentPrice); // Update price label
            }
        }
    }

    private void initImages() {
        panelImage = Objects.requireNonNull(
                ImageLoader.loadImage("/sprites/hud/containers/upgradeContainer.png"));
        defaultIcon = new ImageIcon(Objects.requireNonNull(
                ImageLoader.loadImage("/sprites/hud/buttons/upgradeButton.png")));
        pressedIcon = new ImageIcon(Objects.requireNonNull(
                ImageLoader.loadImage("/sprites/hud/buttons/upgradeButtonPressed.png")));
        disabledIcon = new ImageIcon(Objects.requireNonNull(
                ImageLoader.loadImage("/sprites/hud/buttons/upgradeButtonDisabled.png")));
    }
}
