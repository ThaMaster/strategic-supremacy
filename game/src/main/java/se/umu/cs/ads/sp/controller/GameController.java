package se.umu.cs.ads.sp.controller;

import se.umu.cs.ads.ns.app.Lobby;
import se.umu.cs.ads.ns.app.User;
import se.umu.cs.ads.sp.model.ModelManager;
import se.umu.cs.ads.sp.model.communication.dto.StartGameRequestDTO;
import se.umu.cs.ads.sp.model.objects.entities.Entity;
import se.umu.cs.ads.sp.model.objects.entities.units.PlayerUnit;
import se.umu.cs.ads.sp.utils.Position;
import se.umu.cs.ads.sp.utils.Utils;
import se.umu.cs.ads.sp.utils.enums.Direction;
import se.umu.cs.ads.sp.view.objects.entities.units.PlayerUnitView;
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

    private Timer updateTimer;

    private Timer gameTimer;

    private MainFrame mainFrame;

    private ModelManager modelManager;
    private TileManager tileManager;

    private Direction cameraPanningDirection = Direction.NONE;
    private User player;

    public GameController() {
        UtilView.changeScreenSize(1600, 800);
        mainFrame = new MainFrame();
        setActionListeners();
        this.updateTimer = new Timer(1000 / FPS, this);
        this.gameTimer = new Timer(700, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                long remainingTime = modelManager.getRoundRemainingTime();
                if(remainingTime <= 0){
                    //Todo next round stuff
                }else{
                    updateHudTimer((int)remainingTime);
                }
            }
        });
    }

    //We got a message/request to start the game
    public void startGame(StartGameRequestDTO req) {
        modelManager.startGameReq(req);
        initializeView();
        updateTimer.start();
        gameTimer.start();
    }

    public ModelManager getModelManager() {
        return this.modelManager;
    }

    //We have started the game
    public void startGame() {
        initializeView();
        updateTimer.start();
        gameTimer.start();
    }

    public void spawnFlag(long id, Position flagPos) {
        mainFrame.getGamePanel().spawnFlag(id, flagPos);
    }

    private void initializeView() {
        mainFrame.showGamePanel(tileManager);
        mainFrame.getGamePanel().setGameController(this);
        mainFrame.getGamePanel().startGame();
        mainFrame.getGamePanel().setEntities(modelManager.getObjectHandler().getMyUnits(),
                modelManager.getObjectHandler().getEnemyUnits());
        mainFrame.getGamePanel().setCollectables(modelManager.getObjectHandler().getCollectables());
        mainFrame.getGamePanel().setEnvironments(modelManager.getObjectHandler().getEnvironments());

        ArrayList<PlayerUnitView> myViews = mainFrame.getGamePanel()
                .getMyUnits(modelManager.getObjectHandler().getMyUnitIds());

        mainFrame.getHudPanel().setUpgradeMenu(myViews);
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
        mainFrame.getGamePanel().updateEnvironments();
        updateHudInfo();
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
        updateHudSelectionInfo();
    }

    public void setSelection(int index) {
        PlayerUnit unit = new ArrayList<>(modelManager.getObjectHandler().getMyUnits().values()).get(index);
        modelManager.setSelection(unit.getId());

        Position newCameraPos = unit.getPosition();
        mainFrame.getGamePanel().setCameraWorldPosition(newCameraPos);

        updateHudSelectionInfo();
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
        updateHudSelectionInfo();
    }

    private void updateHudSelectionInfo() {
        ArrayList<PlayerUnit> selectedUnits = modelManager.getObjectHandler().getSelectedUnits();

        ArrayList<String> unitNames = new ArrayList<>();
        for (PlayerUnit unit : selectedUnits) {
            unitNames.add(unit.getEntityType());
        }
        mainFrame.getHudPanel().updateSelectedUnit(unitNames);
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
            GameController.this.player = new User(inputName, Utils.getLocalIP(), Utils.getFreePort());
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
            updateTimer.stop();
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

    public void updateHudTimer(int remainingTime) {
        mainFrame.getHudPanel().updateTimer(remainingTime / 60, remainingTime % 60);
    }

    private void updateHudInfo() {
        mainFrame.getHudPanel().updateMoney(modelManager.getCurrentGold());
        mainFrame.getHudPanel().updateScore(modelManager.getCurrentPoints());
    }

    public void buyUpgrade(long unitId, String type, int amount, int cost) {
        modelManager.setCurrentGold(modelManager.getCurrentGold() - cost);
        modelManager.getObjectHandler().upgradeUnit(unitId, type, amount);
    }

    public void toggleShopWindow() {
        mainFrame.getHudPanel().toggleUpgradeMenu();
    }
}
