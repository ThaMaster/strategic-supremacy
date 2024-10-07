package se.umu.cs.ads.sp.controller;

import se.umu.cs.ads.ns.app.Lobby;
import se.umu.cs.ads.ns.app.User;
import se.umu.cs.ads.ns.util.Util;
import se.umu.cs.ads.sp.model.ModelManager;
import se.umu.cs.ads.sp.model.communication.ComHandler;
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
    private User player;
    private ComHandler comHandler;

    public GameController() {
        modelManager = new ModelManager();
        tileManager = new TileManager();
        tileManager.setMap(modelManager.getMap().getModelMap());

        // TODO: Fix the initialization of the tile manager (should not be passed in the constructor of the main frame)
        mainFrame = new MainFrame();
        setActionListeners();
        comHandler = new ComHandler(this);


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
        mainFrame.setCreateLobbyListener(new CreateButtonListener());
        mainFrame.getBrowseTable().getSelectionModel().addListSelectionListener(e -> {
            // If I do not have this if, it will fire an event when pressing and releasing the mouse
            if (!e.getValueIsAdjusting()) {
                SwingWorker<Object, Object> sw = new SwingWorker<>() {
                    @Override
                    protected String doInBackground() {
                        int row = mainFrame.getBrowseTable().getSelectedRow();
                        mainFrame.setJoinEnabled(row >= 0);
                        return "";
                    }
                };
                sw.execute();
            }
        });
    }

    public class CreateButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String lobbyName = mainFrame.getCreateLobbyFrame().getLobbyNameField().getText();
            int maxPlayers = mainFrame.getCreateLobbyFrame().getMaxPlayerValue();
            comHandler.createLobby(GameController.this.player, lobbyName, maxPlayers);
            mainFrame.getCreateLobbyFrame().showFrame(false);
            fetchLobbies();
        }
    }

    public class EnterButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            // Fetch all lobbies from the naming service and set all entries to the browse panel.
            String inputName = mainFrame.getInputName();
            if (inputName.isEmpty()) {
                inputName = "Default User";
            }
            mainFrame.setPlayerName(inputName);
            GameController.this.player = new User(Util.generateId(), inputName, Util.getLocalIP(), Util.getFreePort());
            mainFrame.switchPanel("Browse");
            fetchLobbies();
        }
    }

    public class RefreshButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            // Fetch and refresh the table full of lobbies.
            fetchLobbies();
        }
    }

    public class JoinButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            // Get the id of the lobby and join the lobby.
            int selectedRow = mainFrame.getBrowseTable().getSelectedRow();
            String lobbyId = (String) mainFrame.getBrowseTable().getValueAt(selectedRow, 0);
            comHandler.fetchPlayersFromLobby(Long.valueOf(lobbyId), GameController.this.player);
            mainFrame.switchPanel("Lobby");
            //mainFrame.setLobbyName
        }
    }

    public void updateLobbyPage(Lobby lobby, int selectedMap) {
        String[][] lobbyData = new String[lobby.users.size()][];
        for (int i = 0; i < lobby.users.size(); i++) {
            lobbyData[i] = new String[]{
                    String.valueOf(lobby.users.get(i).id),
                    lobby.users.get(i).username,
            };
        }
        mainFrame.setLobbyData(lobbyData);
    }

    public class StartButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            startGame();
        }
    }

    //----------------------------------------

    //------ALL-COMMUNICATION-FUNCTIONS------//
    private void fetchLobbies() {
        comHandler.fetchLobbies().thenAccept(lobbies -> {
            SwingUtilities.invokeLater(() -> {
                String[][] lobbyData = new String[lobbies.size()][];
                for (int i = 0; i < lobbies.size(); i++) {
                    lobbyData[i] = new String[]{
                            String.valueOf(lobbies.get(i).id),
                            lobbies.get(i).name,
                            (lobbies.get(i).currentPlayers) + "/" + (lobbies.get(i).maxPlayers)
                    };
                }
                mainFrame.setBrowsePanelData(lobbyData);
            });
        });
    }

    //---------------------------------------//
}
