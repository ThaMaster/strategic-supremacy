package se.umu.cs.ads.sp.view.windows;

import se.umu.cs.ads.sp.util.AppSettings;
import se.umu.cs.ads.sp.view.util.UtilView;
import se.umu.cs.ads.sp.view.windows.dialogs.AboutDialog;
import se.umu.cs.ads.sp.view.windows.dialogs.HelpDialog;
import se.umu.cs.ads.sp.view.windows.frames.CreateLobbyFrame;
import se.umu.cs.ads.sp.view.windows.frames.GameEndFrame;
import se.umu.cs.ads.sp.view.windows.frames.QuitGameFrame;
import se.umu.cs.ads.sp.view.windows.frames.settings.SettingsFrame;
import se.umu.cs.ads.sp.view.windows.panels.browsepanel.BrowsePanel;
import se.umu.cs.ads.sp.view.windows.panels.gamepanel.GamePanel;
import se.umu.cs.ads.sp.view.windows.panels.gamepanel.HUDPanel;
import se.umu.cs.ads.sp.view.windows.panels.gamepanel.map.TileManager;
import se.umu.cs.ads.sp.view.windows.panels.lobbypanel.LobbyPanel;
import se.umu.cs.ads.sp.view.windows.panels.start.StartPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

public class MainFrame extends JFrame {

    private CreateLobbyFrame createLobbyFrame;
    private SettingsFrame settingsFrame;
    private QuitGameFrame quitFrame;
    private GameEndFrame gameEndFrame;
    private JLabel playerLabel;
    private JMenuBar menuBar;

    private CardLayout cardLayout;
    private JPanel cardPanel;

    private JLayeredPane layeredPane;
    private GamePanel gamePanel;
    private HUDPanel hudPanel;
    private LobbyPanel lobbyPanel;
    private StartPanel startPanel;
    private BrowsePanel browsePanel;

    public MainFrame() {
        if (UtilView.DARK_MODE) {
            UtilView.setDarkMode();
        } else {
            UtilView.setLightMode();
        }
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setTitle("Strategic Supremacy");
        createLobbyFrame = new CreateLobbyFrame();
        settingsFrame = new SettingsFrame(this);
        quitFrame = new QuitGameFrame();
        gameEndFrame = new GameEndFrame();

        // Helps with performance
        RepaintManager.currentManager(this).setDoubleBufferingEnabled(true);

        menuBar = setupMenuBar();
        this.setJMenuBar(menuBar);

        setupRegularPanels();
        switchPanel("Start");

        this.pack();
        this.setLocationRelativeTo(null);
        this.setVisible(true);
        this.setResizable(false);
    }

    private void setupRegularPanels() {
        cardLayout = new CardLayout();

        // Overrides the panel to get the size of the currently selected panel.
        cardPanel = new JPanel(cardLayout) {
            @Override
            public Dimension getPreferredSize() {
                // Get the preferred size of the currently visible card
                Component visibleCard = getVisibleCard(this);
                return (visibleCard != null) ? visibleCard.getPreferredSize() : super.getPreferredSize();
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
        browsePanel.getCreateButton().addActionListener(e -> createLobbyFrame.showFrame(true));
        browsePanel.getBackButton().addActionListener(e -> switchPanel("Start"));
        cardPanel.add(browsePanel, "Browse");
        startPanel = new StartPanel();
        cardPanel.add(startPanel, "Start");
        this.add(cardPanel);
    }

    public GamePanel getGamePanel() {
        return this.gamePanel;
    }

    public void showGamePanel(TileManager tm) {
        this.menuBar.setVisible(false);
        layeredPane = new JLayeredPane();
        // Add game panel to the layered pane
        gamePanel = new GamePanel(tm);
        layeredPane.add(gamePanel, JLayeredPane.DEFAULT_LAYER);
        // Add palette layer that is the UI here!
        hudPanel = new HUDPanel(gamePanel);
        layeredPane.add(hudPanel, JLayeredPane.PALETTE_LAYER);

        // Add a MouseListener to the layeredPane to allow clicks to pass through non-interactive areas
        layeredPane.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mousePressed(java.awt.event.MouseEvent e) {
                // Check if the mouse event is over a UI component (like a button)
                Component clickedComponent = SwingUtilities.getDeepestComponentAt(hudPanel, e.getX(), e.getY());

                // If it's null, let the game panel handle it
                if (clickedComponent == null || clickedComponent == hudPanel) {
                    gamePanel.dispatchEvent(e); // Forward the event to the game panel
                }
            }
        });

        layeredPane.setPreferredSize(new Dimension(UtilView.screenWidth, UtilView.screenHeight));
        cardPanel.add(layeredPane, "Game");
        switchPanel("Game");
    }

    private JMenuBar setupMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        JMenu fileMenu = new JMenu("Game");
        JMenuItem settingsItem = new JMenuItem("Settings");
        settingsItem.addActionListener(e -> settingsFrame.showFrame(true));
        JMenuItem exitItem = new JMenuItem("Exit");
        exitItem.addActionListener(e -> System.exit(0));
        fileMenu.add(settingsItem);
        fileMenu.add(exitItem);

        JMenu otherMenu = new JMenu("Other");
        JCheckBoxMenuItem enableDebugItem = new JCheckBoxMenuItem("Enable Debugger");
        enableDebugItem.setState(AppSettings.DEBUG);
        enableDebugItem.addActionListener(e -> AppSettings.DEBUG = enableDebugItem.isSelected());
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
        this.setResizable(true);
        menuBar.setVisible(!panelName.equals("Start") && !panelName.equals("Game"));
        cardLayout.show(cardPanel, panelName);
        cardPanel.revalidate();
        cardPanel.repaint();
        this.pack();
        this.setLocationRelativeTo(null);
        this.setResizable(false);
    }

    public void setBrowsePanelData(String data[][]) {
        browsePanel.setData(data);
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

    public void setLobbyData(String data[][], String name, BufferedImage mapPreview,
                             String selectedMap, boolean isLeader, int currentPlayers, int maxPlayers) {
        lobbyPanel.getPlayerPanel().setData(data);
        lobbyPanel.setLobbyName(name);
        lobbyPanel.setMapPreview(mapPreview, selectedMap);
        lobbyPanel.showStartButton(isLeader);
        lobbyPanel.getPlayerPanel().setPlayerAmount(currentPlayers, maxPlayers);
    }

    public LobbyPanel getLobbyPanel() {
        return lobbyPanel;
    }

    public void setJoinEnabled(boolean bool) {
        browsePanel.setJoinEnabled(bool);
    }

    public CreateLobbyFrame getCreateLobbyFrame() {
        return createLobbyFrame;
    }

    public QuitGameFrame getQuitFrame() {
        return quitFrame;
    }

    public void setEndGameContent(boolean winner, String endText) {
        gameEndFrame.setContent(winner, endText);
    }

    public void showEndGameFrame(boolean bool) {
        gameEndFrame.showFrame(bool);
    }

    //----------------------------------------
    public void setEnterButtonListener(ActionListener actionListener) {
        this.startPanel.getEnterButton().addActionListener(actionListener);
        this.startPanel.getRootPane().setDefaultButton(startPanel.getEnterButton());
    }

    public void setJoinButtonListener(ActionListener actionListener) {
        this.browsePanel.getJoinButton().addActionListener(actionListener);
    }

    public void setRefreshJoinButtonListener(ActionListener actionListener) {
        this.browsePanel.getRefreshButton().addActionListener(actionListener);
    }

    public void setCreateLobbyListener(ActionListener actionListener) {
        this.createLobbyFrame.getCreateButton().addActionListener(actionListener);
    }

    public void setStartButtonListener(ActionListener actionListener) {
        this.lobbyPanel.getStartButton().addActionListener(actionListener);

    }

    public void setLeaveButtonListener(ActionListener actionListener) {
        this.lobbyPanel.getLeaveButton().addActionListener(actionListener);
    }

    public void setQuitButtonListener(ActionListener actionListener) {
        this.quitFrame.getQuitButton().addActionListener(actionListener);
    }

    public void setFinishGameListener(ActionListener actionListener) {
        this.gameEndFrame.getQuitButton().addActionListener(actionListener);
        this.gameEndFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
    }

    //----------------------------------------

    public HUDPanel getHudPanel() {
        return hudPanel;
    }
}