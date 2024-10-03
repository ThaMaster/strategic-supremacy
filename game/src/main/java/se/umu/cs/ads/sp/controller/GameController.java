package se.umu.cs.ads.sp.controller;

import se.umu.cs.ads.sp.model.ModelManager;
import se.umu.cs.ads.sp.utils.Position;
import se.umu.cs.ads.sp.utils.enums.Direction;
import se.umu.cs.ads.sp.view.MainFrame;
import se.umu.cs.ads.sp.view.frameComponents.panels.gamepanel.tiles.TileManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class GameController implements ActionListener {

    private final int FPS = 60;

    private Timer timer;
    private MainFrame mainFrame;

    private ModelManager modelManager;
    private TileManager tileManager;

    private Direction cameraPanningDirection = Direction.NONE;

    private String playerName = "";

    public GameController() {
        modelManager = new ModelManager();
        tileManager = new TileManager();
        tileManager.setMap(modelManager.getMap().getModelMap());

        // TODO: Fix the initialization of the tile manager (should not be passed in the constructor of the main frame)
        mainFrame = new MainFrame();
        setActionListeners();
    }

    public void startGame() {
        this.timer = new Timer(1000 / FPS, this);
        mainFrame.showGamePanel(tileManager);
        mainFrame.getGamePanel().setGameController(this);
        mainFrame.getGamePanel().setEntities(modelManager.getMyUnits(), modelManager.getGameEntities());
        mainFrame.getGamePanel().setCollectables(modelManager.getCollectables());
        this.timer.start();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        update();
        render();
    }

    private void update() {
        modelManager.update();
        mainFrame.getGamePanel().updateEntityViews(modelManager.getGameEntities());
        mainFrame.getGamePanel().updateCollectables();

        // Check where to move the camera.
        if (cameraPanningDirection != Direction.NONE) {
            switch (cameraPanningDirection) {
                case NORTH:
                    mainFrame.getGamePanel().moveCamera(0, -10);
                    break;
                case SOUTH:
                    mainFrame.getGamePanel().moveCamera(0, 10);
                    break;
                case WEST:
                    mainFrame.getGamePanel().moveCamera(-10, 0);
                    break;
                case EAST:
                    mainFrame.getGamePanel().moveCamera(10, 0);
                    break;
                default:
                    break;
            }
        }
    }

    private void render() {
        SwingUtilities.invokeLater(() -> {
            mainFrame.getGamePanel().invalidate();
            mainFrame.getGamePanel().repaint();
        });
    }

    public boolean setEntityDestination(Position newPos) {
        return modelManager.setEntityDestination(newPos);
    }

    public void setSelection(Position clickLocation) {
        modelManager.setSelection(clickLocation);
    }

    public void setSelection(long entityId) {
        modelManager.setSelection(entityId);
        Position newCameraPos = modelManager.getGameEntities().get(entityId).getPosition();
        mainFrame.getGamePanel().setCameraWorldPosition(newCameraPos.getX(), newCameraPos.getY());
    }

    public ArrayList<Long> getSelectedUnits() {
        return modelManager.getSelectedUnits();
    }

    public void stopSelectedEntities() {
        modelManager.stopSelectedEntities();
    }

    public void setCameraPanningDirection(Direction dir) {
        this.cameraPanningDirection = dir;
    }

    public void setSelectedUnit(Rectangle area) {
        this.modelManager.setSelectedUnits(area);
    }

    // Action listener things
    //----------------------------------------
    private void setActionListeners() {
        // TODO: Make it possible to press ENTER when starting the application.
        mainFrame.setEnterButtonListener(new EnterButtonListener());
        mainFrame.setJoinButtonListener(new JoinButtonListener());
        mainFrame.setRefreshJoinButtonListener(new RefreshButtonListener());
        mainFrame.setStartButtonListener(new StartButtonListener());
        mainFrame.getBrowseTable().getSelectionModel().addListSelectionListener(e -> {
            // If I do not have this if, it will fire an event when pressing and releasing the mouse
            if (!e.getValueIsAdjusting()) {
                SwingWorker<Object, Object> sw = new SwingWorker<>() {
                    @Override
                    protected String doInBackground() {
                        performGroupSelection();
                        return "";
                    }
                };
                sw.execute();
            }
        });

    }


    public class EnterButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            // Fetch all lobbies from the naming service and set all entries to the browse panel.
            playerName = mainFrame.getInputName();
            if (playerName.isEmpty()) {
                playerName = "Default User";
            }
            mainFrame.setPlayerName(playerName);
            mainFrame.switchPanel("Browse");
        }
    }

    public class RefreshButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            // Fetch and refresh the table full of lobbies.
            String[][] dummyData = {
                    {"432432432", "The best lobby ever", "3/100", "True"},
                    {"gfdspoj90+8342", "The fullest lobby ever", "100/100", "False"},
                    {"gfdspoj90+1234", "Almost full lobby", "95/100", "False"},
                    {"gfdspoj90+5678", "Private Lobby", "80/100", "True"},
                    {"gfdspoj90+4321", "Casual Game Room", "50/100", "False"},
                    {"gfdspoj90+8765", "Fun Times Ahead", "75/100", "False"},
                    {"gfdspoj90+0001", "Ultimate Showdown", "60/100", "True"},
                    {"gfdspoj90+9999", "Chill Zone", "30/100", "False"},
                    {"gfdspoj90+5432", "Competitive Lobby", "90/100", "True"},
                    {"gfdspoj90+1111", "Friendly Match", "20/100", "False"},
                    {"gfdspoj90+2222", "Tournament Room", "100/100", "True"}
            };
            mainFrame.setBrowsePanelData(dummyData);
        }
    }

    public class JoinButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            // Get the id of the lobby and join the lobby.
            mainFrame.switchPanel("Lobby");
        }
    }

    public class StartButtonListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            startGame();
        }
    }

    //----------------------------------------

    private void performGroupSelection() {
        int row = mainFrame.getBrowseTable().getSelectedRow();
        if (row >= 0) {
            String lobbyId = (String) mainFrame.getBrowseTable().getValueAt(row, 0);
            String lobbyName = (String) mainFrame.getBrowseTable().getValueAt(row, 1);
            String players = (String) mainFrame.getBrowseTable().getValueAt(row, 2);
            boolean isPrivate = Boolean.valueOf((String) mainFrame.getBrowseTable().getValueAt(row, 3));
            mainFrame.setJoinEnabled(true);
        } else {
            mainFrame.setJoinEnabled(false);
        }
    }
}
