package se.umu.cs.ads.sp.view.frameComponents.panels.lobbypanel;

import javax.swing.*;
import java.awt.*;

public class LobbyPanel extends JPanel {

    private JButton startButton;

    private PlayerPanel playerPanel;
    private MapPanel mapPanel;

    public LobbyPanel() {
        this.setLayout(new BorderLayout());

        playerPanel = new PlayerPanel();
        mapPanel = new MapPanel();
        this.startButton = new JButton("Start");

        this.add(playerPanel, BorderLayout.WEST);
        this.add(mapPanel, BorderLayout.EAST);
        this.add(startButton, BorderLayout.SOUTH);
    }

    public JButton getStartButton() {
        return startButton;
    }

    public void setData(String[][] data) {

    }
}
