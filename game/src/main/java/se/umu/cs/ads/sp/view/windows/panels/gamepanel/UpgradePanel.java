package se.umu.cs.ads.sp.view.windows.panels.gamepanel;

import se.umu.cs.ads.sp.utils.enums.UnitType;
import se.umu.cs.ads.sp.view.util.ImageLoader;

import javax.swing.*;
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

    private int upgradePadding = 20; // Padding between upgrades

    public UpgradePanel(int width, int height) {
        this.setLayout(null); // No layout manager, manually setting bounds
        this.setOpaque(false); // HUD is transparent, overlays game panel
        setPreferredSize(new Dimension(width, height)); // Set preferred size

        panelImage = ImageLoader.loadImage("/sprites/hud/containers/upgradeContainer.png");

        defaultIcon = new ImageIcon(Objects.requireNonNull(
                ImageLoader.loadImage("/sprites/hud/buttons/upgradeButton.png")));
        pressedIcon = new ImageIcon(Objects.requireNonNull(
                ImageLoader.loadImage("/sprites/hud/buttons/upgradeButtonPressed.png")));
        disabledIcon = new ImageIcon(Objects.requireNonNull(
                ImageLoader.loadImage("/sprites/hud/buttons/upgradeButtonDisabled.png")));

        this.setVisible(false);
    }

    // Dynamically create upgrade columns for each unit
    public void setUpgradeMenu(ArrayList<Long> unitIds, ArrayList<UnitType> types) {
        int upgradePanelWidth;
        int upgradePanelHeight;

        for (int i = 0; i < unitIds.size(); i++) {
            JPanel upgradePanel = createUpgradeColumn(unitIds.get(i), types.get(i));
            upgradePanelWidth = upgradePanel.getPreferredSize().width;
            upgradePanelHeight = upgradePanel.getPreferredSize().height;

            // Set bounds for the upgrade panel and add it
            upgradePanel.setBounds(
                    i * (upgradePanelWidth + upgradePadding), // Position columns side by side
                    (int) this.getPreferredSize().getHeight() / 2 - upgradePanelHeight / 2,
                    upgradePanelWidth,
                    upgradePanelHeight
            );
            this.add(upgradePanel); // Add upgrade panel to the main panel
        }
    }

    // Create an upgrade column for a specific unit
    private JPanel createUpgradeColumn(long unitId, UnitType type) {
        JPanel column = new JPanel();
        column.setLayout(new BoxLayout(column, BoxLayout.PAGE_AXIS));
        column.setOpaque(false);

        // Load unit image
        ImageIcon unitIcon = new ImageIcon(ImageLoader.loadUnitIcon(type));
        JLabel unitImage = new JLabel(unitIcon);

        // Create upgrade list (speed, attack, max hp) for the unit
        JPanel upgradeList = createUpgradeList(unitId);

        // Add components to the column panel
        column.add(unitImage);
        column.add(upgradeList);

        return column;
    }

    // Create the list of upgrades (speed, attack, max hp) for each unit
    private JPanel createUpgradeList(long unitId) {
        JPanel upgradeListPanel = new JPanel();
        upgradeListPanel.setOpaque(false);
        upgradeListPanel.setLayout(new BoxLayout(upgradeListPanel, BoxLayout.PAGE_AXIS));

        // Add speed, attack, and max hp upgrades
        upgradeListPanel.add(createUpgradeRow("Speed", 10, 1, unitId));
        upgradeListPanel.add(createUpgradeRow("Attack", 15, 1, unitId));
        upgradeListPanel.add(createUpgradeRow("Max HP", 100, 1, unitId));

        return upgradeListPanel;
    }

    // Create an individual upgrade row with a dynamic label and button
    private JPanel createUpgradeRow(String upgradeName, int currentStat, int upgradeAmount, long unitId) {
        JPanel upgradeRowPanel = new JPanel(new FlowLayout());
        upgradeRowPanel.setOpaque(false);

        JLabel nrUpgraded = new JLabel("1");
        upgradeRowPanel.add(nrUpgraded);

        JPanel middlePanel = new JPanel();
        middlePanel.setOpaque(false);
        middlePanel.setLayout(new BoxLayout(middlePanel, BoxLayout.Y_AXIS)); // Vertical layout for the middle panel

        // Upgrade label (e.g., "Speed", "Attack", "Max HP")
        JPanel upgradeLabelPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        upgradeLabelPanel.setOpaque(false);
        JLabel upgradeLabel = new JLabel(upgradeName);
        upgradeLabelPanel.add(upgradeLabel);

        // Stat panel to display the current stat and upgrade amount
        JPanel statPanel = new JPanel();
        statPanel.setOpaque(false);
        statPanel.setLayout(new FlowLayout(FlowLayout.CENTER)); // Horizontal layout for stats, but inside the Y-axis layout

        JLabel currentStatLabel = new JLabel(String.valueOf(currentStat));
        JLabel upgradeAmountLabel = new JLabel("+" + upgradeAmount);

        statPanel.add(currentStatLabel);
        statPanel.add(upgradeAmountLabel);

        // Add the upgrade name above the stat panel in the middle panel
        middlePanel.add(upgradeLabelPanel);
        middlePanel.add(statPanel);

        // Add components to the upgrade row panel
        JButton buyButton = createBuyButton();
        buyButton.addActionListener(new ActionListener() {
            private int buyAmount = 1;

            @Override
            public void actionPerformed(ActionEvent e) {
                buyAmount++;
                nrUpgraded.setText(String.valueOf(buyAmount));
                System.out.println("UPGRADE " + upgradeName + " Amount: " + upgradeAmount + " for Unit with Id " + unitId);

            }
        });
        upgradeRowPanel.add(middlePanel); // Middle panel contains the label and stats
        upgradeRowPanel.add(buyButton);

        return upgradeRowPanel;
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
                button.setIcon(pressedIcon);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                button.setIcon(defaultIcon);
            }
        });

        return button;
    }
}