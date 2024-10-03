package se.umu.cs.ads.sp.view.panels.lobbypanel;

import javax.swing.*;
import java.awt.*;

public class LobbyPanel extends JPanel {

    private JButton startButton;

    public LobbyPanel() {
        this.setLayout(new BorderLayout());

        this.startButton = new JButton("Start");
        this.add(startButton, BorderLayout.SOUTH);
    }

    public JButton getStartButton() {
        return startButton;
    }

    public void setData(String[][] data) {

    }
}
