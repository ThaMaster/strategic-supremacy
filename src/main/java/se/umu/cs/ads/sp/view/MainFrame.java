package se.umu.cs.ads.sp.view;

import jdk.jshell.execution.Util;
import se.umu.cs.ads.sp.view.panels.gamepanel.GamePanel;
import se.umu.cs.ads.sp.view.panels.gamepanel.tiles.TileManager;
import se.umu.cs.ads.sp.view.util.UtilView;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {
    private JLayeredPane layeredPane;

    private GamePanel gamePanel;

    public MainFrame(TileManager tm) {
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setResizable(false);
        this.setTitle("Strategic Supremacy");
        // Helps with performance
        RepaintManager.currentManager(this).setDoubleBufferingEnabled(true);
        this.gamePanel = new GamePanel(tm);

        setupFrame();

        this.pack();
        this.setSize(new Dimension(UtilView.screenWidth, UtilView.screenHeight));
        this.setLocationRelativeTo(null);
        this.setVisible(true);
    }

    private void setupFrame() {
        layeredPane = new JLayeredPane();
        this.setContentPane(layeredPane);

        // Add game panel to the layered pane
        gamePanel.setBounds(0, 0, UtilView.screenWidth, UtilView.screenHeight);
        layeredPane.add(gamePanel, JLayeredPane.DEFAULT_LAYER);

        // Add additional panels below...
        // layeredPane.add(anotherPanel, JLayeredPane.PALETTE_LAYER);
    }

    public GamePanel getGamePanel() {
        return this.gamePanel;
    }
}
