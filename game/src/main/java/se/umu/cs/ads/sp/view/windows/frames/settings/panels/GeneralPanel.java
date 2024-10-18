package se.umu.cs.ads.sp.view.windows.frames.settings.panels;

import javax.swing.*;

public class GeneralPanel extends JPanel {

    private JButton applyButton;

    public GeneralPanel(JButton applyButton) {
        this.applyButton = applyButton;
        this.add(new JLabel("CREATE GENERAL SETTINGS HERE!!"));

    }
}
