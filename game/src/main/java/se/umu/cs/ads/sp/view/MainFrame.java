package se.umu.cs.ads.sp.view;

import se.umu.cs.ads.sp.view.frameComponents.dialogs.AboutDialog;
import se.umu.cs.ads.sp.view.frameComponents.dialogs.HelpDialog;
import se.umu.cs.ads.sp.view.frameComponents.panels.browsepanel.BrowsePanel;
import se.umu.cs.ads.sp.view.frameComponents.panels.gamepanel.GamePanel;
import se.umu.cs.ads.sp.view.frameComponents.panels.gamepanel.tiles.TileManager;
import se.umu.cs.ads.sp.view.frameComponents.panels.lobbypanel.LobbyPanel;
import se.umu.cs.ads.sp.view.frameComponents.panels.start.StartPanel;
import se.umu.cs.ads.sp.view.util.UtilView;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class MainFrame extends JFrame {

    private JLabel playerLabel;
    private JMenuBar menuBar;

    private CardLayout cardLayout;
    private JPanel cardPanel;

    private JLayeredPane layeredPane;
    private GamePanel gamePanel;
    private LobbyPanel lobbyPanel;
    private StartPanel startPanel;
    private BrowsePanel browsePanel;

    public MainFrame() {
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setResizable(false);
        this.setTitle("Strategic Supremacy");
        // Helps with performance
        RepaintManager.currentManager(this).setDoubleBufferingEnabled(true);

        menuBar = setupMenuBar();
        this.setJMenuBar(menuBar);

        setupRegularPanels();
        switchPanel("Start");

        this.pack();
        this.setLocationRelativeTo(null);
        this.setVisible(true);
    }

    private void setupRegularPanels() {
        cardLayout = new CardLayout() {
        };

        // Overrides the panel to get the size of the currently selected panel.
        cardPanel = new JPanel(cardLayout) {
            @Override
            public Dimension getPreferredSize() {
                // Get the preferred size of the currently visible card
                Component visibleCard = getVisibleCard(this);
                return visibleCard != null ? visibleCard.getPreferredSize() : super.getPreferredSize();
            }

            private Component getVisibleCard(JPanel panel) {
                for (Component comp : panel.getComponents()) {
                    if (comp.isVisible()) {
                        return comp;
                    }
                }
                return null; // Fallback in case no component is visible
            }
        };

        lobbyPanel = new LobbyPanel();
        cardPanel.add(lobbyPanel, "Lobby");
        browsePanel = new BrowsePanel();
        cardPanel.add(browsePanel, "Browse");
        startPanel = new StartPanel();
        cardPanel.add(startPanel, "Start");
        this.setContentPane(cardPanel);
    }

    public GamePanel getGamePanel() {
        return this.gamePanel;
    }

    public void showGamePanel(TileManager tm) {
        this.menuBar.setVisible(false);
        layeredPane = new JLayeredPane();
        // Add game panel to the layered pane
        gamePanel = new GamePanel(tm);
        gamePanel.setBounds(0, 0, UtilView.screenWidth, UtilView.screenHeight);
        layeredPane.add(gamePanel, JLayeredPane.DEFAULT_LAYER);
        layeredPane.setPreferredSize(new Dimension(UtilView.screenWidth, UtilView.screenHeight));
        cardPanel.add(layeredPane, "Game");

        switchPanel("Game");
    }

    private JMenuBar setupMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        JMenu fileMenu = new JMenu("Game");
        JMenuItem settingsItem = new JMenuItem("Settings");
        JMenuItem exitItem = new JMenuItem("Exit");
        exitItem.addActionListener(e -> System.exit(0));
        fileMenu.add(settingsItem);
        fileMenu.add(exitItem);

        JMenu otherMenu = new JMenu("Other");
        JCheckBoxMenuItem enableDebugItem = new JCheckBoxMenuItem("Enable Debugger");
        JMenuItem helpItem = new JMenuItem("Help");
        helpItem.addActionListener(e -> new HelpDialog());
        JMenuItem aboutItem = new JMenuItem("About");
        aboutItem.addActionListener(e -> new AboutDialog());

        otherMenu.add(enableDebugItem);
        otherMenu.add(helpItem);
        otherMenu.add(aboutItem);

        JPanel playerLabelPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        playerLabel = new JLabel("");
        playerLabelPanel.add(playerLabel);

        menuBar.add(fileMenu);
        menuBar.add(otherMenu);
        menuBar.add(Box.createHorizontalGlue());
        menuBar.add(playerLabelPanel);
        return menuBar;
    }

    public void switchPanel(String panelName) {
        menuBar.setVisible(!panelName.equals("Start") && !panelName.equals("Game"));

        cardLayout.show(cardPanel, panelName);
        cardPanel.revalidate();
        cardPanel.repaint();
        this.pack();
        this.setLocationRelativeTo(null);
    }

    public void setBrowsePanelData(String data[][]) {
        browsePanel.setData(data);
    }

    public void setLobbyPanelData(String data[][]) {
        lobbyPanel.setData(data);
    }

    public void setPlayerName(String name) {
        this.playerLabel.setText(name);
    }

    public String getInputName() {
        return startPanel.getNameInput();
    }

    public JTable getBrowseTable() {
        return browsePanel.getBrowseTable();
    }

    public void setJoinEnabled(boolean bool) {
        browsePanel.setJoinEnabled(bool);
    }

    //----------------------------------------
    public void setEnterButtonListener(ActionListener actionListener) {
        this.startPanel.getEnterButton().addActionListener(actionListener);
    }

    public void setJoinButtonListener(ActionListener actionListener) {
        this.browsePanel.getJoinButton().addActionListener(actionListener);
    }

    public void setRefreshJoinButtonListener(ActionListener actionListener) {
        this.browsePanel.getRefreshButton().addActionListener(actionListener);
    }

    public void setStartButtonListener(ActionListener actionListener) {
        this.lobbyPanel.getStartButton().addActionListener(actionListener);
    }
    //----------------------------------------

}
