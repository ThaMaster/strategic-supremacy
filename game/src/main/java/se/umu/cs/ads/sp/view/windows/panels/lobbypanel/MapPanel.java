package se.umu.cs.ads.sp.view.frameComponents.panels.lobbypanel;

import javax.swing.*;
import java.awt.*;

public class MapPanel extends JPanel {

    public MapPanel() {
        this.setLayout(new BorderLayout());
        JLabel mapLabel = new JLabel("Map");
        this.add(mapLabel, BorderLayout.CENTER);
    }
}
