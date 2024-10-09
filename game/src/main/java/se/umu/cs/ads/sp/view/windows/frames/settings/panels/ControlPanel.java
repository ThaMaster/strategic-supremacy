package se.umu.cs.ads.sp.view.windows.frames.settings.panels;

import javax.swing.*;

public class ControlPanel extends JPanel {

    private boolean settingsChanged = false;

    public ControlPanel() {
        this.add(new JLabel("CREATE CONTROL SETTINGS HERE!"));
    }

    public boolean hasSettingsChanged() {
        return settingsChanged;
    }
}
