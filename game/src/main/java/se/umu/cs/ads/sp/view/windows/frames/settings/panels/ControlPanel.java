package se.umu.cs.ads.sp.view.windows.frames.settings.panels;

import javax.swing.*;

public class ControlPanel extends JPanel {

    private JButton applyButton;

    public ControlPanel(JButton applyButton) {
        this.add(new JLabel("CREATE CONTROL SETTINGS HERE!"));
        this.applyButton = applyButton;
    }
}
