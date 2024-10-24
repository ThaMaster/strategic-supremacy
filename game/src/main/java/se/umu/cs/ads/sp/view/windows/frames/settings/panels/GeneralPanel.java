package se.umu.cs.ads.sp.view.windows.frames.settings.panels;

import se.umu.cs.ads.sp.view.util.UtilView;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;

public class GeneralPanel extends JPanel {

    private JButton applyButton;
    private JCheckBox fullscreenCheckBox;
    private JComboBox<String> resolutionComboBox;
    private JCheckBox darkModeCheckBox;

    public GeneralPanel(JButton applyButton, boolean fullscreenSupported) {
        this.applyButton = applyButton;
        this.setBorder(new LineBorder(Color.BLACK, 2, true));
        this.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10); // Add padding

        // Add label
        gbc.gridx = 0;
        gbc.gridy = 0;
        this.add(new JLabel("CREATE GENERAL SETTINGS HERE!!"), gbc);

        // Add fullscreen checkbox
        fullscreenCheckBox = new JCheckBox("Fullscreen");
        fullscreenCheckBox.addActionListener(e -> {
            // Enable/disable resolution combo box based on checkbox state
            applyButton.setEnabled(true);
            resolutionComboBox.setEnabled(!fullscreenCheckBox.isSelected());
        });
        gbc.gridx = 0;
        gbc.gridy = 1;

        if (!fullscreenSupported) fullscreenCheckBox.setEnabled(false);

        this.add(fullscreenCheckBox, gbc);
        // Add resolution combo box
        String[] resolutions = {"1920x1080", "1280x720", "800x600"};
        resolutionComboBox = new JComboBox<>(resolutions);
        resolutionComboBox.setEnabled(true); // Enable by default
        resolutionComboBox.setSelectedIndex(1);
        resolutionComboBox.addActionListener(e -> {
            applyButton.setEnabled(true);
        });
        gbc.gridx = 1;
        gbc.gridy = 1;
        this.add(resolutionComboBox, gbc);

        darkModeCheckBox = new JCheckBox("Enable Dark Mode");
        darkModeCheckBox.setSelected(UtilView.DARK_MODE);
        darkModeCheckBox.addActionListener(e -> {
            applyButton.setEnabled(true);
            UtilView.DARK_MODE = darkModeCheckBox.isSelected();
        });
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        this.add(darkModeCheckBox, gbc);
    }

    public String getResolution() {
        return (String) resolutionComboBox.getSelectedItem();
    }

    public boolean isFullscreen() {
        return fullscreenCheckBox.isSelected();
    }
}
