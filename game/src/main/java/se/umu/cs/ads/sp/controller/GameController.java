package se.umu.cs.ads.sp.controller;

import se.umu.cs.ads.ns.app.Lobby;
import se.umu.cs.ads.ns.app.User;
import se.umu.cs.ads.ns.util.Util;
import se.umu.cs.ads.sp.model.ModelManager;
import se.umu.cs.ads.sp.model.communication.dto.StartGameRequest;
import se.umu.cs.ads.sp.model.objects.entities.Entity;
import se.umu.cs.ads.sp.utils.Position;
import se.umu.cs.ads.sp.utils.enums.Direction;
import se.umu.cs.ads.sp.view.util.UtilView;
import se.umu.cs.ads.sp.view.windows.MainFrame;
import se.umu.cs.ads.sp.view.windows.panels.gamepanel.map.MiniMap;
import se.umu.cs.ads.sp.view.windows.panels.gamepanel.map.TileManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class GameController implements ActionListener {

    private final int FPS = 60;

    private Timer timer;
    private MainFrame mainFrame;

    private ModelManager modelManager;
    private TileManager tileManager;

    private Direction cameraPanningDirection = Direction.NONE;
    private User player;

    public GameController() {
        mainFrame = new MainFrame();
        setActionListeners();
    }

    public void startGame(StartGameRequest req) {
        this.timer = new Timer(1000 / FPS, this);
        modelManager.startGameReq(req);
        mainFrame.showGamePanel(tileManager);
        mainFrame.getGamePanel().setGameController(this);
        mainFrame.getGamePanel().startGame();
        mainFrame.getGamePanel().setEntities(modelManager.getObjectHandler().getMyUnits(),
                modelManager.getObjectHandler().getEnemyUnits());
        mainFrame.getGamePanel().setCollectables(modelManager.getObjectHandler().getCollectables());
        this.timer.start();
    }

    public ModelManager getModelManager() {
        return this.modelManager;
    }

    public void startGame() {
        this.timer = new Timer(1000 / FPS, this);
        mainFrame.showGamePanel(tileManager);
        mainFrame.getGamePanel().setGameController(this);
        mainFrame.getGamePanel().startGame();
        mainFrame.getGamePanel().setEntities(modelManager.getObjectHandler().getMyUnits(),
                modelManager.getObjectHandler().getEnemyUnits());
        mainFrame.getGamePanel().setCollectables(modelManager.getObjectHandler().getCollectables());
        this.timer.start();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        update();
        render();
    }

    private ArrayList<Entity> getAllUnits() {
        ArrayList<Entity> allUnits = new ArrayList<>(modelManager.getObjectHandler().getEnemyUnits().values());
        allUnits.addAll(modelManager.getObjectHandler().getMyUnits().values());
        return allUnits;
    }

    private void update() {
        modelManager.update();
        mainFrame.getGamePanel().updateEntityViews(getAllUnits());
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
        modelManager.getObjectHandler().setSelection(clickLocation);
    }

    public void setSelection(int index) {
        long entityId = new ArrayList<>(modelManager.getObjectHandler().getMyUnits().values()).get(index).getId();
        modelManager.setSelection(entityId);
        Position newCameraPos = modelManager.getObjectHandler().getMyUnits().get(entityId).getPosition();
        mainFrame.getGamePanel().setCameraWorldPosition(newCameraPos.getX(), newCameraPos.getY());
    }

    public ArrayList<Long> getSelectedUnits() {
        return modelManager.getObjectHandler().getSelectedUnitIds();
    }

    public void stopSelectedEntities() {
        modelManager.getObjectHandler().stopSelectedEntities();
    }

    public void setCameraPanningDirection(Direction dir) {
        this.cameraPanningDirection = dir;
    }

    public void setSelectedUnit(Rectangle area) {
        this.modelManager.getObjectHandler().setSelectedUnits(area);
    }

    // Action listener things
    //----------------------------------------
    private void setActionListeners() {
        mainFrame.setEnterButtonListener(new EnterButtonListener());
        mainFrame.setJoinButtonListener(new JoinButtonListener());
        mainFrame.setRefreshJoinButtonListener(new RefreshButtonListener());
        mainFrame.setStartButtonListener(new StartButtonListener());
        mainFrame.setLeaveButtonListener(new LeaveButtonListener());
        mainFrame.setCreateLobbyListener(new CreateButtonListener());
        mainFrame.setQuitButtonListener(new QuitButtonListener());
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
            String selectedMap = mainFrame.getCreateLobbyFrame().getSelectedMap();
            mainFrame.getCreateLobbyFrame().showFrame(false);

            createLobby(lobbyName, maxPlayers, selectedMap);
            mainFrame.getLobbyPanel().showStartButton(true);
            mainFrame.switchPanel("Lobby");
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
            GameController.this.player = new User(inputName, Util.getLocalIP(), Util.getFreePort());
            modelManager = new ModelManager(GameController.this, GameController.this.player);
            tileManager = new TileManager();
            tileManager.setMap(modelManager.getMap().getModelMap());
            System.out.println("[Client] Creating my user " + GameController.this.player.ip + ":" + GameController.this.player.port);
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
            joinLobby(Long.parseLong(lobbyId));
        }
    }

    public class LeaveButtonListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            modelManager.getLobbyHandler().leaveLobby();
            mainFrame.switchPanel("Browse");
            fetchLobbies();
        }
    }

    public class StartButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            modelManager.startGame();
            startGame();
        }
    }

    public class QuitButtonListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            modelManager.leaveOngoingGame();
            mainFrame.getQuitFrame().showFrame(false);
            mainFrame.switchPanel("Browse");
            fetchLobbies();
        }
    }

    //------ALL-COMMUNICATION-FUNCTIONS------//
    private void fetchLobbies() {
        ArrayList<Lobby> lobbies = modelManager.getLobbyHandler().fetchLobbies();
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
    }

    private void createLobby(String lobbyName, int maxPlayers, String selectedMap) {
        modelManager.getLobbyHandler().createLobby(lobbyName, maxPlayers, selectedMap);
        SwingUtilities.invokeLater(() -> {
            updateLobby(modelManager.getLobbyHandler().getLobby());
        });
    }

    private void joinLobby(long lobbyId) {
        Lobby lobby = modelManager.getLobbyHandler().joinLobby(lobbyId);
        if (lobby == null) {
            UtilView.displayWarningMessage(mainFrame, "Failed to join lobby! (Lobby is already full)");
            return;
        }
        SwingUtilities.invokeLater(() -> {
            updateLobby(lobby);
            mainFrame.getLobbyPanel().showStartButton(false);
            mainFrame.switchPanel("Lobby");
        });
    }

    //---------------------------------------//

    public void updateLobby(Lobby updatedLobby) {
        Lobby oldLobby = modelManager.getLobbyHandler().getLobby();
        String[][] playerData = new String[updatedLobby.users.size()][];
        for (int i = 0; i < updatedLobby.users.size(); i++) {
            User currentUser = updatedLobby.users.get(i);
            StringBuilder sBuilder = new StringBuilder();
            if (currentUser.id == updatedLobby.leader.id) {
                sBuilder.append("(Leader) ");
            }
            sBuilder.append(updatedLobby.users.get(i).username);

            playerData[i] = new String[]{
                    String.valueOf(currentUser.id),
                    sBuilder.toString()
            };
        }

        modelManager.loadMap(updatedLobby.selectedMap);
        tileManager.setMap(modelManager.getMap().getModelMap());
        BufferedImage mapPreview = MiniMap.createMinimapPreview(
                tileManager.getViewMap(),
                tileManager.getMapWidth(),
                tileManager.getMapHeight(),
                100,
                100);

        mainFrame.setLobbyData(playerData, updatedLobby.name, mapPreview, updatedLobby.selectedMap,
                true, updatedLobby.currentPlayers, updatedLobby.maxPlayers);

        if (modelManager.hasGameStarted() && (oldLobby.currentPlayers != updatedLobby.currentPlayers)) {
            oldLobby.users.removeAll(updatedLobby.users);
            for (User user : oldLobby.users) {
                UtilView.displayInfoMessage(mainFrame, user.username + " has left the lobby!");
            }
        }
        modelManager.getLobbyHandler().setLobby(updatedLobby);
    }

    public void openQuitWindow() {
        mainFrame.getQuitFrame().showFrame(true);
    }

    public void removeEnemyUnits(long playerId, ArrayList<Long> unitIds) {
        modelManager.getObjectHandler().removeEnemyUnits(unitIds);
        modelManager.getLobbyHandler().removePlayer(playerId);
        mainFrame.getGamePanel().removeEntities(unitIds);
    }
}
