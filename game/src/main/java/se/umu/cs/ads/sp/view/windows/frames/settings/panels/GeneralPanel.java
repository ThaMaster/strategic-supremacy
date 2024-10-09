package se.umu.cs.ads.sp.view.windows.frames.settings.panels;

import javax.swing.*;

public class GeneralPanel extends JPanel {
    private boolean settingsChanged = false;

    public GeneralPanel() {
        this.add(new JLabel("CREATE GENERAL SETTINGS HERE!!"));
    }

    public boolean hasSettingsChanged() {
        return settingsChanged;
    }
}
