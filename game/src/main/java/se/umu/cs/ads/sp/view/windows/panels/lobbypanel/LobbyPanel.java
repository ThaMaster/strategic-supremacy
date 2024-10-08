package se.umu.cs.ads.sp.view.windows.panels.lobbypanel;

import se.umu.cs.ads.sp.view.util.StyleConstants;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.text.Style;
import java.awt.*;
import java.awt.image.BufferedImage;

public class LobbyPanel extends JPanel {

    private JButton startButton;
    private JButton leaveButton;
    private JLabel lobbyLabel;
    private PlayerPanel playerPanel;
    private MapPanel mapPanel;

    public LobbyPanel() {
        this.setLayout(new BorderLayout());

        lobbyLabel = new JLabel("");
        lobbyLabel.setFont(StyleConstants.TITLE_FONT);
        playerPanel = new PlayerPanel();
        mapPanel = new MapPanel();

        this.add(lobbyLabel, BorderLayout.NORTH);
        this.add(playerPanel, BorderLayout.WEST);
        this.add(mapPanel, BorderLayout.EAST);
        this.add(createButtonPanel(), BorderLayout.SOUTH);
    }

    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel(new FlowLayout());
        startButton = new JButton("Start");
        leaveButton = new JButton("Leave");
        buttonPanel.add(startButton);
        buttonPanel.add(leaveButton);
        return buttonPanel;
    }

    public void setLobbyName(String name) {
        this.lobbyLabel.setText(name);
    }

    public JButton getStartButton() {
        return startButton;
    }

    public JButton getLeaveButton() {
        return leaveButton;
    }

    public PlayerPanel getPlayerPanel() {
        return playerPanel;
    }

    public void setMapPreview(BufferedImage map) {
        this.mapPanel.setMapPreview(map);
        this.revalidate();
        this.repaint();
    }
}
